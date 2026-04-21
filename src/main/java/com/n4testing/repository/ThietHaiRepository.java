package com.n4testing.repository;

import com.n4testing.model.LuuTru;
import com.n4testing.model.ThietHai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThietHaiRepository extends JpaRepository<ThietHai, Integer> {
    List<ThietHai> findByLuuTru(LuuTru luuTru);
}
