package org.notification.templateservice.repository;

import org.notification.templateservice.entity.TemplateVersion;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemplateVersionRepository extends JpaRepository<TemplateVersion, Long> {
    @Query("""
        SELECT v FROM TemplateVersion v
        WHERE v.template.id = :templateId
          AND v.active = true
    """)
    Optional<TemplateVersion> findActiveVersion(@Param("templateId") Long templateId);

    /**
     * Fetches a specific version — used by rollback and admin version detail.
     */
    Optional<TemplateVersion> findByTemplateIdAndVersion(Long templateId, Integer version);

    /**
     * Full version history for a template, newest first.
     * Used by admin version history API.
     */
    @Query("""
        SELECT v FROM TemplateVersion v
        WHERE v.template.id = :templateId
        ORDER BY v.version DESC
    """)
    List<TemplateVersion> findAllByTemplateIdOrderByVersionDesc(@Param("templateId") Long templateId);

    /**
     * Gets the highest version number for a template.
     * Used to compute next version number during update.
     */
    @Query("""
        SELECT COALESCE(MAX(v.version), 0) FROM TemplateVersion v
        WHERE v.template.id = :templateId
    """)
    Integer findMaxVersionByTemplateId(@Param("templateId") Long templateId);

    /**
     * Deactivates all versions of a template.
     * Called before activating a specific version (rollback / update flow).
     */
    @Modifying
    @Query("""
        UPDATE TemplateVersion v
        SET v.active = false
        WHERE v.template.id = :templateId
    """)
    int deactivateAllVersions(@Param("templateId") Long templateId);

    /**
     * Activates a specific version of a template.
     * Always call deactivateAllVersions first to ensure only one active version exists.
     */
    @Modifying
    @Query("""
        UPDATE TemplateVersion v
        SET v.active = true
        WHERE v.template.id = :templateId
          AND v.version     = :version
    """)
    int activateVersion(
            @Param("templateId") Long templateId,
            @Param("version")    Integer version);
}
