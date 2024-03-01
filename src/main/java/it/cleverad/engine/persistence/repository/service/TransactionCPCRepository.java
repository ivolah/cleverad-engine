package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.TopCampagne;
import it.cleverad.engine.persistence.model.service.TotaleCampagne;
import it.cleverad.engine.persistence.model.service.TransactionCPC;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionCPCRepository extends JpaRepository<TransactionCPC, Long>, JpaSpecificationExecutor<TransactionCPC> {

    @Query(nativeQuery = true, value =
            " Select sum(click_number) as totale " +
                    " from t_transaction_cpc " +
                    " where date_time > CURRENT_DATE - (:days) " +
                    " and date_time < CURRENT_DATE - (:days) + 1 ")
    List<TotaleCampagne> totaleGiorno(@Param("days") Integer days);

    @Query(nativeQuery = true, value =
            "Select campaign_id, sum(click_number) as totale from t_transaction_cpc " +
                    " where date_time > CURRENT_DATE - (:days) " +
                    " and date_time < current_date - (:days) + 1 " +
                    " and ((:affiliate_id) IS NULL OR (affiliate_id = (:affiliateid))) " +
                    " group by campaign_id " +
                    " order by totale desc " +
                    " limit (:minus)")
    List<TopCampagne> listaTopCampagne(@Param("affiliateid") Long affiliateId, @Param("days") Integer days);

}