package org.notification.templateservice.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import org.notification.templateservice.enums.TemplateStatus;

@Data
public class ChangeStatusRequest {
    @NotNull(message = "status is required")
    private TemplateStatus status;
}
