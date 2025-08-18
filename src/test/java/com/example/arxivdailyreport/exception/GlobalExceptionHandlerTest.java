package com.example.arxivdailyreport.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleBusinessException_returnsStructuredErrorResponse() {
        BusinessException ex = new BusinessException(ErrorCode.PAPER_NOT_FOUND);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/papers/1");

        ResponseEntity<ErrorResponse> response = handler.handleBusinessException(ex, request);

        assertEquals(ErrorCode.PAPER_NOT_FOUND.getStatus(), response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertNotNull(body);
        assertEquals(ErrorCode.PAPER_NOT_FOUND.getStatus().value(), body.status());
        assertEquals("/papers/1", body.path());
        assertEquals(ErrorCode.PAPER_NOT_FOUND.getMessage(), body.message());
    }
}
