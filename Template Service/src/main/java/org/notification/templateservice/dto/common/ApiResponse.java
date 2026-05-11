package org.notification.templateservice.dto.common;

import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;


@Getter
@Setter
public class ApiResponse<T> {
    private String requestId;
    private String message;
    private boolean success;
    private T data;
    private OffsetDateTime timestamp = OffsetDateTime.now();

    public static <T> ApiResponse<T> ok(T data) {
        ApiResponse<T> r = new ApiResponse<>();
        r.success = true;
        r.data    = data;
        return r;
    }
    public static <T> ApiResponse<T> ok(T data, String message) {
        ApiResponse<T> r = ok(data);
        r.message = message;
        return r;
    }
    public static <T> ApiResponse<T> error(String message) {
        ApiResponse<T> r = new ApiResponse<>();
        r.success = false;
        r.message = message;
        return r;
    }
    public ApiResponse<T> withRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }
}
