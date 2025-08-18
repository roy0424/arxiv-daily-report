package com.example.arxivdailyreport.batch.rss.db;

import com.example.arxivdailyreport.dto.PaperDto;
import com.example.arxivdailyreport.entity.Category;
import com.example.arxivdailyreport.entity.Paper;
import com.example.arxivdailyreport.repository.CategoryRepository;
import com.example.arxivdailyreport.repository.PaperRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaperProcessorTest {

    @Mock
    private PaperRepository paperRepository;
    @Mock
    private CategoryRepository categoryRepository;

    private PaperProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new PaperProcessor(paperRepository, categoryRepository);
    }

    @Test
    void process_createsNewPaperWithCategories() {
        PaperDto dto = PaperDto.builder()
                .title("Title")
                .absLink("http://arxiv.org/abs/1")
                .pdfLink("http://arxiv.org/pdf/1.pdf")
                .summary("Summary")
                .updatedAt("2024-01-01")
                .fetchedAt("2024-01-02")
                .categories(List.of("cs.AI", "cs.LG"))
                .build();

        Category ai = Category.builder().id(1L).endpoint("cs.AI").name("AI").build();
        Category lg = Category.builder().id(2L).endpoint("cs.LG").name("LG").build();

        when(categoryRepository.findByEndpoint("cs.AI")).thenReturn(Optional.of(ai));
        when(categoryRepository.findByEndpoint("cs.LG")).thenReturn(Optional.of(lg));
        when(paperRepository.findByAbsLink(dto.getAbsLink())).thenReturn(Optional.empty());

        Paper result = processor.process(dto);
        assertNotNull(result);
        assertEquals(dto.getTitle(), result.getTitle());
        assertEquals(2, result.getCategories().size());
        assertTrue(result.getCategories().contains(ai));
        assertTrue(result.getCategories().contains(lg));
        assertEquals(LocalDate.parse("2024-01-01"), result.getUpdatedAt());
        assertEquals(LocalDate.parse("2024-01-02"), result.getFetchedAt());
    }
}
