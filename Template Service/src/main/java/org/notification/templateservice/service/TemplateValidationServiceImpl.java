package org.notification.templateservice.service;

import org.notification.templateservice.enums.TemplateChannel;
import org.notification.templateservice.exception.TemplateValidationException;
import org.notification.templateservice.validation.ValidationResult;
import org.notification.templateservice.validation.model.NotificationTemplate;
import org.notification.templateservice.validation.validators.CompositeTemplateValidator;
import org.notification.templateservice.validation.validators.TemplateValidatorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class TemplateValidationServiceImpl implements TemplateValidationService {

    private static final Logger log = LoggerFactory.getLogger(TemplateValidationServiceImpl.class);

    private final TemplateValidatorFactory factory;

    public TemplateValidationServiceImpl(TemplateValidatorFactory factory) {
        this.factory = factory;
    }

    @Override
    public ValidationResult validate(NotificationTemplate template) {
        assertNotNull(template);
        Assert.notNull(template.getChannel(), "Template channel must not be null before validation");

        log.debug("Validating template [id={}, name='{}', channel={}]",
                template.getId(), template.getName(), template.getChannel());

        ValidationResult result = runValidation(template, template.getChannel());
        logResult(template, result);
        return result;
    }

    @Override
    public ValidationResult validateAndThrowIfInvalid(NotificationTemplate template) {
        ValidationResult result = validate(template);

        if (!result.isValid()) {
            throw new TemplateValidationException(template.getId(), result);
        }

        // Valid — may still have warnings. Return so caller can surface them.
        return result;
    }

    @Override
    public List<ValidationResult> validateBatch(List<NotificationTemplate> templates) {
        Objects.requireNonNull(templates, "Template list must not be null");

        log.info("Starting batch validation for {} templates", templates.size());

        List<ValidationResult> results = templates.stream()
                .map(this::validate)
                .collect(Collectors.toList());

        long invalid = results.stream().filter(r -> !r.isValid()).count();
        log.info("Batch validation complete: {}/{} templates are invalid", invalid, templates.size());

        return results;
    }

    @Override
    public ValidationResult validateForChannel(NotificationTemplate template, TemplateChannel channel) {
        assertNotNull(template);
        Objects.requireNonNull(channel, "Channel override must not be null");

        log.debug("Validating template [id={}] against channel [{}] (channel override)",
                template.getId(), channel);

        ValidationResult result = runValidation(template, channel);
        logResult(template, result);
        return result;
    }

    private ValidationResult runValidation(NotificationTemplate template, TemplateChannel channel) {
        CompositeTemplateValidator composite = factory.getValidator(channel);

        long start = System.currentTimeMillis();
        ValidationResult result = composite.validate(template);
        long elapsed = System.currentTimeMillis() - start;

        log.debug("Validator '{}' completed in {}ms for template [id={}]",
                composite.getName(), elapsed, template.getId());

        return result;
    }

    private void logResult(NotificationTemplate template, ValidationResult result) {
        if (!result.isValid()) {
            log.error("Template [id={}, channel={}] FAILED validation — {} error(s): {}",
                    template.getId(), template.getChannel(),
                    result.errorCount(),
                    result.getErrors().stream()
                            .map(e -> "[" + e.getField() + "] " + e.getErrorCode() + ": " + e.getMessage())
                            .collect(Collectors.joining(" | ")));
        } else if (result.hasWarnings()) {
            log.warn("Template [id={}, channel={}] valid with {} warning(s): {}",
                    template.getId(), template.getChannel(),
                    result.warningCount(),
                    result.getWarnings().stream()
                            .map(w -> w.getErrorCode() + ": " + w.getMessage())
                            .collect(Collectors.joining(" | ")));
        } else {
            log.info("Template [id={}, channel={}] passed all validation checks",
                    template.getId(), template.getChannel());
        }
    }

    private void assertNotNull(NotificationTemplate template) {
        Objects.requireNonNull(template, "Template must not be null");
    }
}
