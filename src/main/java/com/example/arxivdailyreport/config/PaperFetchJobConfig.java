package com.example.arxivdailyreport.config;

import com.example.arxivdailyreport.batch.ArxivPartitionedReader;
import com.example.arxivdailyreport.batch.ArxivPartitioner;
import com.example.arxivdailyreport.batch.PaperItemProcessor;
import com.example.arxivdailyreport.batch.PaperItemWriter;
import com.example.arxivdailyreport.client.ArxivApiClient;
import com.example.arxivdailyreport.dto.PaperDto;
import com.example.arxivdailyreport.entity.Paper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
public class PaperFetchJobConfig {
    private final ArxivPartitioner partitioner;
    private final ArxivApiClient apiClient;
    private final PaperItemProcessor processor;
    private final PaperItemWriter writer;

    @Bean
    public Job paperFetchJob(JobRepository jobRepository, Step partitionedStep) {
        return new JobBuilder("paperFetchJob", jobRepository)
                .start(partitionedStep)
                .build();
    }

    @Bean
    public Step partitionedStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<PaperDto> reader
    ) {
        return new StepBuilder("partitionedStep", jobRepository)
                .partitioner("fetchWorkerStep", partitioner)
                .step(fetchWorkerStep(jobRepository, transactionManager, reader))
                .gridSize(8000)
                .taskExecutor(taskExecutor())
                .build();
    }

    @Bean
    public Step fetchWorkerStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ItemReader<PaperDto> reader
    ) {
        return new StepBuilder("fetchWorkerStep", jobRepository)
                .<PaperDto, Paper>chunk(100, transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .faultTolerant()
                .retryLimit(3)
                .retry(Exception.class)
                .skipLimit(10)
                .skip(Exception.class)
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<PaperDto> partitionedReader(
            @Value("#{stepExecutionContext['start']}") int start,
            @Value("#{stepExecutionContext['end']}") int end
    ) {
        return new ArxivPartitionedReader(start, end, 100, apiClient);
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(8);
        executor.setThreadNamePrefix("arxiv-thread-");
        executor.initialize();
        return executor;
    }
}
