package com.example.arxivdailyreport.controller;

import com.example.arxivdailyreport.service.RssJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/batch")
@RequiredArgsConstructor
public class BatchJobController {
    private final RssJobService rssJobService;

    @PostMapping("/rss/json")
    public ResponseEntity<String> fetchRssJob() {
        rssJobService.runRssJobAsync();
        return ResponseEntity.accepted().body("RSS Job 실행 요청 수락됨");
    }

    @PostMapping("/rss/db")
    public ResponseEntity<String> fetchRssDbJob() {
        rssJobService.runRssDbJobAsync();
        return ResponseEntity.accepted().body("RSS DB Job 실행 요청 수락됨");
    }
}
