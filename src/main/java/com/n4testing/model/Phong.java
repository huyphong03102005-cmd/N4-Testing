package com.n4testing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "phong")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Phong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_phong")
    private Integer idPhong;

    @Column(name = "ten_phong", nullable = false)
    private String tenPhong;

    @Column(name = "loai_phong")
    private String loaiPhong;

    @Column(name = "suc_chua")
    private Integer sucChua;

    @Column(name = "gia_phong")
    private BigDecimal giaPhong;

    @Column(name = "trang_thai")
    private String trangThai; // Trống, Bận, Sửa chữa
}
