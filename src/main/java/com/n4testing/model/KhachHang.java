package com.n4testing.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "khach_hang")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhachHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_kh")
    private Integer idKh;

    @Column(name = "ho_ten", nullable = false)
    private String hoTen;

    @Column(name = "sdt")
    private String sdt;

    @Column(name = "cccd", unique = true)
    private String cccd;
}
