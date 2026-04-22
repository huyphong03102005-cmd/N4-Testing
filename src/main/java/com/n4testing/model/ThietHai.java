package com.n4testing.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "thiet_hai")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThietHai {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_thie_thai")
    private Integer idThieThai;

    @Column(name = "muc_do")
    private String mucDo;

    @Column(name = "so_tien_boi_thuong")
    private BigDecimal soTienBoiThuong;

    @Column(name = "trang_thai")
    private String trangThai;

    @ManyToOne
    @JoinColumn(name = "id_taisan")
    private TaiSan taiSan;

    @ManyToOne
    @JoinColumn(name = "id_luutru")
    @JsonIgnoreProperties("thietHaiList")
    private LuuTru luuTru;
}
