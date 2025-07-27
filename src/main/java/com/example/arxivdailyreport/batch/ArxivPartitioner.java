package com.example.arxivdailyreport.batch;

import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ArxivPartitioner implements Partitioner {
    private final int totalItems = 800_000;
    private final int range = 100;

    @NotNull
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        Map<String, ExecutionContext> partitionMap = new HashMap<>();
        for (int i = 0; i < gridSize; i++) {
            ExecutionContext context = new ExecutionContext();
            int start = i * range;
            int end = (i == gridSize - 1) ? totalItems : start + range;
            context.putInt("start", start);
            context.putInt("end", end);
            partitionMap.put("partition" + i, context);
        }
        return partitionMap;
    }
}
