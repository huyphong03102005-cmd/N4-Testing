package com.n4testing.service;

import com.n4testing.model.*;
import com.n4testing.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TraPhongService {

    private final LuuTruRepository luuTruRepository;
    private final SuDungDichVuRepository suDungDichVuRepository;
    private final ThietHaiRepository thietHaiRepository;
    private final HoaDonRepository hoaDonRepository;
    private final ThanhToanRepository thanhToanRepository;
    private final PhongRepository phongRepository;
    private final DatPhongRepository datPhongRepository;
    private final ChiTietDatPhongRepository chiTietDatPhongRepository;
    private final NotificationService notificationService;

    public String getRoomNames(LuuTru luuTru) {
        if (luuTru == null || luuTru.getDatPhong() == null)
            return "N/A";
        List<ChiTietDatPhong> details = chiTietDatPhongRepository.findByDatPhong(luuTru.getDatPhong());
        if (details == null || details.isEmpty()) {
            return luuTru.getDatPhong().getSoPhong(); // Fallback to reference
        }
        return details.stream()
                .filter(d -> d.getPhong() != null)
                .map(d -> d.getPhong().getTenPhong())
                .collect(Collectors.joining(", "));
    }

    public BigDecimal calculateTienPhong(LuuTru luuTru) {
        DatPhong datPhong = luuTru.getDatPhong();
        if (datPhong == null)
            return BigDecimal.ZERO;

        // 1. Try to get price from ChiTietDatPhong (Recommended)
        List<ChiTietDatPhong> chiTiets = chiTietDatPhongRepository.findByDatPhong(datPhong);
        if (chiTiets != null && !chiTiets.isEmpty()) {
            BigDecimal totalFromDetails = BigDecimal.ZERO;
            for (ChiTietDatPhong ct : chiTiets) {
                if (ct.getPhong() != null && ct.getPhong().getGiaPhong() != null) {
                    BigDecimal qty = new BigDecimal(ct.getSoLuongPhong() != null ? ct.getSoLuongPhong() : 1);
                    totalFromDetails = totalFromDetails.add(ct.getPhong().getGiaPhong().multiply(qty));
                }
            }
            if (totalFromDetails.compareTo(BigDecimal.ZERO) > 0) {
                return totalFromDetails;
            }
        }

        // 2. Fallback: Check DatPhong.tongThanhToan
        BigDecimal basePrice = datPhong.getTongThanhToan();
        if (basePrice != null && basePrice.compareTo(BigDecimal.ZERO) > 0) {
            return basePrice;
        }

        // 3. Last Fallback: Use soPhong directly if possible
        if (datPhong.getSoPhong() != null) {
            Optional<Phong> phongOpt = phongRepository.findByTenPhong(datPhong.getSoPhong());
            if (phongOpt.isPresent())
                return phongOpt.get().getGiaPhong();
        }

        return BigDecimal.ZERO;
    }

    /**
     * Lấy chi tiết một bản ghi lưu trú cho frontend rendering
     */
    public Map<String, Object> getStayDetailDto(Integer idLuutru) {
        LuuTru s = luuTruRepository.findById(idLuutru)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin lưu trú"));

        Map<String, Object> map = new java.util.HashMap<>();
        map.put("idLuutru", s.getIdLuutru());
        map.put("datPhong", s.getDatPhong());
        map.put("thoiGianCheckinThucTe", s.getThoiGianCheckinThucTe());
        map.put("thoiGianCheckoutThucTe", s.getThoiGianCheckoutThucTe());
        map.put("soNguoiThucTe", s.getSoNguoiThucTe());
        map.put("suDungDichVuList", s.getSuDungDichVuList());
        map.put("thietHaiList", s.getThietHaiList());
        map.put("calculatedBasePrice", calculateTienPhong(s));
        map.put("actualRoomName", getRoomNames(s));

        return map;
    }

    /**
     * Tính toán tổng tiền hóa đơn dự kiến
     */
    public BigDecimal tinhTongTien(Integer idLuutru) {
        LuuTru luuTru = luuTruRepository.findById(idLuutru)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin lưu trú"));

        // 1. Tiền phòng
        BigDecimal tienPhong = calculateTienPhong(luuTru);

        // 2. Tiền dịch vụ
        List<SuDungDichVu> services = suDungDichVuRepository.findByLuuTru(luuTru);
        BigDecimal tienDichVu = services.stream()
                .map(s -> s.getThanhTien() != null ? s.getThanhTien() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Tiền bồi thường
        List<ThietHai> damages = thietHaiRepository.findByLuuTru(luuTru);
        BigDecimal tienBoiThuong = damages.stream()
                .map(th -> th.getSoTienBoiThuong() != null ? th.getSoTienBoiThuong() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return tienPhong.add(tienDichVu).add(tienBoiThuong);
    }

    /**
     * Thực hiện quy trình trả phòng
     */
    @Transactional
    public HoaDon thucHienTraPhong(Integer idLuutru, String phuongThucThanhToan) {
        LuuTru luuTru = luuTruRepository.findById(idLuutru)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin lưu trú"));

        BigDecimal tongTien = tinhTongTien(idLuutru);

        // 1. Tạo hóa đơn
        HoaDon hoaDon = new HoaDon();
        hoaDon.setTongTien(tongTien);
        hoaDon.setTrangThai("PAID");
        hoaDon.setNgayLap(LocalDateTime.now());
        hoaDon = hoaDonRepository.save(hoaDon);

        // 2. Tạo bản ghi thanh toán
        ThanhToan thanhToan = new ThanhToan();
        thanhToan.setHoaDon(hoaDon);
        thanhToan.setSoTien(tongTien);
        thanhToan.setPhuongThuc(phuongThucThanhToan);
        thanhToan.setTrangThai("SUCCESS");
        thanhToanRepository.save(thanhToan);

        // 3. Cập nhật LuuTru
        luuTru.setThoiGianCheckoutThucTe(LocalDateTime.now());
        luuTruRepository.save(luuTru);

        // 4. Cập nhật DatPhong
        DatPhong datPhong = luuTru.getDatPhong();
        datPhong.setTrangThai("Đã trả");
        datPhongRepository.save(datPhong);

        // 5. Cập nhật trạng thái các phòng liên quan sang 'Trống'
        List<ChiTietDatPhong> chiTiets = chiTietDatPhongRepository.findByDatPhong(datPhong);
        for (ChiTietDatPhong ct : chiTiets) {
            Phong phong = ct.getPhong();
            if (phong != null) {
                phong.setTrangThai("Trống");
                phongRepository.save(phong);
            }
        }

        // 6. Gán hóa đơn cho các dịch vụ đã dùng
        List<SuDungDichVu> services = suDungDichVuRepository.findByLuuTru(luuTru);
        for (SuDungDichVu s : services) {
            s.setHoaDon(hoaDon);
            suDungDichVuRepository.save(s);
        }

        notificationService.broadcastUpdate();
        return hoaDon;
    }
}
