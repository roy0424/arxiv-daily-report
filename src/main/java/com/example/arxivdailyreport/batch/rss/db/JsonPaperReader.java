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
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.function.Function;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class JsonPaperReader implements ItemReader<PaperDto> {
    private final Queue<PaperDto> buffer;

    public JsonPaperReader(ObjectMapper objectMapper) throws IOException {
        LocalDate today = LocalDate.now();
        Path path = Paths.get("rss/rss-" + "2025-07-29" + ".json");
        List<PaperDto> list = objectMapper.readValue(path.toFile(), new TypeReference<>() {});

        Collection<PaperDto> deduplicated = list.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(
                        PaperDto::getAbsLink,
                        Function.identity(),
                        (existing, duplicate) -> existing
                ))
                .values();

        this.buffer = new LinkedList<>(deduplicated);
    }

    @Override
    public PaperDto read() {
        return buffer.poll();
    }
}
