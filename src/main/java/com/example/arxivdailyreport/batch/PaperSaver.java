package com.example.arxivdailyreport.batch;

import com.example.arxivdailyreport.entity.Paper;
import com.example.arxivdailyreport.repository.PaperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaperSaver {
    private final PaperRepository paperRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(Paper paper) {
        paperRepository.save(paper);
    }
}
