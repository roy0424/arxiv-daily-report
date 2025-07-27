package com.example.arxivdailyreport.client;

import com.example.arxivdailyreport.dto.PaperDto;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
public class ArxivApiClient {
    private static final String ARXIV_API_URL = "http://export.arxiv.org/api/query";

    public List<PaperDto> fetchPapers(int start, int maxResults) {
        String query = String.format("%s?search_query=cat:cs*&start=%d&max_results=%d", ARXIV_API_URL, start, maxResults);
        log.info("Fetching arXiv papers from: {}", query);

        try {
            Document doc = Jsoup.connect(query).timeout(10000).get();
            Elements entries = doc.select("entry");

            List<PaperDto> papers = new ArrayList<>();
            for (Element entry : entries) {
                String title = Objects.requireNonNull(entry.selectFirst("title")).text();
                String link = Objects.requireNonNull(entry.selectFirst("id")).text();
                String summary = Objects.requireNonNull(entry.selectFirst("summary")).text().replaceAll("\n", " ").trim();

                Elements categoryTags = entry.select("category");
                List<String> categories = categoryTags.stream()
                        .map(tag -> tag.attr("term"))
                        .distinct()
                        .filter(term -> term.contains("cs."))
                        .toList();

                String publishedStr = Objects.requireNonNull(entry.selectFirst("published")).text();
                LocalDate publishedDate = LocalDate.parse(publishedStr, DateTimeFormatter.ISO_INSTANT);

                papers.add(PaperDto.builder()
                        .title(title)
                        .link(link)
                        .summary(summary)
                        .categories(categories)
                        .publishedAt(publishedDate)
                        .build());

            }
            return papers;
        } catch (Exception e) {
            log.error("Error fetching/parsing arXiv API", e);
            return List.of();
        }
    }
}
