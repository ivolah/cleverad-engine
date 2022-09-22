package it.cleverad.engine.persistence.repository;

import it.cleverad.engine.persistence.model.Cookie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface CookieRepository extends JpaRepository<Cookie, Long>, JpaSpecificationExecutor<Cookie> {


}

