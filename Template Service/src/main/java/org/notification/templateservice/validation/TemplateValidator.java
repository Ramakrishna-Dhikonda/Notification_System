package org.notification.templateservice.validation;

import org.notification.templateservice.entity.Template;
import org.notification.templateservice.entity.TemplateVersion;
import org.notification.templateservice.validation.model.NotificationTemplate;

@FunctionalInterface
public interface TemplateValidator {

    ValidationResult validate(NotificationTemplate templateVersion);

    default String getName() {
        return this.getClass().getSimpleName();
    }
}
