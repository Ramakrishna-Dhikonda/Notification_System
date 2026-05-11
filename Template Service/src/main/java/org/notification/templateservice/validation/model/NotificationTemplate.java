package org.notification.templateservice.validation.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.notification.templateservice.enums.TemplateChannel;

import java.util.Map;
import java.util.Set;

@Builder
@Getter
@Data
public class NotificationTemplate {
    private String id;
    private String name;
    private TemplateChannel channel;
    private String subject;       // Optional: only for EMAIL
    private String body;
    private Set<String> placeholders;  // e.g. {{name}}, {{otp}}
    private Map<String, String> metadata;
    private boolean active;
    private String locale;
    private int version;
}
