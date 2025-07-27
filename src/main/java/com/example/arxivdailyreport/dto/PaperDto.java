package com.example.arxivdailyreport.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaperDto {
    private String title;
    private String link;
    private String summary;
    private List<String> categories;
    private LocalDate publishedAt;
}
