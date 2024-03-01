package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.FileUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FileUserRepository extends JpaRepository<FileUser, Long>, JpaSpecificationExecutor<FileUser> {
    FileUser findByUserId(Long id);
}