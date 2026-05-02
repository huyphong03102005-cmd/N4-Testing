package com.n4testing;

import com.n4testing.model.User;
import com.n4testing.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@SpringBootApplication
public class N4TestingApplication {
    public static void main(String[] args) {
        SpringApplication.run(N4TestingApplication.class, args);
    }

    @Bean
    public static CommandLineRunner checkConnection(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                jdbcTemplate.execute("SELECT 1");
                System.out.println("==========================================");
                System.out.println("✅ KẾT NỐI DATABASE (SUPABASE) THÀNH CÔNG!");
                System.out.println("==========================================");
            } catch (Exception e) {
                System.out.println("==========================================");
                System.out.println("❌ KẾT NỐI DATABASE THẤT BẠI!");
                System.out.println("Lỗi: " + e.getMessage());
                System.out.println("==========================================");
            }
        };
    }

    @Bean
    public static CommandLineRunner dataInitializer(UserRepository userRepository,
            com.n4testing.repository.TaiKhoanRepository taiKhoanRepository,
            com.n4testing.service.NhanPhongService nhanPhongService) {
        return args -> {
            // Khởi tạo User mẫu nếu chưa có
            if (userRepository.count() == 0) {
                User user1 = new User(null, "Admin", "test@gmail.com");
                userRepository.save(user1);
                System.out.println("✅ Đã khởi tạo người dùng mẫu!");
            }

            // Khởi tạo/Reset tài khoản admin_hotel (Dùng cho TestCheckin)
            taiKhoanRepository.findByTenDangNhap("admin_hotel").ifPresentOrElse(
                    ah -> {
                        ah.setMatKhau("pass_123");
                        ah.setHoTen("Admin Hotel");
                        taiKhoanRepository.save(ah);
                    },
                    () -> {
                        com.n4testing.model.TaiKhoan ah = new com.n4testing.model.TaiKhoan();
                        ah.setTenDangNhap("admin_hotel");
                        ah.setMatKhau("pass_123");
                        ah.setHoTen("Admin Hotel");
                        taiKhoanRepository.save(ah);
                    });

            // Reset trạng thái phòng mẫu để test case Checkin/Checkout không bị lệch data
            nhanPhongService.getAllPhongs().forEach(p -> {
                if (p.getTenPhong().equals("101")) {
                    p.setTrangThai("Trống");
                } else if (p.getTenPhong().equals("102")) {
                    p.setTrangThai("Bận");
                }
                // Các phòng khác có thể giữ nguyên hoặc reset tùy ý
            });

            // Khởi tạo/Reset tài khoản admin
            taiKhoanRepository.findByTenDangNhap("admin").ifPresentOrElse(
                    tk -> {
                        tk.setMatKhau("123");
                        taiKhoanRepository.save(tk);
                    },
                    () -> {
                        com.n4testing.model.TaiKhoan tk = new com.n4testing.model.TaiKhoan();
                        tk.setTenDangNhap("admin");
                        tk.setMatKhau("123");
                        tk.setEmail("test@gmail.com");
                        tk.setHoTen("Quản trị viên (Test)");
                        taiKhoanRepository.save(tk);
                    });

            // Khởi tạo/Reset tài khoản staff_002
            taiKhoanRepository.findByTenDangNhap("staff_002").ifPresentOrElse(
                    staff -> {
                        staff.setMatKhau("pass_123");
                        staff.setHoTen("Lê Văn Lộc");
                        staff.setEmail("loclv@gmail.com");
                        staff.setNgaySinh("22/12/2012");
                        staff.setGioiTinh("Nam");
                        staff.setSoDienThoai("0799376815");
                        staff.setChucVu("Nhân viên");
                        taiKhoanRepository.save(staff);
                    },
                    () -> {
                        com.n4testing.model.TaiKhoan staff = new com.n4testing.model.TaiKhoan();
                        staff.setTenDangNhap("staff_002");
                        staff.setMatKhau("pass_123");
                        staff.setHoTen("Lê Văn Lộc");
                        staff.setEmail("loclv@gmail.com");
                        staff.setNgaySinh("22/12/2012");
                        staff.setGioiTinh("Nam");
                        staff.setSoDienThoai("0799376815");
                        staff.setChucVu("Nhân viên");
                        taiKhoanRepository.save(staff);
                    });

            // Khởi tạo/Reset tài khoản staff_003
            taiKhoanRepository.findByTenDangNhap("staff_003").ifPresentOrElse(
                    staff3 -> {
                        staff3.setMatKhau("pass_505");
                        staff3.setHoTen("Lê Văn An");
                        staff3.setEmail("anlv@gmail.com");
                        staff3.setNgaySinh("01/01/1995");
                        staff3.setGioiTinh("Nam");
                        staff3.setSoDienThoai("0988123456");
                        staff3.setChucVu("Nhân viên");
                        taiKhoanRepository.save(staff3);
                    },
                    () -> {
                        com.n4testing.model.TaiKhoan staff3 = new com.n4testing.model.TaiKhoan();
                        staff3.setTenDangNhap("staff_003");
                        staff3.setMatKhau("pass_505");
                        staff3.setHoTen("Lê Văn An");
                        staff3.setEmail("anlv@gmail.com");
                        staff3.setNgaySinh("01/01/1995");
                        staff3.setGioiTinh("Nam");
                        staff3.setSoDienThoai("0988123456");
                        staff3.setChucVu("Nhân viên");
                        taiKhoanRepository.save(staff3);
                    });
            System.out.println("✅ Đã kiểm tra và Reset các tài khoản Test (admin, staff_002, staff_003)!");
        };
    }
}
