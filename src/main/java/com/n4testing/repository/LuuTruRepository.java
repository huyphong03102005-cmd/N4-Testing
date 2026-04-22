package com.n4testing.repository;

import com.n4testing.model.LuuTru;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Query;
import java.util.List;

@Repository
public interface LuuTruRepository extends JpaRepository<LuuTru, Integer> {
    List<LuuTru> findByThoiGianCheckoutThucTeIsNull();

    @Query("SELECT DISTINCT lt FROM LuuTru lt " +
            "JOIN FETCH lt.datPhong dp " +
            "LEFT JOIN FETCH dp.chiTietDatPhongs ct " +
            "LEFT JOIN FETCH ct.phong " +
            "WHERE lt.thoiGianCheckoutThucTe IS NULL")
    List<LuuTru> findActiveStaysWithDetails();
}
