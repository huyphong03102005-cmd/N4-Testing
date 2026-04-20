package com.n4testing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chi_tiet_dat_phong")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietDatPhong {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ct_dat_phong")
    private Integer idCtDatPhong;

    @ManyToOne
    @JoinColumn(name = "id_phong")
    private Phong phong;

    @ManyToOne
    @JoinColumn(name = "ma_dat_phong")
    private DatPhong datPhong;

    @Column(name = "so_luong_phong")
    private Integer soLuongPhong = 1;
}
