package com.example.arxivdailyreport.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    INVALID_CATEGORY(HttpStatus.BAD_REQUEST, "지원하지 않는 Arxiv 카테고리입니다."),
    PAPER_NOT_FOUND(HttpStatus.NOT_FOUND, "논문을 찾을 수 없습니다."),
    FULLTEXT_ALREADY_CACHED(HttpStatus.CONFLICT, "이미 본문이 저장된 논문입니다."),
    FAILED_TO_FETCH_RSS(HttpStatus.INTERNAL_SERVER_ERROR, "Arxiv RSS 크롤링 실패");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
