package org.notification.templateservice.validation.validators.channel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.notification.templateservice.entity.Template;
import org.notification.templateservice.entity.TemplateVersion;
import org.notification.templateservice.validation.TemplateValidator;
import org.notification.templateservice.validation.ValidationError;
import org.notification.templateservice.validation.ValidationResult;
import org.notification.templateservice.validation.model.NotificationTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class PushTemplateValidator implements TemplateValidator {

    @Override
    public ValidationResult validate(NotificationTemplate template) {
        List<ValidationError> errors = new ArrayList<>();
        String subject = template.getSubject();
        String body = template.getBody();

        if (!StringUtils.hasText(subject)) {
            errors.add(ValidationError.warning("PUSH_SUBJECT_IGNORED", "subject")
                    .message("PUSH templates do not use a subject field; it will be ignored during dispatch")
                    .build()
            );
        }
        if (!StringUtils.hasText(body)) {
            return ValidationResult.of(errors);
        }
        //TODO: Implement the validation based on requirement!!
        return errors.isEmpty() ? ValidationResult.valid() : ValidationResult.of(errors);
    }

    @Override
    public String getName() {
        return PushTemplateValidator.class.getSimpleName();
    }
}
