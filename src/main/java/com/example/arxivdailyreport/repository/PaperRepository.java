package com.example.arxivdailyreport.repository;

import com.example.arxivdailyreport.entity.Paper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaperRepository extends JpaRepository<Paper, Long> {
    Optional<Paper> findByLink(String link);
}
