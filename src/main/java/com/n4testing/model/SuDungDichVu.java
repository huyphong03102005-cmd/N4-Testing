package com.n4testing.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "su_dung_dich_vu")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SuDungDichVu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sudung_dv")
    private Integer idSudungDv;

    @Column(name = "soluong")
    private Integer soluong = 1;

    @Column(name = "thoi_gian")
    private LocalDateTime thoiGian = LocalDateTime.now();

    @Column(name = "thanh_tien")
    private BigDecimal thanhTien;

    @ManyToOne
    @JoinColumn(name = "id_dichvu")
    private DichVu dichVu;

    @ManyToOne
    @JoinColumn(name = "id_luutru")
    @JsonIgnoreProperties("suDungDichVuList")
    private LuuTru luuTru;

    @ManyToOne
    @JoinColumn(name = "id_hoadon")
    private HoaDon hoaDon;

    @PrePersist
    @PreUpdate
    protected void onCreate() {
        if (thoiGian == null) {
            thoiGian = LocalDateTime.now();
        }
        if (dichVu != null && soluong != null) {
            // Luôn làm tròn lên thành tiền
            thanhTien = dichVu.getDonGia().multiply(new BigDecimal(soluong))
                    .setScale(0, java.math.RoundingMode.CEILING);
        }
    }
}
