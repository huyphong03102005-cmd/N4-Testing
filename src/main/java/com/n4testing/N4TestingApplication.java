package com.n4testing;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

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
}
