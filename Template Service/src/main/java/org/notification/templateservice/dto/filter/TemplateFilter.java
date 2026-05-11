package org.notification.templateservice.dto.filter;

import lombok.Builder;
import lombok.Getter;
import org.notification.templateservice.enums.TemplateChannel;
import org.notification.templateservice.enums.TemplateStatus;

import java.time.LocalDate;

@Getter
@Builder
public class TemplateFilter {
    private String eventType;
    private TemplateChannel channel;
    private TemplateStatus status;
    private String locale;
    private String name;
    private Boolean active;
    private String createdBy;
    private LocalDate createdDate;
}
