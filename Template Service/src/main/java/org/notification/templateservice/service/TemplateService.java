package org.notification.templateservice.service;

import org.notification.templateservice.adapter.NotificationTemplateAdapter;
import org.notification.templateservice.dto.filter.TemplateFilter;
import org.notification.templateservice.dto.request.CreateTemplateRequest;
import org.notification.templateservice.dto.request.UpdateTemplateRequest;
import org.notification.templateservice.dto.response.TemplateResponse;
import org.notification.templateservice.dto.response.TemplateVersionResponse;
import org.notification.templateservice.entity.Template;
import org.notification.templateservice.entity.TemplateVersion;
import org.notification.templateservice.enums.AuditAction;
import org.notification.templateservice.enums.TemplateChannel;
import org.notification.templateservice.enums.TemplateStatus;
import org.notification.templateservice.exception.TemplateDuplicateException;
import org.notification.templateservice.exception.TemplateInvalidStateException;
import org.notification.templateservice.exception.TemplateNotFoundException;
import org.notification.templateservice.mapper.TemplateMapper;
import org.notification.templateservice.mapper.VersionMapper;
import org.notification.templateservice.repository.TemplateRepository;
import org.notification.templateservice.repository.TemplateVersionRepository;
import org.notification.templateservice.specification.TemplateSpecification;
import org.notification.templateservice.validation.ValidationResult;
import org.notification.templateservice.validation.model.NotificationTemplate;
import org.notification.templateservice.validation.validators.TemplateValidatorFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TemplateService {
    private final TemplateRepository templateRepository;
    private final TemplateVersionRepository versionRepository;
    private final NotificationTemplateAdapter adapter;
    private final TemplateValidationService validationService;

    @Autowired
    public TemplateService(TemplateRepository templateRepository,
                           TemplateVersionRepository versionRepository,
                           TemplateValidationService validationService,
                           NotificationTemplateAdapter adapter) {
        this.templateRepository = templateRepository;
        this.versionRepository = versionRepository;
        this.validationService = validationService;
        this.adapter = adapter;
    }

    public Template getTemplateById(Long templateId) {
        return templateRepository.findById(templateId)
                .orElseThrow(RuntimeException::new);
    }

    public Page<TemplateResponse> listTemplates(TemplateFilter filter, Pageable pageable) {
        return templateRepository.findAll(TemplateSpecification.withFilters(filter), pageable)
                .map(f -> TemplateMapper.toResponse(f, null));
    }

    public TemplateResponse createTemplate(CreateTemplateRequest req, String createdBy, String requestId) {

        validateEmailSubject(req.getChannel(), req.getSubject());

        if (templateRepository.existsByEventTypeAndChannelAndLocale(req.getEventType(), req.getChannel(), req.getLocale())) {
            throw new TemplateDuplicateException("A template already exists for [" + req.getEventType() + " | " + req.getChannel() + " | " + req.getLocale() + "]");
        }
        NotificationTemplate domainTemplate = adapter.fromCreateRequest(req);
        ValidationResult validationResult   = validationService.validateAndThrowIfInvalid(domainTemplate);

        Template template = TemplateMapper.toEntity(req, createdBy);
        templateRepository.save(template);
        // Build and persist version 1
        TemplateVersion v1 = buildVersionEntity(template, 1, req.getSubject(),
                req.getBody(), req.getPlaceholders(), req.getMetadata(),
                req.getChangeNotes(), createdBy);
        versionRepository.save(v1);

        // Audit:: TODO (Missing auditing)

        return TemplateMapper.toResponse(template, validationResult);
    }

    @Transactional
    public TemplateResponse updateTemplate(Long id, UpdateTemplateRequest req, String updatedBy, String requestId) {

        Template template = findTemplateOrThrow(id);
        validateEmailSubject(template.getChannel(), req.getSubject());
        int nextVersion = versionRepository.findMaxVersionByTemplateId(id) + 1;
        // Deactivate current active version
        versionRepository.deactivateAllVersions(id);
        // Create new version (inactive by default — require explicit activation)
        TemplateVersion newVersion = buildVersionEntity(template, nextVersion, req.getSubject(), req.getBody(), req.getPlaceholders(), req.getMetadata(), req.getChangeNotes(), updatedBy);
        versionRepository.save(newVersion);

        // Update template header
        if (req.getName() != null)        template.setName(req.getName());
        if (req.getDescription() != null) template.setDescription(req.getDescription());
        if (req.getTags() != null)        template.setTags(req.getTags());
        template.setCurrentVersion(nextVersion);
        template.setUpdatedBy(updatedBy);
        templateRepository.save(template);

        //TODO: Audit missing

        return TemplateMapper.toResponse(template, null);
    }

    @Transactional
    public TemplateResponse changeStatus(Long id, TemplateStatus newStatus, String updatedBy, String requestId) {

        Template template = findTemplateOrThrow(id);

        if (template.getStatus() == newStatus)
            throw new TemplateInvalidStateException("Template is already in status [" + newStatus + "]");

        AuditAction action = (newStatus == TemplateStatus.ACTIVE) ? AuditAction.ACTIVATED : AuditAction.DEACTIVATED;
        // If activating: make the current version active in version table too
        if (newStatus == TemplateStatus.ACTIVE) {
            versionRepository.deactivateAllVersions(id);
            versionRepository.activateVersion(id, template.getCurrentVersion());
        }
        templateRepository.updateStatus(id, newStatus, updatedBy);
        template.setStatus(newStatus);
        //TODO: Audit
        return TemplateMapper.toResponse(template, null);
    }

    @Transactional(readOnly = true)
    public List<TemplateVersionResponse> getVersionHistory(Long id) {
        findTemplateOrThrow(id); // guard — throws 404 if template not found
        return versionRepository.findAllByTemplateIdOrderByVersionDesc(id)
                .stream()
                .map(VersionMapper::toVersionResponse)
                .toList();
    }

    private Template findTemplateOrThrow(Long id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> new TemplateNotFoundException("Template not found with id [" + id + "]", "id:" + id));
    }

    private void validateEmailSubject(TemplateChannel channel, String subject) {
        if (channel == TemplateChannel.EMAIL && (subject == null || subject.isBlank())) {
            throw new IllegalArgumentException("subject is required for EMAIL templates");
        }
    }

    private TemplateVersion buildVersionEntity(
            Template template, int version, String subject, String body,
            java.util.Map<String, String> placeholders,
            java.util.Map<String, Object> metadata,
            String changeNotes, String createdBy) {

        TemplateVersion v = new TemplateVersion();
        v.setTemplate(template);
        v.setVersion(version);
        v.setSubject(subject);
        v.setBody(body);
        v.setPlaceholders(placeholders);
        v.setMetadata(metadata);
        v.setChangeNotes(changeNotes);
        v.setCreatedBy(createdBy);
        v.setActive(false); // always starts inactive; activated via changeStatus
        return v;
    }
}
