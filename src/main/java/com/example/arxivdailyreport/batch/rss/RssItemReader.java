package com.example.arxivdailyreport.batch.rss;

import com.example.arxivdailyreport.dto.PaperDto;
import com.example.arxivdailyreport.entity.Category;
import com.example.arxivdailyreport.exception.BusinessException;
import com.example.arxivdailyreport.exception.ErrorCode;
import com.example.arxivdailyreport.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.batch.item.ItemReader;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
public class RssItemReader implements ItemReader<PaperDto> {
    private final Long categoryId;
    private final CategoryRepository categoryRepository;

    private List<PaperDto> buffer;
    private int index = 0;


    public RssItemReader(Long categoryId, CategoryRepository categoryRepository) {
        this.categoryId = categoryId;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public PaperDto read() {
        if (buffer == null) {
            buffer = fetchRss();
            log.info("[categoryId={}] 총 {}개의 논문을 가져왔습니다.", categoryId, buffer.size());
        }
        return index < buffer.size() ? buffer.get(index++) : null;
    }

    private List<PaperDto> fetchRss() {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

        String rssUrl = "https://rss.arxiv.org/rss/" + category.getEndpoint();
        List<PaperDto> dtos = new ArrayList<>();

        try {
            Document doc = fetchWithRetry(rssUrl);
            String pubDate = Objects.requireNonNull(doc.selectFirst("channel > pubDate")).text();
            LocalDate updatedAt = ZonedDateTime.parse(pubDate, DateTimeFormatter.RFC_1123_DATE_TIME).toLocalDate();
            Elements items = doc.select("item");

            for (Element item: items) {
                String title = textOrRetry(item, "title", rssUrl);
                String link = textOrRetry(item, "link", rssUrl);
                String description = textOrRetry(item, "description", rssUrl);
                Elements categoryElements = item.select("category");
                List<String> categories = categoryElements.stream()
                        .map(Element::text)
                        .distinct()
                        .toList();

                PaperDto dto = PaperDto.builder()
                        .title(title)
                        .absLink(link)
                        .pdfLink(link.replace("abs", "pdf"))
                        .summary(description)
                        .updatedAt(updatedAt)
                        .fetchedAt(LocalDate.now())
                        .categories(categories)
                        .build();
                dtos.add(dto);
            }
        } catch (Exception e) {
            log.error("❌ RSS 수집 실패 - category: id-{} name-{} endpoint-{}, url: {}, error: {}",
                    category.getId(), category.getName(), category.getEndpoint(), rssUrl, e.getMessage(), e);
            throw new BusinessException(ErrorCode.FAILED_TO_FETCH_RSS,
                    "RSS 수집 실패 - category: " + category.getName() + ", url: " + rssUrl);
        }
        return dtos;
    }

    private Document fetchWithRetry(String url) throws Exception {
        int retries = 2;
        Exception last = null;
        for (int i = 0; i < retries; i++) {
            try {
                return Jsoup.connect(url)
                        .timeout(5000)
                        .userAgent("Mozilla") // 더 안정적인 응답을 위해 User-Agent 설정
                        .get();
            } catch (Exception e) {
                log.warn("⚠️ Jsoup 연결 실패 재시도 ({}/{}): {}", i + 1, retries, e.getMessage());
                last = e;
                Thread.sleep(1000); // 대기 후 재시도
            }
        }
        throw last;
    }

    private String textOrRetry(Element item, String selector, String fallbackUrl) {
        Element element = item.selectFirst(selector);
        if (element == null) {
            try {
                log.warn("⚠️ 필드 '{}' 누락. 재시도 중...", selector);
                Document retriedDoc = fetchWithRetry(fallbackUrl);
                Element retriedItem = retriedDoc.selectFirst("item");
                if (retriedItem != null) {
                    Element retryElement = retriedItem.selectFirst(selector);
                    if (retryElement != null) {
                        return retryElement.text();
                    }
                }
            } catch (Exception e) {
                log.error("❌ 필드 재시도 실패: {}", e.getMessage());
            }
            return null;
        }
        return element.text();
    }
}
