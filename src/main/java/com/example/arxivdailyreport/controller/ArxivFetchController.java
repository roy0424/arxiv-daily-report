package com.example.arxivdailyreport.controller;

import com.example.arxivdailyreport.dto.PaperResponse;
import com.example.arxivdailyreport.entity.ArxivCategory;
import com.example.arxivdailyreport.service.ArxivFetcherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/arxiv")
@RequiredArgsConstructor
public class ArxivFetchController {
    private final ArxivFetcherService arxivFetcherService;

    @GetMapping("/fetch-rss/{categoryId}")
    public ResponseEntity<List<PaperResponse>> fetchRss(
        @PathVariable("categoryId") Long categoryId
    ) {
        return ResponseEntity.ok(arxivFetcherService.fetchRss(categoryId));
    }
}
