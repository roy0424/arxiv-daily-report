package com.example.arxivdailyreport.scheduler;

import com.example.arxivdailyreport.entity.ArxivCategory;
import com.example.arxivdailyreport.service.ArxivFetcherService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArxivScheduler {
    private final ArxivFetcherService arxivFetcherService;

    @Scheduled(cron = "0 0 7 * * *")
    public void fetchAllCategories() {
        arxivFetcherService.fetchRss(ArxivCategory.CS);
    }
}
