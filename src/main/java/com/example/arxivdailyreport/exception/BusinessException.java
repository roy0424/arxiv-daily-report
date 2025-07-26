package com.example.arxivdailyreport.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final ErrorCode errorcode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorcode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String detailMessage) {
        super(detailMessage);
        this.errorcode = errorCode;
    }
}
