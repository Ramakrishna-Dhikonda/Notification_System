package org.notification.templateservice.mapper;

import org.notification.templateservice.dto.request.CreateTemplateRequest;
import org.notification.templateservice.dto.response.TemplateResponse;
import org.notification.templateservice.entity.Template;
import org.notification.templateservice.enums.TemplateStatus;
import org.notification.templateservice.validation.ValidationResult;

import java.time.OffsetDateTime;
import java.util.ArrayList;

public class TemplateMapper {

    public static TemplateResponse toResponse(Template entity, ValidationResult validationResult) {
        return TemplateResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .eventType(entity.getEventType())
                .channel(entity.getChannel())
                .locale(entity.getLocale())
                .currentVersion(entity.getCurrentVersion())
                .status(entity.getStatus())
                .tags(entity.getTags())
                .warnings(validationResult == null ? null : validationResult.getWarnings()
                                                            .stream()
                                                            .map(w -> "[" + w.getField() + "] " + w.getErrorCode() + ": " + w.getMessage())
                                                            .toList()
                )
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public static Template toEntity(CreateTemplateRequest request, String createdBy) {
        Template template = new Template();
        template.setName(request.getName());
        template.setDescription(request.getDescription());
        template.setEventType(request.getEventType().toUpperCase());
        template.setChannel(request.getChannel());
        template.setLocale(request.getLocale());
        template.setStatus(TemplateStatus.DRAFT);
        template.setCurrentVersion(1);
        template.setTags(request.getTags());
        template.setCreatedBy(createdBy);
        template.setCreatedAt(OffsetDateTime.now());
        template.setUpdatedAt(OffsetDateTime.now());
        return template;
    }
}
