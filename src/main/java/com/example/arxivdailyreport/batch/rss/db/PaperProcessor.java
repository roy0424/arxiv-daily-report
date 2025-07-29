package com.example.arxivdailyreport.batch.rss.db;


import com.example.arxivdailyreport.dto.PaperDto;
import com.example.arxivdailyreport.entity.Category;
import com.example.arxivdailyreport.entity.Paper;
import com.example.arxivdailyreport.entity.PaperCategory;
import com.example.arxivdailyreport.repository.CategoryRepository;
import com.example.arxivdailyreport.repository.PaperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class PaperProcessor implements ItemProcessor<PaperDto, Paper> {
    private final PaperRepository paperRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public Paper process(PaperDto item) {
        List<Category> categories = item.getCategories().stream()
                .map(endpoint -> categoryRepository.findByEndpoint(endpoint).orElse(null))
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        Paper existing = paperRepository.findByAbsLink(item.getAbsLink()).orElse(null);

        if (existing != null) {
            boolean changed = false;

            if (!Objects.equals(existing.getTitle(), item.getTitle())) {
                existing.setTitle(item.getTitle());
                changed = true;
            }

            if (!Objects.equals(existing.getSummary(), item.getSummary())) {
                existing.setSummary(item.getSummary());
                changed = true;
            }

            if (!Objects.equals(existing.getUpdatedAt(), item.getUpdatedAt())) {
                existing.setUpdatedAt(LocalDate.parse(item.getUpdatedAt()));
                changed = true;
            }

            existing.setFetchedAt(LocalDate.parse(item.getFetchedAt())); // 항상 업데이트

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

            return changed ? existing : null; // 변경이 없다면 저장하지 않음
        } else {
            Paper newPaper = Paper.builder()
                    .title(item.getTitle())
                    .absLink(item.getAbsLink())
                    .pdfLink(item.getPdfLink())
                    .summary(item.getSummary())
                    .updatedAt(LocalDate.parse(item.getUpdatedAt()))
                    .fetchedAt(LocalDate.parse(item.getFetchedAt()))
                    .embedded(false)
                    .fullTextCached(false)
                    .build();

            for (Category cat : categories) {
                newPaper.getPaperCategories().add(PaperCategory.builder()
                        .paper(newPaper)
                        .category(cat)
                        .build());
            }

            return newPaper;
        }
    }
}
