package com.example.arxivdailyreport.dto;

import com.example.arxivdailyreport.entity.ArxivCategory;
import com.example.arxivdailyreport.entity.Paper;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaperResponse {
    private Long id;
    private String title;
    private String link;
    private ArxivCategory category;
    private String description;

    public static PaperResponse of(Paper paper) {
        return PaperResponse.builder()
                .id(paper.getId())
                .title(paper.getTitle())
                .link(paper.getLink())
                .category(paper.getCategory())
                .description(paper.getDescription())
                .build();
    }
}
