package com.n4testing.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "dat_phong")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DatPhong {
    @Id
    @Column(name = "ma_dat_phong")
    private String maDatPhong;

    @Column(name = "ngay_nhan", nullable = false)
    private LocalDateTime ngayNhan;

    @Column(name = "ngay_tra", nullable = false)
    private LocalDateTime ngayTra;

    @Column(name = "so_nguoi_lon")
    private Integer soNguoiLon = 1;

    @Column(name = "so_tre_em")
    private Integer soTreEm = 0;

    @Column(name = "tong_so_nguoi", insertable = false, updatable = false)
    private Integer tongSoNguoi;

    @Column(name = "so_phong")
    private String soPhong;

    @OneToMany(mappedBy = "datPhong", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("datPhong")
    private List<ChiTietDatPhong> chiTietDatPhongs;

    public String getDisplaySoPhong() {
        if (chiTietDatPhongs == null || chiTietDatPhongs.isEmpty()) {
            return soPhong;
        }
        return chiTietDatPhongs.stream()
                .filter(ct -> ct.getPhong() != null)
                .map(ct -> ct.getPhong().getTenPhong())
                .collect(Collectors.joining(", "));
    }

    @Column(name = "trang_thai")
    private String trangThai; // Đã đặt, Đang ở, Đã trả, Đã hủy

    @Column(name = "ten_nguoi_dat")
    private String tenNguoiDat;

    @Column(name = "email")
    private String email;

    @Column(name = "sdt_nguoi_dat")
    private String sdtNguoiDat;

    @Column(name = "tien_coc")
    private BigDecimal tienCoc = BigDecimal.ZERO;

    @Column(name = "tong_thanh_toan")
    private BigDecimal tongThanhToan = BigDecimal.ZERO;

    @Column(name = "phuong_thuc_thanh_toan")
    private String phuongThucThanhToan;

    @ManyToOne
    @JoinColumn(name = "id_kh")
    private KhachHang khachHang;

    @Column(name = "ghi_chu")
    private String ghiChu;

    // id_letan reference can be added if LeTan entity is created
}
