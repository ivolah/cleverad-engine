package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.TopCampagne;
import it.cleverad.engine.persistence.model.service.TransactionCPS;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionCPSRepository extends JpaRepository<TransactionCPS, Long>, JpaSpecificationExecutor<TransactionCPS> {

    @Query(nativeQuery = true, value =
            " Select campaign_id, tc.name, sum(value) as totale, sum(value) as valore " +
                    " from t_transaction_cps " +
                    " left join public.t_campaign tc on t_transaction_cps.campaign_id = tc.id " +
                    " where " +
                    " date_time > CURRENT_DATE - (:days) " +
                    " and date_time < CURRENT_DATE - (:days) + 1 " +
                    " and ((:affiliateid) IS NULL OR (affiliate_id = (:affiliateid))) " +
                    " and ((:advertiserId) IS NULL OR (tc.advertiser_id = (:advertiserId))) " +
                    " group by campaign_id, tc.name "
    )
    List<TopCampagne> totaleGiorno(@Param("days") Integer days, @Param("affiliateid") Long affiliateId, @Param("advertiserId") Long advertiserId);

    @Query(nativeQuery = true, value =
            "Select campaign_id, tc.name, sum(value) as totale, sum(value) as valore " +
                    " from t_transaction_cps " +
                    " left join public.t_campaign tc on t_transaction_cps.campaign_id = tc.id " +
                    " where date_time > CURRENT_DATE - (:days) " +
                    " and date_time < current_date " +
                    " and ((:affiliateid) IS NULL OR (affiliate_id = (:affiliateid))) " +
                    " and ((:advertiserId) IS NULL OR (tc.advertiser_id = (:advertiserId))) " +
                    " group by campaign_id, tc.name " +
                    " order by totale desc " +
                    " limit 100")
    List<TopCampagne> listaTopCampagneTotale(@Param("days") Integer days, @Param("affiliateid") Long affiliateId, @Param("advertiserId") Long advertiserId);

    @Query(nativeQuery = true, value =
            "Select campaign_id, tc.name, sum(value) as totale, sum(value) as valore " +
                    " from t_transaction_cps " +
                    " left join public.t_campaign tc on t_transaction_cps.campaign_id = tc.id " +
                    " where date_time > CURRENT_DATE - (:days) " +
                    " and date_time < current_date " +
                    " and ((:affiliateid) IS NULL OR (affiliate_id = (:affiliateid))) " +
                    " and ((:advertiserId) IS NULL OR (tc.advertiser_id = (:advertiserId))) " +
                    " group by campaign_id, tc.name " +
                    " order by valore desc " +
                    " limit 100")
    List<TopCampagne> listaTopCampagneValore(@Param("days") Integer days, @Param("affiliateid") Long affiliateId, @Param("advertiserId") Long advertiserId);


}