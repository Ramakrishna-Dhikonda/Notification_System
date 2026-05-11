package org.notification.templateservice.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.notification.templateservice.dto.common.ApiResponse;
import org.notification.templateservice.dto.filter.TemplateFilter;
import org.notification.templateservice.dto.request.ChangeStatusRequest;
import org.notification.templateservice.dto.request.CreateTemplateRequest;
import org.notification.templateservice.dto.request.UpdateTemplateRequest;
import org.notification.templateservice.dto.response.PagedResponse;
import org.notification.templateservice.dto.response.TemplateResponse;
import org.notification.templateservice.dto.response.TemplateVersionResponse;
import org.notification.templateservice.entity.Template;
import org.notification.templateservice.enums.TemplateChannel;
import org.notification.templateservice.enums.TemplateStatus;
import org.notification.templateservice.service.TemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/templates")
public class TemplateController {

    private final TemplateService templateService;

    @Autowired
    public TemplateController(TemplateService templateService) {
        this.templateService = templateService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TemplateResponse>> createTemplate(@Valid @RequestBody CreateTemplateRequest request,
                                                                        HttpServletRequest httpRequest) {
        String requestId = httpRequest.getHeader("X-Request-ID");
        TemplateResponse response = templateService.createTemplate(request, "AD-Test", requestId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok(response, "Template created successfully").withRequestId(requestId));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<TemplateResponse>>> listTemplates(@RequestParam(required = false) String eventType,
                                                                                      @RequestParam(required = false) TemplateChannel channel,
                                                                                      @RequestParam(required = false) TemplateStatus status,
                                                                                      @PageableDefault(sort = "createdAt") Pageable pageable,
                                                                                      HttpServletRequest servletRequest) {
        TemplateFilter templateFilter = TemplateFilter.builder()
                .eventType(eventType)
                .channel(channel)
                .status(status)
                .build();
        Page<TemplateResponse> pageResponse = templateService.listTemplates(templateFilter, pageable);
        return ResponseEntity.ok(ApiResponse.ok(PagedResponse.from(pageResponse)).withRequestId(servletRequest.getHeader("X-Request-ID")));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Template>> getTemplate(@PathVariable Long id, HttpServletRequest servletRequest) {
        return ResponseEntity.ok(ApiResponse.ok(templateService.getTemplateById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<TemplateResponse>> updateTemplate(@PathVariable Long id,
                                                                        @Valid @RequestBody UpdateTemplateRequest request,
                                                                        HttpServletRequest httpRequest) {

        String requestId = httpRequest.getHeader("X-Request-ID");
        TemplateResponse response = templateService.updateTemplate(id, request, "ADTest", requestId);

        return ResponseEntity.ok(ApiResponse.ok(response, "Template updated. New version created.").withRequestId(requestId));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<TemplateResponse>> changeStatus(@PathVariable Long id,
                                                                      @Valid @RequestBody ChangeStatusRequest request,
                                                                      HttpServletRequest httpRequest) {

        String requestId = httpRequest.getHeader("X-Request-ID");
        TemplateResponse response = templateService.changeStatus(id, request.getStatus(), "ADTest", requestId);
        return ResponseEntity.ok(ApiResponse.ok(response, "Status changed to " + request.getStatus()).withRequestId(requestId));
    }

    @GetMapping("/{id}/versions")
    public ResponseEntity<ApiResponse<List<TemplateVersionResponse>>> getVersionHistory(@PathVariable Long id,
                                                                                        HttpServletRequest httpRequest) {
        List<TemplateVersionResponse> versions = templateService.getVersionHistory(id);
        return ResponseEntity.ok(ApiResponse.ok(versions).withRequestId(httpRequest.getHeader("X-Request-ID")));
    }
}
