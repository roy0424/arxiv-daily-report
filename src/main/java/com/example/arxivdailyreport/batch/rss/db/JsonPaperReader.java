package com.example.arxivdailyreport.batch.rss.db;

import com.example.arxivdailyreport.dto.PaperDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.item.ItemReader;

import java.io.IOException;
import java.nio.file.Files;
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

    public JsonPaperReader(ObjectMapper objectMapper, LocalDate today) throws IOException {
        Path path = Paths.get("rss/rss-" + today + ".json");
        if (!Files.exists(path)) {
            throw new IOException("RSS file for " + today + " not found: " + path.toAbsolutePath());
        }
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
