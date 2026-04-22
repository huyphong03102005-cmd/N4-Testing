package com.n4testing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "dich_vu")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DichVu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dichvu")
    private Integer idDichvu;

    @Column(name = "ten_dich_vu", nullable = false)
    private String tenDichVu;

    @Column(name = "don_gia", nullable = false)
    private BigDecimal donGia;
}
