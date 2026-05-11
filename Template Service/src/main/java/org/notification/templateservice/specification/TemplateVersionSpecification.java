package org.notification.templateservice.specification;

import org.notification.templateservice.entity.TemplateVersion;
import org.notification.templateservice.enums.TemplateChannel;
import org.springframework.data.jpa.domain.Specification;

public class TemplateVersionSpecification {

    public static Specification<TemplateVersion> withFilters(String eventType, TemplateChannel channel, String locale) {
        return Specification
                .where(hasEventType(eventType))
                .and(hasChannel(channel))
                .and(hashLocale(locale));
    }
    private static Specification<TemplateVersion> hasEventType(String eventType) {
        return (root, query, cb) ->
                eventType == null ? null : cb.equal(root.get("eventType"), eventType);
    }

    private static Specification<TemplateVersion> hasChannel(TemplateChannel channel) {
        return (root, query, cb) ->
                channel == null ? null : cb.equal(root.get("channel"), channel);
    }

    private static Specification<TemplateVersion> hashLocale(String locale) {
        return (root, query, cb) ->
                locale == null ? null : cb.equal(root.get("locale"), locale);
    }
}
