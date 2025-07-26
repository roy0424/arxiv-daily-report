package com.example.arxivdailyreport.service;

import com.example.arxivdailyreport.entity.Paper;
import com.example.arxivdailyreport.entity.PaperContent;
import com.example.arxivdailyreport.repository.PaperContentRepository;
import com.example.arxivdailyreport.repository.PaperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaperCrawlingService {
    private final PaperRepository paperRepository;
    private final PaperContentRepository paperContentRepository;

    public String fetchAndCacheFullText(Paper paper) {
        String content = crawl(paper.getLink());

        PaperContent contentEntity = PaperContent.builder()
                .paper(paper)
                .rawText(content)
                .parsedSummary(null)
                .cachedAt(LocalDateTime.now())
                .build();
        paperContentRepository.save(contentEntity);

        paper.markFullTextCached();
        paperRepository.save(paper);

        return content;
    }

    private String crawl(String link) {
        return "asd";
    }
}
