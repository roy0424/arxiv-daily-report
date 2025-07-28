package com.example.arxivdailyreport.batch.rss;

import com.example.arxivdailyreport.dto.PaperDto;
import com.example.arxivdailyreport.repository.CategoryRepository;
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
public class RssFetchJobConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final CategoryPartitioner partitioner;
    private final PaperItemWriter writer;
    private final CategoryRepository categoryRepository;

    @Bean
    public Job rssFetchJob() {
        return new JobBuilder("rssFetchJob", jobRepository)
                .start(partitionStep())
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
                .<PaperDto, PaperDto>chunk(10, transactionManager)
                .reader(rssItemReader(null))
                .writer(writer)
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<PaperDto> rssItemReader(
            @Value("#{stepExecutionContext['categoryId']}") Long categoryId
    ) {
        return new RssItemReader(categoryId, categoryRepository);
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
