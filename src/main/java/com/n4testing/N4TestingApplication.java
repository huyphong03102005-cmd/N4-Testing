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
    public CommandLineRunner dataInitializer(UserRepository userRepository) {
        return args -> {
            if (userRepository.count() == 0) {
                User user1 = new User(null, "Admin", "admin@n4hotel.com");
                User user2 = new User(null, "Nguyen Van A", "vana@gmail.com");
                User user3 = new User(null, "Tran Thi B", "thib@gmail.com");

                userRepository.saveAll(List.of(user1, user2, user3));

                System.out.println("✅ Đã khởi tạo 3 người dùng mẫu!");
            }
        };
    }
}
