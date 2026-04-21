package com.n4testing.repository;

import com.n4testing.model.DatPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface DatPhongRepository extends JpaRepository<DatPhong, String> {
    List<DatPhong> findByTrangThai(String trangThai);
    
    @Query("SELECT d FROM DatPhong d LEFT JOIN FETCH d.chiTietDatPhongs ct LEFT JOIN FETCH ct.phong " +
           "WHERE (LOWER(d.tenNguoiDat) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR d.maDatPhong LIKE CONCAT('%', :keyword, '%') " +
           "OR d.sdtNguoiDat LIKE CONCAT('%', :keyword, '%')) AND d.trangThai = :status")
    List<DatPhong> searchBookings(@Param("keyword") String keyword, @Param("status") String status);
}
