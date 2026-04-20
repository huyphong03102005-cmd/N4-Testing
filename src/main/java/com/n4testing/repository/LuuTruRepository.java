package com.n4testing.repository;

import com.n4testing.model.LuuTru;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LuuTruRepository extends JpaRepository<LuuTru, Integer> {
}
