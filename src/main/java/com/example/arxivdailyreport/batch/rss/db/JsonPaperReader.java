package com.example.arxivdailyreport.batch.rss.db;

import com.example.arxivdailyreport.dto.PaperDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemReader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@RequiredArgsConstructor
public class JsonPaperReader implements ItemReader<PaperDto> {
    private final Queue<PaperDto> buffer;

    public JsonPaperReader(ObjectMapper objectMapper) throws IOException {
        LocalDate today = LocalDate.now();
        Path path = Paths.get("rss/rss-" + today + ".json");
        List<PaperDto> list = objectMapper.readValue(path.toFile(), new TypeReference<>() {});
        this.buffer = new LinkedList<>(list);
    }

    @Override
    public PaperDto read() {
        return buffer.poll();
    }
}
