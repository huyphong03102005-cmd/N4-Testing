package com.n4testing.controller;

import com.n4testing.model.Phong;
import com.n4testing.repository.PhongRepository;
import com.n4testing.service.NhanPhongService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final PhongRepository phongRepository;
    private final NhanPhongService nhanPhongService;

    @GetMapping("/available")
    public ResponseEntity<List<Phong>> getAvailableRooms() {
        return ResponseEntity.ok(phongRepository.findByTrangThai("Trống"));
    }

    @PostMapping("/change")
    public ResponseEntity<?> changeRoom(@RequestBody Map<String, Object> payload) {
        try {
            Integer currentRoomId = (Integer) payload.get("currentRoomId");
            String targetRoomName = (String) payload.get("targetRoomName");
            
            nhanPhongService.changeRoom(currentRoomId, targetRoomName);
            
            return ResponseEntity.ok(Map.of("message", "Đổi phòng thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
