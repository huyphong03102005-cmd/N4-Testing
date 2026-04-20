package com.n4testing.repository;

import com.n4testing.model.ChiTietDatPhong;
import com.n4testing.model.DatPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChiTietDatPhongRepository extends JpaRepository<ChiTietDatPhong, Integer> {
    List<ChiTietDatPhong> findByDatPhong(DatPhong datPhong);
}
