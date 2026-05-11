package org.notification.templateservice.exception;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TemplateDuplicateException extends RuntimeException {

    public TemplateDuplicateException(String message) {
        super(message);
    }
}
