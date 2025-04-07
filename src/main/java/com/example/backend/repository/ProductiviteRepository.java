package com.example.backend.repository;

import com.example.backend.entity.Productivite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductiviteRepository extends JpaRepository<Productivite, Long> {
    @Query(value = "SELECT * FROM t_productivite ORDER BY rang ASC LIMIT 5", nativeQuery = true)
    List<Productivite> findTop5ByOrderByRangAsc();
}
