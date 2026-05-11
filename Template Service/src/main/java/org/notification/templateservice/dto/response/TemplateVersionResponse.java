package org.notification.templateservice.dto.response;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.Map;

@Data
public class TemplateVersionResponse {
    private Long                    id;
    private Long                    templateId;
    private Integer                 version;
    private String                  subject;
    private String                  body;
    private Map<String, String>     placeholders;
    private Map<String, Object>     metadata;
    private boolean                 active;
    private String                  changeNotes;
    private String                  createdBy;
    private OffsetDateTime          createdAt;
}
