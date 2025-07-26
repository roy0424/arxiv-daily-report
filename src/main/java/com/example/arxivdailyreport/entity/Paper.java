package com.example.arxivdailyreport.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Paper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(length = 2000)
    private String link;

    @Column(length = 10)
    private ArxivCategory category;

    @Column(length = 5000)
    private String description;

    private LocalDate fetchedAt;

    private boolean embedded;
    private boolean fullTextCached;

    public void markFullTextCached() {
        this.toBuilder()
                .fullTextCached(true)
                .build();
    }
}
