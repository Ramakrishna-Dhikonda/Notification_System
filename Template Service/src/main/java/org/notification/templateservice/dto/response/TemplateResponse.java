package org.notification.templateservice.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.notification.templateservice.enums.TemplateChannel;
import org.notification.templateservice.enums.TemplateStatus;

import java.time.OffsetDateTime;
import java.util.List;

@Builder
@Getter
public class TemplateResponse {
    private Long            id;
    private String          name;
    private String          description;
    private String          eventType;
    private TemplateChannel channel;
    private String          locale;
    private Integer         currentVersion;
    private TemplateStatus  status;
    private List<String>    tags;
    private List<String>    warnings;
    private String          createdBy;
    private String          updatedBy;
    private OffsetDateTime  createdAt;
    private OffsetDateTime  updatedAt;
}
