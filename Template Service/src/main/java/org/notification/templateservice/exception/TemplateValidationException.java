package org.notification.templateservice.exception;

import lombok.Getter;
import org.notification.templateservice.validation.ValidationResult;

@Getter
public class TemplateValidationException extends RuntimeException {
    private final ValidationResult validationResult;
    private final String templateId;

    public TemplateValidationException(String templateId, ValidationResult result) {
        super(buildMessage(templateId, result));
        this.templateId       = templateId;
        this.validationResult = result;
    }

    private static String buildMessage(String templateId, ValidationResult result) {
        return "Template '" + templateId + "' failed validation with " +
                result.errorCount() + " error(s) and " +
                result.warningCount() + " warning(s)";
    }
}
