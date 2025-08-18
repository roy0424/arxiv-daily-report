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
        // Previous implementation attempted to use Lombok's builder to set the flag, but
        // the result of the build operation was discarded. As a consequence the
        // `fullTextCached` field on the current entity instance remained `false`.
        // Mutate the existing instance directly so the change is persisted.
        this.fullTextCached = true;
    }
}
