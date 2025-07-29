package com.example.arxivdailyreport.batch.rss.json;

import com.example.arxivdailyreport.dto.PaperDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@RequiredArgsConstructor
public class RssJsonWriter implements ItemWriter<List<PaperDto>> {
    private final ConcurrentLinkedQueue<PaperDto> collector;

    @Override
    public void write(Chunk<? extends List<PaperDto>> items) {
        for (List<PaperDto> batch : items) {
            collector.addAll(batch);
        }
    }
}
