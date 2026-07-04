package org.notification.templateservice.validation.validators;

import com.samskivert.mustache.Mustache;
import org.notification.templateservice.enums.TemplateChannel;
import org.notification.templateservice.validation.TemplateValidator;
import org.notification.templateservice.validation.ValidationError;
import org.notification.templateservice.validation.ValidationResult;
import org.notification.templateservice.validation.model.NotificationTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PlaceholderSyntaxValidator implements TemplateValidator {

    @Override
    public ValidationResult validate(NotificationTemplate template) {
        List<ValidationError> errors = new ArrayList<>();
        Set<String> declaredPlaceholders = template.getPlaceholders() != null ? template.getPlaceholders() : Set.of();

        // 1. Extract used placeholders from body
        Set<String> bodyPlaceholders = extractPlaceholders(template.getBody());
        
        // 2. Extract used placeholders from subject (only for EMAIL channel)
        Set<String> subjectPlaceholders = Set.of();
        if (template.getChannel() == TemplateChannel.EMAIL) {
            subjectPlaceholders = extractPlaceholders(template.getSubject());
        }

        // Validate placeholders used in body
        validateUsedPlaceholders(bodyPlaceholders, declaredPlaceholders, "body", template.getId(), errors);

        // Validate placeholders used in subject
        if (template.getChannel() == TemplateChannel.EMAIL) {
            validateUsedPlaceholders(subjectPlaceholders, declaredPlaceholders, "subject", template.getId(), errors);
        }

        // Combine used placeholders to check for unused declared placeholders
        Set<String> allUsedPlaceholders = new HashSet<>(bodyPlaceholders);
        allUsedPlaceholders.addAll(subjectPlaceholders);

        // Validate declared placeholders that are not used in body or subject
        validateUnusedPlaceholders(declaredPlaceholders, allUsedPlaceholders, errors);

        return errors.isEmpty() ? ValidationResult.valid() : ValidationResult.of(errors);
    }

    private void validateUsedPlaceholders(Set<String> used, Set<String> declared, String field, String templateId, List<ValidationError> errors) {
        for (String u : used) {
            if (!isDeclared(u, declared)) {
                errors.add(ValidationError.error("UNDECLARED_PLACEHOLDER", field)
                        .message("Placeholder '" + u + "' is used in [" + field + "] for template [" + templateId + "] but not declared in placeholders map")
                        .build());
            }
        }
    }

    private boolean isDeclared(String usedPlaceholder, Set<String> declared) {
        if (declared.contains(usedPlaceholder)) {
            return true;
        }
        // Support dotted nested properties: if template uses user.name, we check if user is declared
        if (usedPlaceholder.contains(".")) {
            String[] parts = usedPlaceholder.split("\\.");
            if (parts.length > 0 && declared.contains(parts[0])) {
                return true;
            }
        }
        return false;
    }

    private void validateUnusedPlaceholders(Set<String> declared, Set<String> used, List<ValidationError> errors) {
        for (String d : declared) {
            boolean isUsed = used.contains(d) || used.stream().anyMatch(u -> u.startsWith(d + "."));
            if (!isUsed) {
                errors.add(ValidationError.warning("UNUSED_PLACEHOLDER", "placeholders")
                        .message("Declared placeholder '" + d + "' is not used in the template text")
                        .build());
            }
        }
    }

    private Set<String> extractPlaceholders(String text) {
        if (text == null || text.isBlank()) {
            return Set.of();
        }
        Set<String> variables = new HashSet<>();
        try {
            com.samskivert.mustache.Template tmpl = Mustache.compiler().compile(text);
            tmpl.visit(new Mustache.Visitor() {
                @Override
                public void visitText(String text) {
                }

                @Override
                public void visitVariable(String name) {
                    // Ignore built-in variables like "." or "this"
                    if (!name.equals(".") && !name.equalsIgnoreCase("this")) {
                        variables.add(name);
                    }
                }

                @Override
                public boolean visitSection(String name) {
                    if (!name.equals(".") && !name.equalsIgnoreCase("this")) {
                        variables.add(name);
                    }
                    return true;
                }

                @Override
                public boolean visitInvertedSection(String name) {
                    if (!name.equals(".") && !name.equalsIgnoreCase("this")) {
                        variables.add(name);
                    }
                    return true;
                }

                @Override
                public boolean visitInclude(String name) {
                    return true;
                }
            });
        } catch (Exception ex) {
            // Ignore compiler syntax errors here (handled by MustacheCompileValidator)
        }
        return variables;
    }

    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }
}
