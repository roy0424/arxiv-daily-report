package com.example.arxivdailyreport.batch;

import com.example.arxivdailyreport.dto.PaperDto;
import com.example.arxivdailyreport.entity.Paper;
import com.example.arxivdailyreport.entity.PaperCategory;
import com.example.arxivdailyreport.repository.CategoryRepository;
import com.example.arxivdailyreport.repository.PaperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class PaperItemProcessor implements ItemProcessor<PaperDto, Paper> {
    private final CategoryRepository categoryRepository;
    private final PaperRepository paperRepository;

    @Override
    public Paper process(PaperDto paperDto) {
        if (paperRepository.existsByLink(paperDto.getLink())) {
            return null;
        }

        Paper paper = Paper.builder()
                .title(paperDto.getTitle())
                .link(paperDto.getLink())
                .summary(paperDto.getSummary())
                .fetchedAt(LocalDate.now())
                .embedded(false)
                .fullTextCached(false)
                .build();

        for (String endpoint : paperDto.getCategories()) {
            categoryRepository.findByEndpoint(endpoint)
                    .ifPresent(category -> paper.getPaperCategories().add(
                            PaperCategory.builder()
                                    .paper(paper)
                                    .category(category)
                                    .build()
                    ));
        }
        return paper;
    }
}
