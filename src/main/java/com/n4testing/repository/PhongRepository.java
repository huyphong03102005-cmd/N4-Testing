package com.n4testing.repository;

import com.n4testing.model.Phong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhongRepository extends JpaRepository<Phong, Integer> {
    List<Phong> findByTrangThai(String trangThai);
    Optional<Phong> findByTenPhong(String tenPhong);
}
