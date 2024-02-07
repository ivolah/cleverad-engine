package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.TotaleCampagne;
import it.cleverad.engine.persistence.model.service.TransactionCPL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionCPLRepository extends JpaRepository<TransactionCPL, Long>, JpaSpecificationExecutor<TransactionCPL> {

    @Query(nativeQuery = true, value =
            " Select sum(lead_number) as totale " +
                    " from t_transaction_cpl " +
                    " where date_time > CURRENT_DATE - (:days) " +
                    " and date_time < CURRENT_DATE - (:days) + 1 ")
    List<TotaleCampagne> totaleGiorno(@Param("days") Integer days);
}