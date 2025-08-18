package com.example.arxivdailyreport.batch.rss.json;

import com.example.arxivdailyreport.batch.rss.db.JsonPaperReader;
import com.example.arxivdailyreport.batch.rss.db.PaperProcessor;
import com.example.arxivdailyreport.dto.PaperDto;
import com.example.arxivdailyreport.entity.Paper;
import com.example.arxivdailyreport.repository.CategoryRepository;
import com.example.arxivdailyreport.repository.PaperRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.batch.item.database.JpaItemWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class RssFetchJobConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final CategoryPartitioner partitioner;
    private final CategoryRepository categoryRepository;
    private final PaperRepository paperRepository;
    private final ObjectMapper objectMapper;
    private final EntityManagerFactory entityManagerFactory;

    private final ConcurrentLinkedQueue<PaperDto> paperCollector = new ConcurrentLinkedQueue<>();

    @Bean
    public Job rssFetchJob() throws IOException {
        return new JobBuilder("rssFetchJob", jobRepository)
                .start(partitionStep())
                .next(saveStep(null))
                .next(importStep())
                .build();
    }

    @Bean
    public Step partitionStep() {
        return new StepBuilder("partitionStep", jobRepository)
                .partitioner("rssStep", partitioner)
                .step(rssStep())
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Step rssStep() {
        return new StepBuilder("rssStep", jobRepository)
                .<List<PaperDto>, List<PaperDto>>chunk(1, transactionManager)
                .reader(rssItemReader(null))
                .writer(new RssJsonWriter(paperCollector))
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<List<PaperDto>> rssItemReader(
            @Value("#{stepExecutionContext['categoryIds']}") List<Long> categoryIds
    ) {
        return new RssItemReader(categoryIds, categoryRepository);
    }

    @Bean
    @JobScope
    public Step saveStep(@Value("#{jobParameters['date']}") String date) {
        return new StepBuilder("saveStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    List<PaperDto> result = new ArrayList<>(paperCollector);
                    LocalDate today = date != null ? LocalDate.parse(date) : LocalDate.now();
                    String filename = "rss-" + today + ".json";
                    Path path = Paths.get("rss", filename);
                    Files.createDirectories(path.getParent());

                    objectMapper.writeValue(path.toFile(), result);

                    log.info("✅ 저장 완료: {} ({}개)", path, result.size());
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step importStep() throws IOException {
        return new StepBuilder("importStep", jobRepository)
                .<PaperDto, Paper>chunk(50, transactionManager)
                .reader(jsonPaperReader(null))
                .processor(new PaperProcessor(paperRepository, categoryRepository))
                .writer(paperJpaItemWriter())
                .build();
    }

    @Bean
    public JpaItemWriter<Paper> paperJpaItemWriter() {
        JpaItemWriter<Paper> writer = new JpaItemWriter<>();
        writer.setEntityManagerFactory(entityManagerFactory);
        return writer;
    }

    @Bean
    @StepScope
    public ItemReader<PaperDto> jsonPaperReader(@Value("#{jobParameters['date']}") String date) throws IOException {
        LocalDate today = date != null ? LocalDate.parse(date) : LocalDate.now();
        return new JsonPaperReader(objectMapper, today);
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4); // 병렬 수집
        executor.setMaxPoolSize(8);
        executor.setThreadNamePrefix("rss-worker-");
        executor.initialize();
        return executor;
    }
}
