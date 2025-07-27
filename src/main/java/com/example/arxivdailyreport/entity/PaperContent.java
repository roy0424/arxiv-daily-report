package com.example.arxivdailyreport.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class PaperContent {
    @Id
    private Long paperId;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String rawText;

    @Column(length = 5000)
    private String parsedSummary;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "paper_id")
    private Paper paper;

    private LocalDateTime cachedAt;
}
