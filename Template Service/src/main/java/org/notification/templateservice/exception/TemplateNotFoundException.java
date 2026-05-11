package org.notification.templateservice.exception;

import lombok.Getter;

@Getter
public class TemplateNotFoundException extends RuntimeException {

    private final String lookupKey;

    public TemplateNotFoundException(String message, String lookupKey) {
        super(message);
        this.lookupKey = lookupKey;
    }
}
