package com.n4testing.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "luu_tru")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LuuTru {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_luutru")
    private Integer idLuutru;

    @ManyToOne
    @JoinColumn(name = "ma_dat_phong")
    private DatPhong datPhong;

    @Column(name = "thoi_gian_checkin_thuc_te")
    private LocalDateTime thoiGianCheckinThucTe;

    @Column(name = "thoi_gian_checkout_thuc_te")
    private LocalDateTime thoiGianCheckoutThucTe;

    @Column(name = "so_nguoi_thuc_te")
    private Integer soNguoiThucTe;

    @OneToMany(mappedBy = "luuTru", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("luuTru")
    private List<SuDungDichVu> suDungDichVuList;

    @OneToMany(mappedBy = "luuTru", fetch = FetchType.LAZY)
    @JsonIgnoreProperties("luuTru")
    private List<ThietHai> thietHaiList;
}
