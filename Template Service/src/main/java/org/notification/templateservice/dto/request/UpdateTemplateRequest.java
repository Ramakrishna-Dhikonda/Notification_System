package org.notification.templateservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Data
public class UpdateTemplateRequest {
    @Size(max = 255)
    private String name;

    @Size(max = 1000)
    private String description;

    @Size(max = 1000)
    private String subject;

    @NotBlank(message = "body is required")
    private String body;

    private Map<String, String> placeholders;
    private Map<String, Object> metadata;
    private List<String> tags;

    @Size(max = 500)
    private String changeNotes;
}
