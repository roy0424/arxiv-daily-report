package com.example.arxivdailyreport.service;

import com.example.arxivdailyreport.entity.Category;
import com.example.arxivdailyreport.entity.Paper;
import com.example.arxivdailyreport.exception.BusinessException;
import com.example.arxivdailyreport.exception.ErrorCode;
import com.example.arxivdailyreport.repository.CategoryRepository;
import com.example.arxivdailyreport.repository.PaperRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ArxivFetcherService {
    public final PaperRepository paperRepository;
    public final CategoryRepository categoryRepository;

    @Transactional
    public List<Paper> fetchRss(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        String rssUrl = "https://rss.arxiv.org/rss/" + category.getEndpoint();
        List<Paper> newPapers = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(rssUrl).get();
            Elements items = doc.select("item");

            for (Element item: items) {
                String title = Objects.requireNonNull(item.selectFirst("title")).text();
                String link = Objects.requireNonNull(item.selectFirst("link")).text();
                String description = Objects.requireNonNull(item.selectFirst("description")).text();
                String pubDate = Objects.requireNonNull(item.selectFirst("pubDate")).text();
                LocalDate updatedAt = ZonedDateTime.parse(pubDate, DateTimeFormatter.RFC_1123_DATE_TIME).toLocalDate();

                Paper paper = paperRepository.findByAbsLink(link)
                        .map(p -> p.toBuilder()
                                .title(title)
                                .summary(description)
                                .updatedAt(updatedAt)
                                .fetchedAt(LocalDate.now())
                                .build())
                        .orElse(
                                Paper.builder()
                                        .title(title)
                                        .absLink(link)
                                        .pdfLink(link.replace("abs", "pdf"))
                                        .summary(description)
                                        .updatedAt(updatedAt)
                                        .fetchedAt(LocalDate.now())
                                        .embedded(false)
                                        .fullTextCached(false)
                                        .build()
                        );
                newPapers.add(paper);
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FAILED_TO_FETCH_RSS);
        }
        return newPapers;
    }
}
