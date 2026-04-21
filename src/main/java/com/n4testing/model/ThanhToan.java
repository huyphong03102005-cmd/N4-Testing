package com.n4testing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "thanh_toan")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThanhToan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_thanhtoan")
    private Integer idThanhtoan;

    @Column(name = "so_tien", nullable = false)
    private BigDecimal soTien;

    @Column(name = "phuong_thuc")
    private String phuongThuc; // CASH, CREDIT_CARD, TRANSFER, QR

    @Column(name = "trang_thai")
    private String trangThai;

    @ManyToOne
    @JoinColumn(name = "id_hoadon")
    private HoaDon hoaDon;
}
