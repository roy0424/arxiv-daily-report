package com.example.arxivdailyreport.repository;

import com.example.arxivdailyreport.entity.PaperContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaperContentRepository extends JpaRepository<PaperContent, Long> {
}
