package com.n4testing.controller;

import com.n4testing.model.HoaDon;
import com.n4testing.model.LuuTru;
import com.n4testing.repository.LuuTruRepository;
import com.n4testing.service.TraPhongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tra-phong")
@RequiredArgsConstructor
public class TraPhongController {

    private final TraPhongService traPhongService;
    private final LuuTruRepository luuTruRepository;

    @GetMapping("/active-stays")
    public ResponseEntity<?> getActiveStays() {
        List<LuuTru> stays = luuTruRepository.findByThoiGianCheckoutThucTeIsNull();
        List<Map<String, Object>> result = stays.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("idLuutru", s.getIdLuutru());
            map.put("datPhong", s.getDatPhong());
            map.put("thoiGianCheckinThucTe", s.getThoiGianCheckinThucTe());
            map.put("thoiGianCheckoutThucTe", s.getThoiGianCheckoutThucTe());
            map.put("soNguoiThucTe", s.getSoNguoiThucTe());
            map.put("suDungDichVuList", s.getSuDungDichVuList());
            map.put("thietHaiList", s.getThietHaiList());
            map.put("calculatedBasePrice", traPhongService.calculateTienPhong(s));
            map.put("actualRoomName", traPhongService.getRoomNames(s));
            return map;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/checkout-info/{idLuutru}")
    public ResponseEntity<?> getCheckoutInfo(@PathVariable Integer idLuutru) {
        try {
            BigDecimal total = traPhongService.tinhTongTien(idLuutru);
            return ResponseEntity.ok(Map.of("total", total));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/finalize")
    public ResponseEntity<?> finalizeCheckout(@RequestBody Map<String, Object> payload) {
        try {
            Integer idLuutru = (Integer) payload.get("idLuutru");
            String paymentMethod = (String) payload.get("paymentMethod");
            HoaDon hoaDon = traPhongService.thucHienTraPhong(idLuutru, paymentMethod);
            return ResponseEntity.ok(hoaDon);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
