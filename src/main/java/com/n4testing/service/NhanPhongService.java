package com.n4testing.service;

import com.n4testing.model.ChiTietDatPhong;
import com.n4testing.model.DatPhong;
import com.n4testing.model.LuuTru;
import com.n4testing.model.Phong;
import com.n4testing.repository.ChiTietDatPhongRepository;
import com.n4testing.repository.DatPhongRepository;
import com.n4testing.repository.LuuTruRepository;
import com.n4testing.repository.PhongRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.n4testing.dto.RoomInfoDTO;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class NhanPhongService {

    private final DatPhongRepository datPhongRepository;
    private final ChiTietDatPhongRepository chiTietDatPhongRepository;
    private final LuuTruRepository luuTruRepository;
    private final PhongRepository phongRepository;
    private final NotificationService notificationService;

    /**
     * Lấy danh sách các phòng đang 'Bận' kèm thông tin khách hàng và ngày
     * check-in/out
     * Optimized to avoid N+1 query problem.
     */
    public List<RoomInfoDTO> getOccupiedRoomsDetail() {
        // 1. Fetch all occupied rooms - Query 1
        List<Phong> occupiedRooms = phongRepository.findByTrangThai("Bận");

        // 2. Fetch all active stays with their related booking and room details - Query
        // 2
        List<LuuTru> activeStays = luuTruRepository.findActiveStaysWithDetails();

        // 3. Create a map of Room ID to Stay for O(1) lookup
        Map<Integer, LuuTru> roomStayMap = new java.util.HashMap<>();
        for (LuuTru stay : activeStays) {
            DatPhong dp = stay.getDatPhong();
            if (dp != null && dp.getChiTietDatPhongs() != null) {
                for (ChiTietDatPhong detail : dp.getChiTietDatPhongs()) {
                    if (detail.getPhong() != null) {
                        roomStayMap.put(detail.getPhong().getIdPhong(), stay);
                    }
                }
            }
        }

        // 4. Build DTOs efficiently
        List<RoomInfoDTO> dtos = new ArrayList<>();
        for (Phong p : occupiedRooms) {
            RoomInfoDTO dto = RoomInfoDTO.builder()
                    .idPhong(p.getIdPhong())
                    .tenPhong(p.getTenPhong())
                    .loaiPhong(p.getLoaiPhong())
                    .trangThai(p.getTrangThai())
                    .giaPhong(p.getGiaPhong())
                    .floor(deriveFloor(p.getTenPhong()))
                    .build();

            LuuTru s = roomStayMap.get(p.getIdPhong());
            if (s != null) {
                dto.setIdLuutru(s.getIdLuutru());
                dto.setTenKhachHang(s.getDatPhong().getTenNguoiDat());
                dto.setCheckInTime(s.getThoiGianCheckinThucTe());
                dto.setExpectedCheckOutTime(s.getDatPhong().getNgayTra());
            }
            dtos.add(dto);
        }
        return dtos;
    }

    private Integer deriveFloor(String roomName) {
        if (roomName == null || roomName.isEmpty())
            return 1;
        try {
            return Integer.parseInt(roomName.substring(0, 1));
        } catch (Exception e) {
            return 1;
        }
    }

    /**
     * Lấy danh sách phiếu đặt phòng đang ở trạng thái 'Chờ check-in'
     */
    public List<DatPhong> getDanhSachChoNhanPhong() {
        List<DatPhong> list = datPhongRepository.findByTrangThai("Chờ check-in");
        list.addAll(datPhongRepository.findByTrangThai("Đã đặt"));
        return list;
    }

    /**
     * Tìm kiếm phiếu đặt phòng theo tên hoặc số điện thoại
     */
    public List<DatPhong> searchBookings(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getDanhSachChoNhanPhong();
        }
        List<DatPhong> list = datPhongRepository.searchBookings(keyword, "Chờ check-in");
        list.addAll(datPhongRepository.searchBookings(keyword, "Đã đặt"));
        return list;
    }

    /**
     * Lấy danh sách tất cả các phòng để hiển thị sơ đồ
     */
    public List<Phong> getAllPhongs() {
        return phongRepository.findAll();
    }

    /**
     * Thực hiện quy trình nhận phòng
     */
    @Transactional
    public void thucHienNhanPhong(String maDatPhong) {
        DatPhong datPhong = datPhongRepository.findById(maDatPhong)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin đặt phòng"));

        // [BỔ SUNG] Chống Double Check-in
        if ("Đang ở".equals(datPhong.getTrangThai())) {
            throw new RuntimeException("Phiếu này đã được nhận phòng rồi!");
        }

        // [BỔ SUNG] Kiểm tra tất cả phòng có sẵn sàng không
        List<ChiTietDatPhong> chiTiets = chiTietDatPhongRepository.findByDatPhong(datPhong);
        for (ChiTietDatPhong ct : chiTiets) {
            if (ct.getPhong() != null && !"Trống".equals(ct.getPhong().getTrangThai())) {
                throw new RuntimeException("Phòng " + ct.getPhong().getTenPhong() + " hiện không sẵn sàng!");
            }
        }

        // 1. Cập nhật trạng thái phiếu đặt phòng
        datPhong.setTrangThai("Đang ở");
        datPhongRepository.save(datPhong);

        // 2. Tạo bản ghi lưu trú
        LuuTru luuTru = new LuuTru();
        luuTru.setDatPhong(datPhong);
        luuTru.setThoiGianCheckinThucTe(LocalDateTime.now());
        luuTru.setSoNguoiThucTe(datPhong.getTongSoNguoi());
        luuTruRepository.save(luuTru);

        // 3. Cập nhật trạng thái các phòng liên quan sang 'Bận'
        for (ChiTietDatPhong ct : chiTiets) {
            Phong phong = ct.getPhong();
            if (phong != null) {
                phong.setTrangThai("Bận");
                phongRepository.save(phong);
            }
        }
        notificationService.broadcastUpdate();
    }

    public Map<Integer, ChiTietDatPhong> getActiveBookingMap() {
        List<ChiTietDatPhong> activeDetails = chiTietDatPhongRepository.findActiveDetails();
        return activeDetails.stream()
                .filter(ct -> ct.getPhong() != null)
                .collect(Collectors.toMap(
                        ct -> ct.getPhong().getIdPhong(),
                        ct -> ct,
                        (existing, replacement) -> existing // Nếu một phòng có nhiều đặt phòng (hiếm), lấy cái đầu
                ));
    }

    /**
     * Đổi phòng cho một booking đang hoạt động
     */
    @Transactional
    public void changeRoom(Integer currentRoomId, String targetRoomName) {
        // 1. Tìm bản ghi đặt phòng hiện tại của phòng này
        Map<Integer, ChiTietDatPhong> activeMap = getActiveBookingMap();
        ChiTietDatPhong ct = activeMap.get(currentRoomId);
        if (ct == null) {
            throw new RuntimeException("Phòng hiện không có khách hoặc lịch đặt trước để đổi.");
        }

        // 2. Tìm phòng mới
        Phong targetRoom = phongRepository.findByTenPhong(targetRoomName)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng " + targetRoomName));

        if (!"Trống".equals(targetRoom.getTrangThai())) {
            throw new RuntimeException("Phòng " + targetRoomName + " hiện đang bận hoặc bảo trì.");
        }

        // 3. Hoán đổi trạng thái và cập nhật
        Phong currentRoom = ct.getPhong();
        String currentStatus = currentRoom.getTrangThai();

        targetRoom.setTrangThai(currentStatus);
        currentRoom.setTrangThai("Trống");

        phongRepository.save(targetRoom);
        phongRepository.save(currentRoom);

        // 4. Cập nhật chi tiết đặt phòng trỏ sang phòng mới
        ct.setPhong(targetRoom);
        chiTietDatPhongRepository.save(ct);

        // 5. Cập nhật field so_phong trong DatPhong để đồng bộ (nếu có)
        DatPhong dp = ct.getDatPhong();
        if (dp != null) {
            dp.setSoPhong(targetRoomName);
            datPhongRepository.save(dp);
        }

        // 6. Đảm bảo dữ liệu được đẩy xuống DB trước khi kết thúc transaction
        phongRepository.flush();
        chiTietDatPhongRepository.flush();
        datPhongRepository.flush();
    }
}
