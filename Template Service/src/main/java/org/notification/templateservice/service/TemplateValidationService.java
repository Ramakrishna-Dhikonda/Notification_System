package org.notification.templateservice.service;

import org.notification.templateservice.enums.TemplateChannel;
import org.notification.templateservice.validation.ValidationResult;
import org.notification.templateservice.validation.model.NotificationTemplate;

import java.util.List;


public interface TemplateValidationService {

    ValidationResult validate(NotificationTemplate template);

    ValidationResult validateAndThrowIfInvalid(NotificationTemplate template);

    List<ValidationResult> validateBatch(List<NotificationTemplate> templates);

    ValidationResult validateForChannel(NotificationTemplate template, TemplateChannel channel);
}
