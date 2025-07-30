package com.example.arxivdailyreport.batch.rss.db;

import com.example.arxivdailyreport.dto.PaperDto;
import com.example.arxivdailyreport.entity.Paper;
import com.example.arxivdailyreport.repository.CategoryRepository;
import com.example.arxivdailyreport.repository.PaperRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.IOException;

@Configuration
@RequiredArgsConstructor
public class JsonImportJobConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final PaperRepository paperRepository;
    private final CategoryRepository categoryRepository;
    private final ObjectMapper objectMapper;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job jsonImportJob() throws IOException {
        return new JobBuilder("jsonImportJob", jobRepository)
                .start(jsonImportStep())
                .build();
    }

    @Bean
    public Step jsonImportStep() throws IOException {
        return new StepBuilder("jsonImportStep", jobRepository)
                .<PaperDto, Paper>chunk(50, transactionManager)
                .reader(jsonPaperReader())
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
    public ItemReader<PaperDto> jsonPaperReader() throws IOException {
        return new JsonPaperReader(objectMapper);
    }
}
