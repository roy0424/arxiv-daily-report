package com.example.arxivdailyreport;

import com.example.arxivdailyreport.client.ArxivApiClient;
import com.example.arxivdailyreport.dto.PaperDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ArxivDailyReportApplicationTests {
    @Autowired
    private ArxivApiClient arxivApiClient;

    @Test
    void fetchTest() {
        List<PaperDto> papers = arxivApiClient.fetchPapers(0, 100);
        papers.forEach(paper -> {
            System.out.println("Title: " + paper.getTitle());
            System.out.println("Link: " + paper.getLink());
            System.out.println("Summary: " + paper.getSummary());
            System.out.println("Categories: " + String.join(", ", paper.getCategories()));
            System.out.println("Published At: " + paper.getPublishedAt());
            System.out.println("-----------------------------");
        });
    }

    @Test
    void contextLoads() {
    }

}
