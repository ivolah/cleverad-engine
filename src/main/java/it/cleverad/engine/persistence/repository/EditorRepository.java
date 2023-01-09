package it.cleverad.engine.persistence.repository;

import it.cleverad.engine.persistence.model.Editor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EditorRepository extends JpaRepository<Editor, Long>, JpaSpecificationExecutor<Editor> {
}
