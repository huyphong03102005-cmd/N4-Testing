package com.n4testing.controller;

import com.n4testing.model.DatPhong;
import com.n4testing.service.DatPhongService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Controller
@RequestMapping("/dat-phong")
public class DatPhongController {

    private final DatPhongService datPhongService;

    @Autowired
    public DatPhongController(DatPhongService datPhongService) {
        this.datPhongService = datPhongService;
    }

    @PostMapping("/xac-nhan")
    @ResponseBody
    public ResponseEntity<?> xacNhanDatPhong(
            @RequestParam("phong") String tenPhong,
            @RequestParam("hoTen") String hoTen,
            @RequestParam("sdt") String sdt,
            @RequestParam("cccd") String cccd,
            @RequestParam("ngayNhan") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ngayNhan,
            @RequestParam("ngayTra") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime ngayTra,
            @RequestParam("soNguoiLon") Integer soNguoiLon,
            @RequestParam("soTreEm") Integer soTreEm,
            @RequestParam("email") String email,
            @RequestParam("tienCoc") BigDecimal tienCoc,
            @RequestParam("tongThanhToan") BigDecimal tongThanhToan,
            @RequestParam("phuongThuc") String phuongThuc) {
        
        try {
            DatPhong dp = datPhongService.datPhong(hoTen, sdt, cccd, tenPhong, ngayNhan, ngayTra, 
                                                  soNguoiLon, soTreEm, email, tienCoc, tongThanhToan, phuongThuc);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "maDatPhong", dp.getMaDatPhong(),
                "message", "Đặt phòng và thanh toán cọc thành công!"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        }
    }

    @org.springframework.web.bind.annotation.GetMapping("/{maDatPhong}")
    @ResponseBody
    public ResponseEntity<?> getChiTiet(@org.springframework.web.bind.annotation.PathVariable String maDatPhong) {
        DatPhong dp = datPhongService.getDatPhongDetail(maDatPhong);
        if (dp == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(dp);
    }

    @PostMapping("/huy")
    @ResponseBody
    public ResponseEntity<?> huyDatPhong(@RequestParam("maDatPhong") String maDatPhong) {
        try {
            datPhongService.huyDatPhong(maDatPhong);
            return ResponseEntity.ok(Map.of("success", true, "message", "Đã hủy đặt phòng thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
