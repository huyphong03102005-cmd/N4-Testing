package com.n4testing.repository;

import com.n4testing.model.Phong;
import com.n4testing.model.TaiSan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaiSanRepository extends JpaRepository<TaiSan, Integer> {
    List<TaiSan> findByPhong(Phong phong);
}
