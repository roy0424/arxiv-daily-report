package com.example.arxivdailyreport.dto;

import com.example.arxivdailyreport.entity.Category;
import com.example.arxivdailyreport.entity.Paper;
import com.example.arxivdailyreport.entity.PaperCategory;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PaperResponse {
    private Long id;
    private String title;
    private String link;
    private List<String> categories;
    private String summary;

    public static PaperResponse of(Paper paper) {
        return PaperResponse.builder()
                .id(paper.getId())
                .title(paper.getTitle())
                .link(paper.getLink())
                .categories(paper.getCategories().stream()
                        .map(Category::getName)
                        .toList())
                .summary(paper.getSummary())
                .build();
    }
}
