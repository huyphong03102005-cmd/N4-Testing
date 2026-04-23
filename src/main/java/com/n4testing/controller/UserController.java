package com.n4testing.controller;
import com.n4testing.model.TaiKhoan;
import com.n4testing.model.User;
import com.n4testing.repository.TaiKhoanRepository;
import com.n4testing.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*") // Allow frontend access from any origin (ideal for local testing)
public class UserController {

    private final UserService userService;
    private final TaiKhoanRepository taiKhoanRepository;

    @Autowired
    public UserController(UserService userService, TaiKhoanRepository taiKhoanRepository) {
        this.userService = userService;
        this.taiKhoanRepository = taiKhoanRepository;
    }

    // Lấy thông tin tài khoản đang đăng nhập
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(HttpSession session) {
        TaiKhoan user = (TaiKhoan) session.getAttribute("user");
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Chưa đăng nhập");
        }
        return ResponseEntity.ok(user);
    }

    // Đổi mật khẩu
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> payload, HttpSession session) {
        TaiKhoan sessionUser = (TaiKhoan) session.getAttribute("user");
        if (sessionUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Chưa đăng nhập");
        }

        String currentPw = payload.get("currentPassword");
        String newPw = payload.get("newPassword");
        String confirmPw = payload.get("confirmPassword");

        // 1. Kiểm tra mật khẩu hiện tại
        if (!sessionUser.getMatKhau().equals(currentPw)) {
            return ResponseEntity.badRequest().body("Mật khẩu hiện tại không chính xác!");
        }

        // 2. Kiểm tra mật khẩu mới và xác nhận
        if (newPw == null || newPw.isEmpty()) {
            return ResponseEntity.badRequest().body("Mật khẩu mới không được để trống!");
        }
        if (!newPw.equals(confirmPw)) {
            return ResponseEntity.badRequest().body("Mật khẩu xác nhận không khớp!");
        }

        // 3. Cập nhật database
        TaiKhoan dbUser = taiKhoanRepository.findById(sessionUser.getId()).orElseThrow();
        dbUser.setMatKhau(newPw);
        taiKhoanRepository.save(dbUser);

        // 4. Cập nhật session
        session.setAttribute("user", dbUser);

        return ResponseEntity.ok("Đổi mật khẩu thành công!");
    }

    // Cập nhật thông tin cá nhân
    @PostMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@RequestBody TaiKhoan profileData, HttpSession session) {
        TaiKhoan sessionUser = (TaiKhoan) session.getAttribute("user");
        if (sessionUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Chưa đăng nhập");
        }

        TaiKhoan dbUser = taiKhoanRepository.findById(sessionUser.getId()).orElseThrow();
        dbUser.setHoTen(profileData.getHoTen());
        dbUser.setEmail(profileData.getEmail());
        dbUser.setNgaySinh(profileData.getNgaySinh());
        dbUser.setGioiTinh(profileData.getGioiTinh());
        dbUser.setSoDienThoai(profileData.getSoDienThoai());
        dbUser.setChucVu(profileData.getChucVu());

        taiKhoanRepository.save(dbUser);
        session.setAttribute("user", dbUser); // Cập nhật session

        return ResponseEntity.ok("Cập nhật thông tin thành công!");
    }

    // GET /api/users - Lấy danh sách tất cả người dùng
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    // GET /api/users/{id} - Lấy thông tin người dùng theo ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/users - Tạo người dùng mới
    @PostMapping
    public User createUser(@RequestBody User user) {
        return userService.createUser(user);
    }

    // PUT /api/users/{id} - Cập nhật người dùng
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/users/{id} - Xóa người dùng
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
