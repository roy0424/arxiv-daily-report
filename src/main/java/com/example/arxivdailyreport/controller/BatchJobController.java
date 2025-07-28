package com.example.arxivdailyreport.controller;

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
    private final JobLauncher jobLauncher;
    private final Job rssFetchJob;

    @PostMapping("/rss/fetch")
    public ResponseEntity<String> fetchRssJob() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("run.id", LocalDateTime.now().toString()) // 중복 방지
                    .toJobParameters();

            JobExecution execution = jobLauncher.run(rssFetchJob, params);
            return ResponseEntity.ok("RSS 수집 Job이 실행되었습니다. 상태: " + execution.getStatus());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Job 실행 실패: " + e.getMessage());
        }
    }
}
