package org.notification.templateservice.validation.validators;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.notification.templateservice.validation.ValidationResult;
import org.notification.templateservice.validation.model.NotificationTemplate;

import static org.junit.jupiter.api.Assertions.*;

class MustacheCompileValidatorTest {
    private MustacheCompileValidator mustacheCompileValidator;

    @BeforeEach
    void setUp() {
        this.mustacheCompileValidator = new MustacheCompileValidator();
    }

    @Test
    void shouldReturnValid_whenBodyAndSubjectAreValid() {
        NotificationTemplate template = NotificationTemplate.builder()
                .id("welcome-template")
                .body("Hello {{name}}")
                .subject("Welcome {{user}}")
                .build();

        ValidationResult result = mustacheCompileValidator.validate(template);

        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
    }
}