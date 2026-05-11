package org.notification.templateservice.repository;

import org.notification.templateservice.entity.Template;
import org.notification.templateservice.enums.TemplateChannel;
import org.notification.templateservice.enums.TemplateStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TemplateRepository extends JpaRepository<Template, Long>, JpaSpecificationExecutor<Template> {

    Optional<Template> getTemplateById(Long id);

    boolean existsByEventTypeAndChannelAndLocale(String eventType, TemplateChannel channel, String locale);

    @Modifying
    @Query("""
        UPDATE Template t
        SET t.status         = :status,
            t.updatedBy      = :updatedBy,
            t.updatedAt      = CURRENT_TIMESTAMP
        WHERE t.id = :id
    """)
    int updateStatus(@Param("id") Long id, @Param("status") TemplateStatus status, @Param("updatedBy") String updatedBy);
}
