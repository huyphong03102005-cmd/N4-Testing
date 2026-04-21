package com.n4testing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "hoa_don")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoaDon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hoadon")
    private Integer idHoadon;

    @Column(name = "tong_tien")
    private BigDecimal tongTien = BigDecimal.ZERO;

    @Column(name = "trang_thai")
    private String trangThai; // UNPAID, PAID, CANCELLED

    @Column(name = "ngay_lap")
    private LocalDateTime ngayLap = LocalDateTime.now();
    
    @PrePersist
    protected void onCreate() {
        if (ngayLap == null) {
            ngayLap = LocalDateTime.now();
        }
    }
}
