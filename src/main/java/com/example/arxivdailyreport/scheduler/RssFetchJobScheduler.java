package com.example.arxivdailyreport.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class RssFetchJobScheduler {
    private final JobLauncher jobLauncher;
    private final Job rssFetchJob;

    @Scheduled(cron = "0 0 8 * * *")
    public void runRssFetchJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addString("run.id", LocalDateTime.now().toString())
                    .toJobParameters();

            log.info("🔁 RSS 수집 배치 스케줄러 실행 시작");
            JobExecution execution = jobLauncher.run(rssFetchJob, jobParameters);
            log.info("✅ 배치 실행 완료 - 상태: {}", execution.getStatus());
        } catch (Exception e) {
            log.error("❌ RSS 수집 스케줄러 실행 실패: {}", e.getMessage(), e);
        }
    }
}
