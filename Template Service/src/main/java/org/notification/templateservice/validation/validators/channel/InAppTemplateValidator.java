package org.notification.templateservice.validation.validators.channel;

import org.notification.templateservice.entity.TemplateVersion;
import org.notification.templateservice.validation.TemplateValidator;
import org.notification.templateservice.validation.ValidationError;
import org.notification.templateservice.validation.ValidationResult;
import org.notification.templateservice.validation.model.NotificationTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Component
public class InAppTemplateValidator implements TemplateValidator {

    private static final Pattern HTML_TAG        = Pattern.compile("<[a-zA-Z][^>]*>");
    private static final Pattern VALID_HEX_COLOR = Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$");
    private static final Pattern VALID_ICON_NAME = Pattern.compile("^[a-zA-Z0-9\\-]+$");
    private static final Pattern JSON_LIKE        = Pattern.compile("^\\s*\\{");

    @Override
    public ValidationResult validate(NotificationTemplate template) {
        List<ValidationError> errors = new ArrayList<>();
        String body     = template.getBody();
        String subject  = template.getSubject();
        Map<String, String> metadata = template.getMetadata();

        // -------------------------------------------------------
        // 1. Subject not applicable for IN_APP
        // -------------------------------------------------------
        if (subject != null && !subject.isBlank()) {
            errors.add(ValidationError.warning("IN_APP_SUBJECT_IGNORED", "subject")
                    .message("IN_APP templates do not use a subject field; it will be ignored during dispatch")
                    .build());
        }

        // -------------------------------------------------------
        // 2. Body must not contain HTML
        // -------------------------------------------------------
        if (body != null && HTML_TAG.matcher(body).find()) {
            errors.add(ValidationError.error("IN_APP_HTML_NOT_ALLOWED", "body")
                    .message("IN_APP body must be plain text. HTML tags are not rendered and may appear " +
                            "as raw markup to the user")
                    .build());
        }

        // -------------------------------------------------------
        // 3. Detect accidental PUSH format (JSON body)
        // -------------------------------------------------------
        if (body != null && JSON_LIKE.matcher(body).find()) {
            errors.add(ValidationError.warning("IN_APP_BODY_LOOKS_LIKE_JSON", "body")
                    .message("IN_APP body appears to be a JSON object. " +
                            "If this is a PUSH template, set channel=PUSH instead.")
                    .build());
        }

        // -------------------------------------------------------
        // 4. Metadata field validations (skip if no metadata)
        // -------------------------------------------------------
        if (metadata != null && !metadata.isEmpty()) {
            validateColor(metadata.get("color"), errors);
            validateIcon(metadata.get("icon"), errors);
            validateActionAndUrl(metadata.get("action"), metadata.get("actionUrl"), errors);
        }

        return errors.isEmpty() ? ValidationResult.valid() : ValidationResult.of(errors);
    }

    // -------------------------------------------------------
    // Private helpers
    // -------------------------------------------------------

    private void validateColor(String color, List<ValidationError> errors) {
        if (color == null || color.isBlank()) return;
        if (!VALID_HEX_COLOR.matcher(color).matches()) {
            errors.add(ValidationError.error("IN_APP_INVALID_COLOR", "metadata.color")
                    .message("color must be a valid CSS hex value (#RGB or #RRGGBB). Found: '" + color + "'")
                    .build());
        }
    }

    private void validateIcon(String icon, List<ValidationError> errors) {
        if (icon == null || icon.isBlank()) return;
        if (icon.length() > 60) {
            errors.add(ValidationError.error("IN_APP_ICON_TOO_LONG", "metadata.icon")
                    .message("icon name must not exceed 60 characters. Found: " + icon.length())
                    .build());
        }
        if (!VALID_ICON_NAME.matcher(icon).matches()) {
            errors.add(ValidationError.warning("IN_APP_ICON_INVALID_CHARS", "metadata.icon")
                    .message("icon name '" + icon + "' contains characters other than letters, digits, " +
                            "and hyphens. This may not resolve in the icon library.")
                    .build());
        }
    }

    private void validateActionAndUrl(String action, String actionUrl, List<ValidationError> errors) {
        boolean hasAction    = action    != null && !action.isBlank();
        boolean hasActionUrl = actionUrl != null && !actionUrl.isBlank();

        if (hasAction && !hasActionUrl) {
            errors.add(ValidationError.warning("IN_APP_ACTION_WITHOUT_URL", "metadata.actionUrl")
                    .message("metadata has 'action' = '" + action + "' but no 'actionUrl'. " +
                            "Tapping the notification will silently no-op.")
                    .build());
        }

        if (!hasAction && hasActionUrl) {
            errors.add(ValidationError.warning("IN_APP_URL_WITHOUT_ACTION", "metadata.action")
                    .message("metadata has 'actionUrl' but no 'action' constant. " +
                            "The URL is unreachable without an action to trigger it.")
                    .build());
        }

        if (hasActionUrl) {
            // Strip Mustache placeholders before path check
            String stripped = actionUrl.replaceAll("\\{\\{\\w+}}", "0");
            if (!stripped.startsWith("/")) {
                errors.add(ValidationError.error("IN_APP_ACTION_URL_INVALID", "metadata.actionUrl")
                        .message("actionUrl must be a relative path starting with '/'. Found: '" + actionUrl + "'")
                        .build());
            }
        }
    }
}
