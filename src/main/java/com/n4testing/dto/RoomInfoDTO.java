package com.n4testing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomInfoDTO {
    private Integer idPhong;
    private String tenPhong;
    private Integer floor;
    private String loaiPhong;
    private String trangThai;
    private BigDecimal giaPhong;

    // Customer info from active stay
    private Integer idLuutru;
    private String tenKhachHang;
    private LocalDateTime checkInTime;
    private LocalDateTime expectedCheckOutTime;
}
