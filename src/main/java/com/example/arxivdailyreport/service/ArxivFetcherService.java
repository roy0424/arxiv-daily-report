package com.example.arxivdailyreport.service;

import com.example.arxivdailyreport.dto.PaperResponse;
import com.example.arxivdailyreport.entity.ArxivCategory;
import com.example.arxivdailyreport.entity.Paper;
import com.example.arxivdailyreport.exception.BusinessException;
import com.example.arxivdailyreport.exception.ErrorCode;
import com.example.arxivdailyreport.repository.PaperRepository;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ArxivFetcherService {
    public final PaperRepository paperRepository;

    public List<PaperResponse> fetchRss(ArxivCategory category) {
        String rssUrl = "https://rss.arxiv.org/rss/" + category.getEndpoint();
        List<Paper> newPapers = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(rssUrl).get();
            Elements items = doc.select("item");

            for (Element item: items) {
                String title = Objects.requireNonNull(item.selectFirst("title")).text();
                String link = Objects.requireNonNull(item.selectFirst("link")).text();
                String description = Objects.requireNonNull(item.selectFirst("description")).text();

                if (paperRepository.findByLink(link).isPresent()) continue;

                Paper paper = Paper.builder()
                        .title(title)
                        .link(link)
                        .summary(description)
                        .fetchedAt(LocalDate.now())
                        .embedded(false)
                        .fullTextCached(false)
                        .build();

                paperRepository.save(paper);
                newPapers.add(paper);
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.FAILED_TO_FETCH_RSS);
        }
        return newPapers.stream().map(PaperResponse::of).toList();
    }
}
