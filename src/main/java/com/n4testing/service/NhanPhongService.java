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

@Service
@RequiredArgsConstructor
public class NhanPhongService {

    private final DatPhongRepository datPhongRepository;
    private final ChiTietDatPhongRepository chiTietDatPhongRepository;
    private final LuuTruRepository luuTruRepository;
    private final PhongRepository phongRepository;

    /**
     * Lấy danh sách phiếu đặt phòng đang ở trạng thái 'Chờ check-in'
     */
    public List<DatPhong> getDanhSachChoNhanPhong() {
        return datPhongRepository.findByTrangThai("Chờ check-in");
    }

    /**
     * Tìm kiếm phiếu đặt phòng theo tên hoặc số điện thoại
     */
    public List<DatPhong> searchBookings(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getDanhSachChoNhanPhong();
        }
        return datPhongRepository.searchBookings(keyword, "Chờ check-in");
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
    public void thucHienNhanPhong(Integer maDatPhong) {
        DatPhong datPhong = datPhongRepository.findById(maDatPhong)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin đặt phòng"));

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
        List<ChiTietDatPhong> chiTiets = chiTietDatPhongRepository.findByDatPhong(datPhong);
        for (ChiTietDatPhong ct : chiTiets) {
            Phong phong = ct.getPhong();
            if (phong != null) {
                phong.setTrangThai("Bận");
                phongRepository.save(phong);
            }
        }
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
}
