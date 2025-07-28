package com.example.arxivdailyreport.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class Paper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, unique = true)
    private String absLink;

    @Column(nullable = false, unique = true)
    private String pdfLink;

    @Column(length = 5000)
    private String summary;

    private LocalDate updatedAt;
    private LocalDate fetchedAt;

    private boolean embedded;
    private boolean fullTextCached;

    @Builder.Default
    @OneToMany(mappedBy = "paper", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaperCategory> paperCategories = new ArrayList<>();

    public List<Category> getCategories() {
        return paperCategories.stream()
                .map(PaperCategory::getCategory)
                .toList();
    }

    public void markFullTextCached() {
        this.toBuilder()
                .fullTextCached(true)
                .build();
    }
}
