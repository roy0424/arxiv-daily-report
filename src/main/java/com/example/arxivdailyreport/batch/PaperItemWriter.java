package com.example.arxivdailyreport.batch;

import com.example.arxivdailyreport.entity.Paper;
import com.example.arxivdailyreport.repository.PaperRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PaperItemWriter implements ItemWriter<Paper> {
    private final PaperRepository paperRepository;

    private final Logger logger = LoggerFactory.getLogger(PaperItemWriter.class);
    private final Path logFile = Paths.get("failed-papers.log");

    @Override
    public void write(@NotNull Chunk<? extends Paper> items) {
        try {
            paperRepository.saveAll(items);
            logger.info("✅ 저장 성공 - {}개", items.size());
        } catch (Exception e) {
            List<String> failedLinks = items.getItems().stream()
                    .map(Paper::getLink)
                    .toList();

            failedLinks.forEach(link ->
                    logger.error("❌ 저장 실패 - {}", link)
            );

            try {
                Files.write(
                        logFile,
                        failedLinks,
                        StandardCharsets.UTF_8,
                        Files.exists(logFile) ? StandardOpenOption.APPEND : StandardOpenOption.CREATE
                );
            } catch (IOException ioException) {
                logger.error("❌ 로그 파일 저장 실패", ioException);
            }

            throw e;
        }
    }
}
