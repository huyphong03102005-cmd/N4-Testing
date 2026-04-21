package com.n4testing.repository;

import com.n4testing.model.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {
    Optional<KhachHang> findByCccd(String cccd);
    Optional<KhachHang> findBySdt(String sdt);
}
