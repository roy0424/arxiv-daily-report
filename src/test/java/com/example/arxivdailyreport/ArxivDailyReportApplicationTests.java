package com.example.arxivdailyreport;

import com.example.arxivdailyreport.client.ArxivApiClient;
import com.example.arxivdailyreport.client.ArxivOaiClient;
import com.example.arxivdailyreport.dto.PaperDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ArxivDailyReportApplicationTests {
//    @Autowired
//    private ArxivOaiClient arxivOaiClient;
//
//    @Test
//    void fetchTest() {
//        List<PaperDto> papers = arxivOaiClient.fetchPapers("2010-01-01", "2010-02-01");
//        papers.forEach(paper -> {
//            System.out.println("Title: " + paper.getTitle());
//            System.out.println("Link: " + paper.getLink());
//            System.out.println("Summary: " + paper.getSummary());
//            System.out.println("Categories: " + String.join(", ", paper.getCategories()));
//            System.out.println("Published At: " + paper.getPublishedAt());
//            System.out.println("-----------------------------");
//        });
//    }

    @Test
    void contextLoads() {
    }

}
