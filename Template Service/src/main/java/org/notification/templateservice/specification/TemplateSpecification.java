package org.notification.templateservice.specification;

import jakarta.persistence.criteria.Predicate;
import org.notification.templateservice.dto.filter.TemplateFilter;
import org.notification.templateservice.entity.Template;
import org.notification.templateservice.enums.TemplateChannel;
import org.notification.templateservice.enums.TemplateStatus;
import org.notification.templateservice.utils.AppConstants;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

public class TemplateSpecification {

    public static Specification<Template> withFilters(TemplateFilter filter) {

        return (root, query, cb) -> {
            var predicates = new ArrayList<Predicate>();

            if (StringUtils.hasText(filter.getEventType()))
                predicates.add(cb.equal(root.get(AppConstants.Template.FIELD_EVENT_TYPE), filter.getEventType()));
            if (filter.getChannel() != null)
                predicates.add(cb.equal(root.get(AppConstants.Template.FIELD_CHANNEL), filter.getChannel()));
            if (filter.getStatus() != null)
                predicates.add(cb.equal(root.get(AppConstants.Template.FIELD_STATUS), filter.getStatus()));
            if (StringUtils.hasText(filter.getLocale()))
                predicates.add(cb.equal(root.get(AppConstants.Template.FIELD_LOCALE), filter.getLocale()));

            return predicates.isEmpty() ? cb.conjunction() : cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static Specification<Template> hasEventType(String eventType) {
        return (root, query, cb) ->
                eventType == null ? null : cb.equal(root.get("eventType"), eventType);
    }
    private static Specification<Template> hasChannel(TemplateChannel channel) {
        return (root, query, cb) ->
                channel == null ? null : cb.equal(root.get("channel"), channel);
    }
    private static Specification<Template> hasStatus(TemplateStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }
    private static Specification<Template> hasLocale(String locale) {
        return (root, query, cb) ->
                locale == null ? null : cb.equal(root.get("locale"), locale);
    }
}