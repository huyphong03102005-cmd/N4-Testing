package com.n4testing.repository;

import com.n4testing.model.LuuTru;
import com.n4testing.model.SuDungDichVu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SuDungDichVuRepository extends JpaRepository<SuDungDichVu, Integer> {
    List<SuDungDichVu> findByLuuTru(LuuTru luuTru);
}
