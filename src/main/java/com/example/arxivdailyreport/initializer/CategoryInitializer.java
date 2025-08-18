package com.example.arxivdailyreport.initializer;

import com.example.arxivdailyreport.entity.ArxivCategory;
import com.example.arxivdailyreport.entity.Category;
import com.example.arxivdailyreport.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(1)
public class CategoryInitializer implements ApplicationRunner {
    private final CategoryRepository categoryRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // TODO: Consider extracting this initialization into a batch job or admin tool
        String url = "https://arxiv.org/category_taxonomy";
        Document doc = null;
        int maxRetries = 3;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                doc = Jsoup.connect(url).get();
                break;
            } catch (IOException e) {
                log.warn("카테고리 페이지 로딩 실패 ({}회) : {}", attempt, e.getMessage());
                if (attempt == maxRetries) {
                    log.error("카테고리 페이지 로딩 실패로 기본 카테고리로 초기화합니다.", e);
                    initializeDefaultCategories();
                    return;
                }
            }
        }

        Elements h4Tags = doc.select("div.column.is-one-fifth > h4");

        for (Element h4 : h4Tags) {
            String raw = h4.text();
            int splitIndex = raw.indexOf(" ");
            if (splitIndex == -1) continue;

            String endpoint = raw.substring(0, splitIndex).trim();
            String name = raw.substring(splitIndex).replace("(", "").replace(")", "").trim();

            if (!categoryRepository.existsByEndpoint(endpoint)) {
                categoryRepository.save(Category.builder()
                        .endpoint(endpoint)
                        .name(name)
                        .build());
                log.info("✅ 카테고리 저장 성공 - {}: {}", endpoint, name);
            }
        }

        log.info("✅ 카테고리 초기화 완료 - 총 {}개 카테고리", h4Tags.size());
    }

    private void initializeDefaultCategories() {
        for (ArxivCategory category : ArxivCategory.values()) {
            if (!categoryRepository.existsByEndpoint(category.getEndpoint())) {
                categoryRepository.save(Category.builder()
                        .endpoint(category.getEndpoint())
                        .name(category.getName())
                        .build());
                log.info("✅ 기본 카테고리 저장 성공 - {}: {}", category.getEndpoint(), category.getName());
            }
        }
        log.info("✅ 기본 카테고리 초기화 완료 - 총 {}개 카테고리", ArxivCategory.values().length);
    }
}
