package org.notification.templateservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.notification.templateservice.dto.common.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TemplateNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(TemplateNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<Void>error(ex.getMessage()).withRequestId(extractRequestId(request)));
    }
    @ExceptionHandler(TemplateDuplicateException.class)
    public ResponseEntity<ApiResponse<Void>> handleTemplateDuplicateException(TemplateDuplicateException ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.<Void>error(ex.getMessage()).withRequestId(extractRequestId(request)));
    }
    @ExceptionHandler(TemplateInvalidStateException.class)
    public ResponseEntity<ApiResponse<Void>> handleTemplateInvalidStateException(TemplateInvalidStateException ex, HttpServletRequest request) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.<Void>error(ex.getMessage())
                        .withRequestId(extractRequestId(request)));
    }

    private String extractRequestId(HttpServletRequest request) {
        return request.getHeader("X-Request-ID");
    }
}
