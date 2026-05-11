package org.notification.templateservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.notification.templateservice.enums.TemplateChannel;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class CreateTemplateRequest {
    @NotBlank(message = "name is required")
    @Size(max = 255)
    private String name;

    @Size(max = 1000)
    private String description;

    @NotBlank(message = "eventType is required")
    @Size(max = 100)
    private String eventType;

    @NotNull(message = "channel is required")
    private TemplateChannel channel;

    @Size(max = 20)
    private String locale = "en";

    /*
     * Subject is mandatory for EMAIL channel.
     * Enforced in the service layer (not here) because we need the channel value.
     * Mustache placeholders are supported: "Your order {{orderId}} has shipped!"
     */
    @Size(max = 1000)
    private String subject;

    @NotBlank(message = "body is required")
    private String body;

    /*
     * Declared placeholder key → type map.
     * e.g. {"name": "string", "orderId": "string", "amount": "number"}
     * Used for pre-render validation: ensures callers provide all required keys.
     */
    private Map<String, String> placeholders;

    /*
     * Channel-specific config.
     * e.g. {"priority": "HIGH", "ttl": 3600}
     */
    private Map<String, Object> metadata;

    private List<String> tags;

    @Size(max = 500)
    private String changeNotes;
}
