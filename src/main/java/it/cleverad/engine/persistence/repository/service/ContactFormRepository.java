package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.ContactForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ContactFormRepository extends JpaRepository<ContactForm, Long>, JpaSpecificationExecutor<ContactForm> {


}
