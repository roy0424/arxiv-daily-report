package com.example.arxivdailyreport.batch.rss.db;

import com.example.arxivdailyreport.entity.Paper;
import com.example.arxivdailyreport.repository.PaperRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class PaperWriter implements ItemWriter<Paper> {
    private final PaperRepository paperRepository;

    @Override
    @Transactional
    public void write(@NotNull Chunk<? extends Paper> items) {
        List<Paper> toSave = items.getItems().stream()
                .filter(Objects::nonNull)
                .map(p -> (Paper) p)
                .toList();

        if (toSave.isEmpty()) {
            return;
        }

        try {
            paperRepository.saveAll(toSave);
        } catch (DataIntegrityViolationException e) {
            log.warn("⚠️ 중복 논문 예외, 개별 저장 시도");
            for (Paper paper : toSave) {
                try {
                    paperRepository.save(paper);
                } catch (DataIntegrityViolationException ex) {
                    log.warn("❌ 중복 저장 실패: {}", paper.getAbsLink());
                }
            }
        }
    }
}
