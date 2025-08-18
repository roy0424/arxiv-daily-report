package com.example.arxivdailyreport.batch.rss.json;

import com.example.arxivdailyreport.entity.Category;
import com.example.arxivdailyreport.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CategoryPartitioner implements Partitioner {
    private final CategoryRepository categoryRepository;

    @NotNull
    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        List<Category> categories = categoryRepository.findAll();
        Map<String, ExecutionContext> partitionMap = new HashMap<>();

        for (int i = 0; i < categories.size(); i++) {
            Category category = categories.get(i);
            int partitionIndex = i % gridSize;
            String partitionKey = "partition" + partitionIndex;

            ExecutionContext context = partitionMap.computeIfAbsent(partitionKey, k -> new ExecutionContext());
            @SuppressWarnings("unchecked")
            List<Long> categoryIds = (List<Long>) context.get("categoryIds");
            if (categoryIds == null) {
                categoryIds = new ArrayList<>();
            }
            categoryIds.add(category.getId());
            context.put("categoryIds", categoryIds);
        }

        return partitionMap;
    }
}
