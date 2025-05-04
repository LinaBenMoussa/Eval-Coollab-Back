package com.example.backend.repository;

import com.example.backend.entity.Productivite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductiviteRepository extends JpaRepository<Productivite, Long> {

}
