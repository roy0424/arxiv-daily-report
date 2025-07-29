package com.example.arxivdailyreport.service;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RssJobService {
    private final JobLauncher jobLauncher;
    private final Job rssFetchJob;
    private final Job jsonImportJob;

    @Async
    public void runRssJobAsync() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("run.id", LocalDateTime.now().toString())
                    .toJobParameters();
            jobLauncher.run(rssFetchJob, params);
        } catch (Exception e) {
            System.err.println("❌ RSS Job 실행 실패: " + e.getMessage());
        }
    }

    @Async
    public void runRssDbJobAsync() {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addString("run.id", LocalDateTime.now().toString())
                    .toJobParameters();
            jobLauncher.run(jsonImportJob, params);
        } catch (Exception e) {
            System.err.println("❌ RSS DB Job 실행 실패: " + e.getMessage());
        }
    }
}
