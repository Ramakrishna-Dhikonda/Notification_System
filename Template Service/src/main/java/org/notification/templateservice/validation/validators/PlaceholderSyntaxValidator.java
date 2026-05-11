package org.notification.templateservice.validation.validators;

import org.notification.templateservice.entity.TemplateVersion;
import org.notification.templateservice.validation.TemplateValidator;
import org.notification.templateservice.validation.ValidationResult;
import org.notification.templateservice.validation.model.NotificationTemplate;
import org.springframework.stereotype.Component;

@Component
public class PlaceholderSyntaxValidator implements TemplateValidator {

    @Override
    public ValidationResult validate(NotificationTemplate template) {
        return null;
    }

    @Override
    public String getName() {
        return TemplateValidator.class.getSimpleName();
    }
}
