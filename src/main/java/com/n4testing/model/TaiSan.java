package com.n4testing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "tai_san")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaiSan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_taisan")
    private Integer idTaisan;

    @Column(name = "ten_tai_san", nullable = false)
    private String tenTaiSan;

    @Column(name = "gia_tri_boi_thuong")
    private BigDecimal giaTriBoiThuong;

    @ManyToOne
    @JoinColumn(name = "id_phong")
    private Phong phong;
}
