package org.notification.templateservice.mapper;

import org.notification.templateservice.dto.response.TemplateVersionResponse;
import org.notification.templateservice.entity.TemplateVersion;

public class VersionMapper {

    public static TemplateVersionResponse toVersionResponse(TemplateVersion version) {
        TemplateVersionResponse response = new TemplateVersionResponse();
        response.setId(version.getId());
        response.setTemplateId(version.getTemplate().getId());
        response.setVersion(version.getVersion());
        response.setSubject(version.getSubject());
        response.setBody(version.getBody());
        response.setPlaceholders(version.getPlaceholders());
        response.setMetadata(version.getMetadata());
        response.setActive(version.isActive());
        response.setChangeNotes(version.getChangeNotes());
        response.setCreatedBy(version.getCreatedBy());
        response.setCreatedAt(version.getCreatedAt());
        return response;
    }

    public static TemplateVersion toVersionEntity() {
        return null;
    }
}
