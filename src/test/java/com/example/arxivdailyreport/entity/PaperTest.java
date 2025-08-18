package com.example.arxivdailyreport.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaperTest {
    @Test
    void markFullTextCached_setsFlagTrue() {
        Paper paper = Paper.builder()
                .title("t")
                .absLink("https://arxiv.org/abs/1234")
                .pdfLink("https://arxiv.org/pdf/1234")
                .summary("summary")
                .embedded(false)
                .fullTextCached(false)
                .build();

        assertFalse(paper.isFullTextCached());

        paper.markFullTextCached();

        assertTrue(paper.isFullTextCached());
    }
}
