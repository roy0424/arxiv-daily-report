package com.example.arxivdailyreport.batch.rss;

import com.example.arxivdailyreport.batch.PaperSaver;
import com.example.arxivdailyreport.dto.PaperDto;
import com.example.arxivdailyreport.entity.Category;
import com.example.arxivdailyreport.entity.Paper;
import com.example.arxivdailyreport.entity.PaperCategory;
import com.example.arxivdailyreport.repository.CategoryRepository;
import com.example.arxivdailyreport.repository.PaperRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaperItemWriter implements ItemWriter<PaperDto> {
    private final PaperRepository paperRepository;
    private final CategoryRepository categoryRepository;
    private final PaperSaver paperSaver;

    @Override
    @Transactional
    public void write(@NotNull Chunk<? extends PaperDto> items) {
        List<Paper> papers = new ArrayList<>();

        for (PaperDto item : items) {
            List<Category> categories = item.getCategories().stream()
                    .map(endpoint -> categoryRepository.findByEndpoint(endpoint).orElse(null))
                    .filter(Objects::nonNull)
                    .distinct()
                    .toList();
            Paper existing = paperRepository.findByAbsLink(item.getAbsLink()).orElse(null);

            if (existing != null) {
                boolean titleChanged = !Objects.equals(existing.getTitle(), item.getTitle());
                boolean summaryChanged = !Objects.equals(existing.getSummary(), item.getSummary());
                boolean changed = titleChanged || summaryChanged;

                if (changed) {
                    existing.setTitle(item.getTitle());
                    existing.setSummary(item.getSummary());
                    existing.setUpdatedAt(item.getUpdatedAt());
                    existing.setFetchedAt(item.getFetchedAt());
                }

                for (Category cat : categories) {
                    boolean hasCategory = existing.getPaperCategories().stream()
                            .anyMatch(pc -> pc.getCategory().getId().equals(cat.getId()));

                    if (!hasCategory) {
                        existing.getPaperCategories().add(PaperCategory.builder()
                                .paper(existing)
                                .category(cat)
                                .build());
                        changed = true;
                    }
                }

                if (changed) {
                    papers.add(existing);
                } else {
                    log.debug("변경 없음: {}", item.getAbsLink());
                }
            } else {
                Paper newPaper = Paper.builder()
                        .title(item.getTitle())
                        .absLink(item.getAbsLink())
                        .pdfLink(item.getPdfLink())
                        .summary(item.getSummary())
                        .updatedAt(item.getUpdatedAt())
                        .fetchedAt(item.getFetchedAt())
                        .embedded(false)
                        .fullTextCached(false)
                        .build();

                for (Category cat : categories) {
                    newPaper.getPaperCategories().add(PaperCategory.builder()
                            .paper(newPaper)
                            .category(cat)
                            .build());
                }

                papers.add(newPaper);
            }
        }
        log.info("총 {}개의 논문을 저장합니다.", papers.size());
        try {
            paperRepository.saveAll(papers);
        } catch (DataIntegrityViolationException e) {
            log.warn("중복 논문 존재, 개별 저장으로 재시도");
            for (Paper paper : papers) {
                try {
                    paperSaver.save(paper);
                } catch (DataIntegrityViolationException ex) {
                    log.warn("중복으로 저장 실패: {}", paper.getAbsLink());
                }
            }
        }
    }
}
