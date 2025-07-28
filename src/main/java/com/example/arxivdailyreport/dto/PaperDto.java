package com.example.arxivdailyreport.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaperDto {
    private String title;
    
    private String id;
    
    @JsonProperty("abstract")
    private String summary;
    
    @JsonProperty("categories")
    private String categoriesRaw;
    
    @JsonProperty("update_date")
    private String updateDateStr;

}
