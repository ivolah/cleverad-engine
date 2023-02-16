package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.MailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MailTemplateRepository extends JpaRepository<MailTemplate, Long>, JpaSpecificationExecutor<MailTemplate> {

}
