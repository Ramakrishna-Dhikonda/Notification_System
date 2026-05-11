package org.notification.templateservice.adapter;

import org.notification.templateservice.dto.request.CreateTemplateRequest;
import org.notification.templateservice.dto.request.UpdateTemplateRequest;
import org.notification.templateservice.entity.Template;
import org.notification.templateservice.entity.TemplateVersion;
import org.notification.templateservice.enums.TemplateChannel;
import org.notification.templateservice.validation.model.NotificationTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class NotificationTemplateAdapter {

    public NotificationTemplate fromCreateRequest(CreateTemplateRequest req) {
        return NotificationTemplate.builder()
                .id("NEW:" + req.getEventType() + ":" + req.getChannel())
                .name(nullSafe(req.getName()))
                .channel(mapChannel(req.getChannel()))
                .subject(req.getSubject())
                .body(req.getBody())
                .placeholders(extractKeys(req.getPlaceholders()))
                //.metadata(toStringMap(req.getMetadata()))
                .locale(req.getLocale() != null ? req.getLocale() : "en")
                .version(1)
                .build();
    }

    public NotificationTemplate fromUpdateRequest(Template template, UpdateTemplateRequest req,
                                                  int nextVersion) {
        return NotificationTemplate.builder()
                .id(String.valueOf(template.getId()))
                .name(req.getName() != null ? req.getName() : template.getName())
                .channel(mapChannel(template.getChannel()))
                .subject(req.getSubject())
                .body(req.getBody())
                .placeholders(extractKeys(req.getPlaceholders()))
                //.metadata(toStringMap(req.getMetadata()))
                .locale(template.getLocale())
                .version(nextVersion)
                .build();
    }

    public NotificationTemplate fromEntities(Template template, TemplateVersion version) {
        return NotificationTemplate.builder()
                .id(String.valueOf(template.getId()))
                .name(template.getName())
                .channel(mapChannel(template.getChannel()))
                .subject(version.getSubject())
                .body(version.getBody())
                .placeholders(extractKeys(version.getPlaceholders()))
                //.metadata(toStringMap(version.getMetadata()))
                .locale(template.getLocale())
                .version(version.getVersion())
                .build();
    }

    private TemplateChannel mapChannel(TemplateChannel channel) {
        if (channel == null) return null;
        try {
            return TemplateChannel.valueOf(channel.name());
        } catch (IllegalArgumentException ex) {
            throw new IllegalStateException(
                    "TemplateChannel enum mismatch: enums." + channel.name()
                            + " has no corresponding constant in domain.TemplateChannel. "
                            + "Update domain.TemplateChannel to include [" + channel.name() + "].", ex);
        }
    }

    private Set<String> extractKeys(Map<String, String> placeholders) {
        if (placeholders == null || placeholders.isEmpty()) return Set.of();
        return Collections.unmodifiableSet(placeholders.keySet());
    }

    private Map<String, String> toStringMap(java.util.Map<String, String> map) {
        if (map == null || map.isEmpty()) return Map.of();
        return Collections.unmodifiableMap(map);
    }

    private String nullSafe(String value) {
        return value != null ? value : "";
    }
}
