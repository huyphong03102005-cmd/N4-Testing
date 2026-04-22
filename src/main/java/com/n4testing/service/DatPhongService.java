package com.n4testing.service;

import com.n4testing.model.ChiTietDatPhong;
import com.n4testing.model.DatPhong;
import com.n4testing.model.KhachHang;
import com.n4testing.model.Phong;
import com.n4testing.repository.ChiTietDatPhongRepository;
import com.n4testing.repository.DatPhongRepository;
import com.n4testing.repository.KhachHangRepository;
import com.n4testing.repository.PhongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class DatPhongService {

    private final KhachHangRepository khachHangRepository;
    private final DatPhongRepository datPhongRepository;
    private final ChiTietDatPhongRepository chiTietDatPhongRepository;
    private final PhongRepository phongRepository;
    private final NotificationService notificationService;

    @Autowired
    public DatPhongService(KhachHangRepository khachHangRepository,
                          DatPhongRepository datPhongRepository,
                          ChiTietDatPhongRepository chiTietDatPhongRepository,
                          PhongRepository phongRepository,
                          NotificationService notificationService) {
        this.khachHangRepository = khachHangRepository;
        this.datPhongRepository = datPhongRepository;
        this.chiTietDatPhongRepository = chiTietDatPhongRepository;
        this.phongRepository = phongRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public DatPhong datPhong(String hoTen, String sdt, String cccd, String tenPhong, 
                             LocalDateTime ngayNhan, LocalDateTime ngayTra, 
                             Integer soNguoiLon, Integer soTreEm,
                             BigDecimal tienCoc, BigDecimal tongThanhToan, String phuongThuc) {
        
        // 1. Tìm hoặc tạo khách hàng
        KhachHang khachHang = khachHangRepository.findByCccd(cccd)
                .orElse(khachHangRepository.findBySdt(sdt).orElse(null));
        
        if (khachHang == null) {
            khachHang = new KhachHang();
            khachHang.setHoTen(hoTen);
            khachHang.setSdt(sdt);
            khachHang.setCccd(cccd);
            khachHang = khachHangRepository.save(khachHang);
        }

        // 2. Tìm phòng
        Phong phong = phongRepository.findByTenPhong(tenPhong)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng: " + tenPhong));

        // 3. Tạo phiếu đặt phòng
        DatPhong datPhong = new DatPhong();
        datPhong.setKhachHang(khachHang);
        datPhong.setTenNguoiDat(hoTen);
        datPhong.setSdtNguoiDat(sdt);
        datPhong.setNgayNhan(ngayNhan);
        datPhong.setNgayTra(ngayTra);
        datPhong.setSoNguoiLon(soNguoiLon);
        datPhong.setSoTreEm(soTreEm);
        datPhong.setSoPhong(tenPhong);
        datPhong.setTienCoc(tienCoc);
        datPhong.setTongThanhToan(tongThanhToan);
        datPhong.setPhuongThucThanhToan(phuongThuc);
        datPhong.setTrangThai("Đã đặt cọc");
        
        datPhong = datPhongRepository.save(datPhong);

        // 5. Lưu chi tiết đặt phòng
        ChiTietDatPhong chiTiet = new ChiTietDatPhong();
        chiTiet.setDatPhong(datPhong);
        chiTiet.setPhong(phong);
        chiTiet.setSoLuongPhong(1);
        chiTietDatPhongRepository.save(chiTiet);

        notificationService.broadcastUpdate();
        return datPhong;
    }
}
