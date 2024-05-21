package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.FileCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FileCouponRepository extends JpaRepository<FileCoupon, Long>, JpaSpecificationExecutor<FileCoupon> {
}