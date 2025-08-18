package com.example.arxivdailyreport.batch.rss.db;

import com.example.arxivdailyreport.dto.PaperDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class JsonPaperReaderTest {

    @Test
    void read_deduplicatesByAbsLink() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonPaperReader reader = new JsonPaperReader(mapper, LocalDate.parse("2024-01-01"));

        PaperDto first = reader.read();
        assertNotNull(first);
        assertEquals("http://arxiv.org/abs/1", first.getAbsLink());

        PaperDto second = reader.read();
        assertNotNull(second);
        assertEquals("http://arxiv.org/abs/2", second.getAbsLink());

        assertNull(reader.read());
    }

    @Test
    void constructorThrowsWhenFileMissing() {
        ObjectMapper mapper = new ObjectMapper();
        assertThrows(IOException.class, () -> new JsonPaperReader(mapper, LocalDate.parse("2099-01-01")));
    }
}
