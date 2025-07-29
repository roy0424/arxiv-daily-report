package com.example.arxivdailyreport.batch.rss.json;

import com.example.arxivdailyreport.entity.Category;
import com.example.arxivdailyreport.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.stereotype.Component;

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
            ExecutionContext context = new ExecutionContext();
            context.putLong("categoryId", category.getId());
            partitionMap.put("partition" + i, context);
        }

        return partitionMap;
    }
}
