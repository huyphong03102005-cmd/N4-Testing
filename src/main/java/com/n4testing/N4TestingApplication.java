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
    public CommandLineRunner checkConnection(JdbcTemplate jdbcTemplate) {
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
    public CommandLineRunner dataInitializer(UserRepository userRepository, com.n4testing.repository.TaiKhoanRepository taiKhoanRepository) {
        return args -> {
            // Khởi tạo User mẫu nếu chưa có
            if (userRepository.count() == 0) {
                User user1 = new User(null, "Admin", "test@gmail.com");
                userRepository.save(user1);
                System.out.println("✅ Đã khởi tạo người dùng mẫu!");
            }
            
            // Đảm bảo có tài khoản test@gmail.com để chạy test automation
            if (taiKhoanRepository.findByEmail("test@gmail.com").isEmpty()) {
                com.n4testing.model.TaiKhoan tk = new com.n4testing.model.TaiKhoan();
                tk.setTenDangNhap("admin");
                tk.setMatKhau("123");
                tk.setEmail("test@gmail.com");
                tk.setHoTen("Quản trị viên (Test)");
                taiKhoanRepository.save(tk);
                System.out.println("✅ Đã khởi tạo/cập nhật tài khoản mẫu (test@gmail.com)!");
            }
        };
    }
}
