package org.notification.templateservice.validation.validators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.notification.templateservice.enums.TemplateChannel;
import org.notification.templateservice.validation.ValidationError;
import org.notification.templateservice.validation.ValidationResult;
import org.notification.templateservice.validation.model.NotificationTemplate;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PlaceholderSyntaxValidatorTest {

    private PlaceholderSyntaxValidator validator;

    @BeforeEach
    void setUp() {
        validator = new PlaceholderSyntaxValidator();
    }

    @Test
    void shouldReturnValid_whenNoPlaceholdersUsedAndDeclared() {
        NotificationTemplate template = NotificationTemplate.builder()
                .id("test-id")
                .channel(TemplateChannel.EMAIL)
                .body("Hello world")
                .subject("No placeholder")
                .placeholders(Set.of())
                .build();

        ValidationResult result = validator.validate(template);

        assertTrue(result.isValid());
        assertFalse(result.hasWarnings());
    }

    @Test
    void shouldReturnValid_whenDeclaredMatchesUsed() {
        NotificationTemplate template = NotificationTemplate.builder()
                .id("test-id")
                .channel(TemplateChannel.EMAIL)
                .body("Hello {{name}}, your OTP is {{otp}}")
                .subject("Update for {{name}}")
                .placeholders(Set.of("name", "otp"))
                .build();

        ValidationResult result = validator.validate(template);

        assertTrue(result.isValid());
        assertFalse(result.hasWarnings());
    }

    @Test
    void shouldReturnError_whenPlaceholderUsedInBodyButNotDeclared() {
        NotificationTemplate template = NotificationTemplate.builder()
                .id("test-id")
                .channel(TemplateChannel.EMAIL)
                .body("Hello {{name}} and {{undeclared}}")
                .subject("Update for {{name}}")
                .placeholders(Set.of("name"))
                .build();

        ValidationResult result = validator.validate(template);

        assertFalse(result.isValid());
        assertEquals(1, result.errorCount());
        
        ValidationError error = result.getErrors().get(0);
        assertEquals("UNDECLARED_PLACEHOLDER", error.getErrorCode());
        assertEquals("body", error.getField());
        assertTrue(error.getMessage().contains("undeclared"));
    }

    @Test
    void shouldReturnError_whenPlaceholderUsedInSubjectButNotDeclared() {
        NotificationTemplate template = NotificationTemplate.builder()
                .id("test-id")
                .channel(TemplateChannel.EMAIL)
                .body("Hello {{name}}")
                .subject("Update for {{undeclared}}")
                .placeholders(Set.of("name"))
                .build();

        ValidationResult result = validator.validate(template);

        assertFalse(result.isValid());
        assertEquals(1, result.errorCount());

        ValidationError error = result.getErrors().get(0);
        assertEquals("UNDECLARED_PLACEHOLDER", error.getErrorCode());
        assertEquals("subject", error.getField());
        assertTrue(error.getMessage().contains("undeclared"));
    }

    @Test
    void shouldReturnWarning_whenDeclaredButNotUsed() {
        NotificationTemplate template = NotificationTemplate.builder()
                .id("test-id")
                .channel(TemplateChannel.EMAIL)
                .body("Hello {{name}}")
                .subject("Welcome")
                .placeholders(Set.of("name", "unused"))
                .build();

        ValidationResult result = validator.validate(template);

        assertTrue(result.isValid());
        assertTrue(result.hasWarnings());
        assertEquals(1, result.warningCount());

        ValidationError warning = result.getWarnings().get(0);
        assertEquals("UNUSED_PLACEHOLDER", warning.getErrorCode());
        assertEquals("placeholders", warning.getField());
        assertTrue(warning.getMessage().contains("unused"));
    }

    @Test
    void shouldReturnValid_whenNestedDottedPropertyDeclaredByRoot() {
        NotificationTemplate template = NotificationTemplate.builder()
                .id("test-id")
                .channel(TemplateChannel.EMAIL)
                .body("Hello {{user.profile.name}}")
                .placeholders(Set.of("user"))
                .build();

        ValidationResult result = validator.validate(template);

        assertTrue(result.isValid());
        assertFalse(result.hasWarnings());
    }

    @Test
    void shouldIgnoreMustacheBuiltIns() {
        NotificationTemplate template = NotificationTemplate.builder()
                .id("test-id")
                .channel(TemplateChannel.EMAIL)
                .body("Items: {{#items}} {{.}} {{/items}} or {{this}}")
                .placeholders(Set.of("items"))
                .build();

        ValidationResult result = validator.validate(template);

        assertTrue(result.isValid());
        assertFalse(result.hasWarnings());
    }

    @Test
    void shouldIgnoreSubjectForNonEmailChannels() {
        NotificationTemplate template = NotificationTemplate.builder()
                .id("test-id")
                .channel(TemplateChannel.SMS)
                .body("Hello {{name}}")
                .subject("Ignore {{undeclared}}")
                .placeholders(Set.of("name"))
                .build();

        ValidationResult result = validator.validate(template);

        assertTrue(result.isValid());
        assertFalse(result.hasWarnings());
    }
}
