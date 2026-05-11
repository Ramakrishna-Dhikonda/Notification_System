package org.notification.templateservice.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.Map;

@Entity
@Table(
        name = "template_versions",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_template_version",
                columnNames = {"template_id", "version"}
        )
)
@Data
public class TemplateVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "template_id", nullable = false)
    private Template template;

    @Column(nullable = false)
    private Integer version;

    /*
     * subject: Used only by EMAIL channel.
     * Mustache placeholders are supported here too.
     * e.g. "Your order {{orderId}} has been shipped!"
     */
    @Column(length = 1000)
    private String subject;

    /*
     * Mustache template body.
     * e.g. "Hello {{name}}, your order {{orderId}} is shipped."
     */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    /*
     * Declared placeholder map for pre-render validation.
     * e.g. {"name": "string", "orderId": "string"}
     * Stored as JSONB for GIN-indexed lookups.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSONB")
    private Map<String, String> placeholders;

    /*
     * Channel-specific config.
     * e.g. {"priority": "HIGH", "ttl": 3600, "encoding": "UTF-8"}
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "JSONB")
    private Map<String, Object> metadata;

    @Column(name = "is_active", nullable = false)
    private boolean active = false;

    @Column(name = "change_notes", length = 500)
    private String changeNotes;

    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false, columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }
}