package com.example.arxivdailyreport.batch;


import com.example.arxivdailyreport.client.ArxivApiClient;
import com.example.arxivdailyreport.dto.PaperDto;
import org.springframework.batch.item.ItemReader;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ArxivPartitionedReader implements ItemReader<PaperDto> {
    private final Queue<PaperDto> buffer = new LinkedList<>();
    private final int start;
    private final int end;
    private final int pageSize;
    private int current;

    private ArxivApiClient arxivApiClient;

    public ArxivPartitionedReader(int start, int end, int pageSize, ArxivApiClient arxivApiClient) {
        this.start = start;
        this.end = end;
        this.pageSize = pageSize;
        this.current = start;
        this.arxivApiClient = arxivApiClient;
    }

    @Override
    public PaperDto read() {
        if (buffer.isEmpty()) {
            if (current >= end) {
                return null;
            }
            List<PaperDto> papers = arxivApiClient.fetchPapers(current, pageSize);
            buffer.addAll(papers);
            current += pageSize;
        }
        return buffer.poll();
    }
}
