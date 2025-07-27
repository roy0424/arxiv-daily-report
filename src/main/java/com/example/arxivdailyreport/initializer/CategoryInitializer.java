package com.example.arxivdailyreport.initializer;

import com.example.arxivdailyreport.entity.ArxivCategory;
import com.example.arxivdailyreport.entity.Category;
import com.example.arxivdailyreport.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CategoryInitializer implements ApplicationRunner {
    private final CategoryRepository categoryRepository;

    @Override
    public void run(ApplicationArguments args) {
        for (ArxivCategory category : ArxivCategory.values()) {
            if (category != ArxivCategory.CS && !categoryRepository.existsByEndpoint(category.getEndpoint())) {
                categoryRepository.save(
                    Category.builder()
                        .name(category.getName())
                        .endpoint(category.getEndpoint())
                        .build()
                );
            }
        }
    }
}
