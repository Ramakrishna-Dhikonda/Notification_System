package org.notification.templateservice.validation.validators;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.MustacheException;
import org.notification.templateservice.validation.TemplateValidator;
import org.notification.templateservice.validation.ValidationError;
import org.notification.templateservice.validation.ValidationResult;
import org.notification.templateservice.validation.model.NotificationTemplate;
import org.springframework.stereotype.Component;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class MustacheCompileValidator implements TemplateValidator {

        private final Mustache.Compiler compiler = Mustache.compiler().strictSections(true).defaultValue("");

        @Override
        public ValidationResult validate(NotificationTemplate template) {
            List<ValidationError> errors = new ArrayList<>();

            if (template.getBody() != null && !template.getBody().isBlank()) {
                compileAndCollect(template.getBody(), "body", template.getId(), errors);
            }

            if (template.getSubject() != null && !template.getSubject().isBlank()) {
                compileAndCollect(template.getSubject(), "subject", template.getId(), errors);
            }

            return errors.isEmpty() ? ValidationResult.valid() : ValidationResult.of(errors);
        }

        private void compileAndCollect(String templateText, String field, String templateId, List<ValidationError> errors) {
            try {
                compiler.compile(templateText);
            } catch (MustacheException ex) {
                errors.add(ValidationError.error("MUSTACHE_COMPILE_ERROR", field)
                        .message("Mustache compile error in [" + field + "] for template [" + templateId + "]: " + sanitize(ex.getMessage()))
                        .build());
            } catch (Exception ex) {
                errors.add(ValidationError.error("MUSTACHE_PARSE_ERROR", field)
                        .message("Unexpected parse error in [" + field + "]: " + sanitize(ex.getMessage()))
                        .build());
            }
        }

        private String sanitize(String msg) {
            if (msg == null) return "unknown error";
            return msg.length() > 300 ? msg.substring(0, 300) + "..." : msg;
        }
}