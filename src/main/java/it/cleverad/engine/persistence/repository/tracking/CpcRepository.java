package it.cleverad.engine.persistence.repository.tracking;

import it.cleverad.engine.persistence.model.service.ClickMultipli;
import it.cleverad.engine.persistence.model.service.ReportTopCampaings;
import it.cleverad.engine.persistence.model.tracking.Cpc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface CpcRepository extends JpaRepository<Cpc, Long>, JpaSpecificationExecutor<Cpc> {


    @Query(nativeQuery = true, value =
            "         WITH click_univoci as (SELECT id, count(*) Over (partition by agent, ip, refferal, info) as totale, date, blacklisted " +
                    "                       from t_cpc " +
                    "                       where date >= cast(current_date as date)) " +
                    "" +
                    "" +
                    "" +
                    "" +
                    " select * " +
                    " from click_univoci " +
                    " where totale > 3 and blacklisted = false" )
    List<ClickMultipli> getListaClickMultipliDaDisabilitare(LocalDate date);


}