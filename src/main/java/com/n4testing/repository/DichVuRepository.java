package com.n4testing.repository;

import com.n4testing.model.DichVu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DichVuRepository extends JpaRepository<DichVu, Integer> {
}
