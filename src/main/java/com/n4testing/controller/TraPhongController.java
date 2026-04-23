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
        // Use optimized query that fetches related and room data in one go
        List<LuuTru> stays = luuTruRepository.findActiveStaysWithDetails();
        
        // Return a simplified list for discovery/overview, 
        // detailed info should be fetched via /stay-details/{id}
        List<Map<String, Object>> result = stays.stream().map(s -> {
            Map<String, Object> map = new java.util.HashMap<>();
            map.put("idLuutru", s.getIdLuutru());
            map.put("tenNguoiDat", s.getDatPhong() != null ? s.getDatPhong().getTenNguoiDat() : "N/A");
            map.put("thoiGianCheckinThucTe", s.getThoiGianCheckinThucTe());
            return map;
        }).collect(Collectors.toList());
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/stay-details/{idLuutru}")
    public ResponseEntity<?> getStayDetails(@PathVariable Integer idLuutru) {
        try {
            return ResponseEntity.ok(traPhongService.getStayDetailDto(idLuutru));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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
