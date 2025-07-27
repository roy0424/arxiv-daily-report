package com.example.arxivdailyreport.repository;

import com.example.arxivdailyreport.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    boolean existsByEndpoint(String endpoint);

    Optional<Category> findByEndpoint(String endpoint);
}
