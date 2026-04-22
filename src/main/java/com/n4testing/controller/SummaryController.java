package com.n4testing.controller;

import com.n4testing.model.ChiTietDatPhong;
import com.n4testing.model.Phong;
import com.n4testing.service.NhanPhongService;
import com.n4testing.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/summary")
@RequiredArgsConstructor
public class SummaryController {

    private final NhanPhongService nhanPhongService;
    private final NotificationService notificationService;

    @GetMapping
    public Map<String, Object> getSummary() {
        List<Phong> rooms = nhanPhongService.getAllPhongs();
        Map<Integer, ChiTietDatPhong> roomBookings = nhanPhongService.getActiveBookingMap();

        // Group rooms by floors
        Map<String, List<Map<String, Object>>> floors = rooms.stream()
                .filter(p -> p.getTenPhong() != null && !p.getTenPhong().isEmpty())
                .sorted(Comparator.comparing(Phong::getTenPhong, (s1, s2) -> {
                    try {
                        return Integer.compare(Integer.parseInt(s1), Integer.parseInt(s2));
                    } catch (NumberFormatException e) {
                        return s1.compareTo(s2);
                    }
                }))
                .map(p -> {
                    Map<String, Object> roomMap = new HashMap<>();
                    roomMap.put("idPhong", p.getIdPhong());
                    roomMap.put("tenPhong", p.getTenPhong());
                    roomMap.put("trangThai", p.getTrangThai());
                    roomMap.put("giaPhong", p.getGiaPhong());
                    
                    ChiTietDatPhong booking = roomBookings.get(p.getIdPhong());
                    if (booking != null) {
                        Map<String, Object> bMap = new HashMap<>();
                        bMap.put("tenKhach", booking.getDatPhong().getTenNguoiDat());
                        bMap.put("sdtKhach", booking.getDatPhong().getSdtNguoiDat());
                        bMap.put("email", booking.getDatPhong().getEmail());
                        bMap.put("ngayNhan", booking.getDatPhong().getNgayNhan());
                        bMap.put("ngayTra", booking.getDatPhong().getNgayTra());
                        bMap.put("soNguoi", booking.getDatPhong().getTongSoNguoi());
                        bMap.put("giaPhong", p.getGiaPhong());
                        bMap.put("trangThaiDat", booking.getDatPhong().getTrangThai());
                        roomMap.put("booking", bMap);
                    }
                    return roomMap;
                })
                .collect(Collectors.groupingBy(
                        p -> "Tầng " + ((String)p.get("tenPhong")).substring(0, 1),
                        TreeMap::new,
                        Collectors.toList()));

        // Statistics
        long total = rooms.size();
        long occupied = rooms.stream().filter(p -> "Bận".equals(p.getTrangThai())).count();
        long maintenance = rooms.stream().filter(p -> "Sửa chữa".equals(p.getTrangThai())).count();
        long reserved = rooms.stream()
                .filter(p -> "Trống".equals(p.getTrangThai()) && 
                             roomBookings.containsKey(p.getIdPhong()) && 
                             "Chờ check-in".equals(roomBookings.get(p.getIdPhong()).getDatPhong().getTrangThai()))
                .count();
        long available = total - occupied - maintenance - reserved;

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("available", available);
        stats.put("occupied", occupied);
        stats.put("reserved", reserved);
        stats.put("maintenance", maintenance);

        Map<String, Object> response = new HashMap<>();
        response.put("floors", floors);
        response.put("stats", stats);
        return response;
    }

    @PostMapping("/change-room")
    public ResponseEntity<?> changeRoom(@RequestParam Integer currentRoomId,
                                        @RequestParam String targetRoomName) {
        try {
            nhanPhongService.changeRoom(currentRoomId, targetRoomName);
            // Phát tín hiệu CHỈ SAU KHI transaction đã hoàn tất và thành công
            notificationService.broadcastUpdate();
            return ResponseEntity.ok(Map.of("message", "Đổi phòng thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
