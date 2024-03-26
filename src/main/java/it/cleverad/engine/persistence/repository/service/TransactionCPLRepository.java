package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.TopCampagne;
import it.cleverad.engine.persistence.model.service.TotaleCampagne;
import it.cleverad.engine.persistence.model.service.TransactionCPL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionCPLRepository extends JpaRepository<TransactionCPL, Long>, JpaSpecificationExecutor<TransactionCPL> {

    @Query(nativeQuery = true, value =
            " Select campaign_id, tc.name, sum(lead_number) as totale, sum(value) as valore " +
                    " from t_transaction_cpl " +
                    " left join public.t_campaign tc on t_transaction_cpl.campaign_id = tc.id " +
                    " where " +
                    " date_time > CURRENT_DATE - (:days) " +
                    " and date_time < CURRENT_DATE - (:days) + 1 " +
                    " and ((:affiliateid) IS NULL OR (affiliate_id = (:affiliateid))) " +
                    " and ((:advertiserId) IS NULL OR (tc.advertiser_id = (:advertiserId))) " +
                    " group by campaign_id, tc.name "
    )
    List<TopCampagne> totaleGiorno(@Param("days") Integer days, @Param("affiliateid") Long affiliateId, @Param("advertiserId") Long advertiserId);

    @Query(nativeQuery = true, value =
            "Select campaign_id, tc.name, sum(lead_number) as totale, sum(value) as valore " +
                    " from t_transaction_cpl " +
                    " left join public.t_campaign tc on t_transaction_cpl.campaign_id = tc.id " +
                    " where date_time > CURRENT_DATE - (:days) " +
                    " and date_time < current_date " +
                    " and ((:affiliateid) IS NULL OR (affiliate_id = (:affiliateid))) " +
                    " and ((:advertiserId) IS NULL OR (tc.advertiser_id = (:advertiserId))) " +
                    " group by campaign_id, tc.name " +
                    " order by totale desc " +
                    " limit 100")
    List<TopCampagne> listaTopCampagneTotale(@Param("days") Integer days, @Param("affiliateid") Long affiliateId, @Param("advertiserId") Long advertiserId);

    @Query(nativeQuery = true, value =
            "Select campaign_id, tc.name, sum(lead_number) as totale, sum(value) as valore " +
                    " from t_transaction_cpl " +
                    " left join public.t_campaign tc on t_transaction_cpl.campaign_id = tc.id " +
                    " where date_time > CURRENT_DATE - (:days) " +
                    " and date_time < current_date " +
                    " and ((:affiliateid) IS NULL OR (affiliate_id = (:affiliateid))) " +
                    " and ((:advertiserId) IS NULL OR (tc.advertiser_id = (:advertiserId))) " +
                    " group by campaign_id, tc.name " +
                    " order by valore desc " +
                    " limit 100")
    List<TopCampagne> listaTopCampagneValore(@Param("days") Integer days, @Param("affiliateid") Long affiliateId, @Param("advertiserId") Long advertiserId);

    @Query(nativeQuery = true, value = "SELECT row_number() OVER () AS id, " +
            "       tt.impression_number                   AS totale, " +
            "       tt.value                               AS valore, " +
            "       tt.campaign_id, " +
            "       tc.name                                AS campaign, " +
            "       tt.affiliate_id, " +
            "       date_part('year'::text, tt.date_time)  AS year, " +
            "       date_part('month'::text, tt.date_time) AS month, " +
            "       date_part('day'::text, tt.date_time)   AS day, " +
            "       date_part('doy'::text, tt.date_time)   AS doy, " +
            "       date_part('week'::text, tt.date_time)  AS week, " +
            "       CURRENT_DATE - (:days)                 as date " +
            "FROM t_transaction_cpm tt " +
            "         LEFT JOIN t_campaign tc ON tt.campaign_id = tc.id " +
            "where date_time > CURRENT_DATE - (:days) " +
            "  and date_time < CURRENT_DATE - (:days) + 1 " +
            "  and affiliate_id = (:affiliateid) " +
            "  and tc.advertiser_id = (:advertiserId) " +
            "GROUP BY tt.impression_number, tt.value, tc.name, tt.campaign_id, tt.affiliate_id, (date_part('year'::text, tt.date_time)), " +
            "         (date_part('month'::text, tt.date_time)), (date_part('day'::text, tt.date_time)), (date_part('week'::text, tt.date_time)), " +
            "         (date_part('doy'::text, tt.date_time)) " +
            "order by totale desc " +
            "limit 10;")
    List<TopCampagne> listaTop10CampagneGiorno(@Param("days") Integer days, @Param("affiliateid") Long affiliateId, @Param("advertiserId") Long advertiserId);

}