package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.TopCampagneCPM;
import it.cleverad.engine.persistence.model.service.WidgetTopCPM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WidgetTopCPMRepository extends JpaRepository<WidgetTopCPM, Long>, JpaSpecificationExecutor<WidgetTopCPM> {

    @Query(nativeQuery = true, value =
            " select campaignid, sum(impression) as impression " +
                    " from v_widget_all " +
                    " where date > CURRENT_DATE - (:days) " +
                    " and impression > 0 " +
                    " and ((:affiliateid) IS NULL OR (affiliateid = (:affiliateid))) " +
                    " group by campaignid " +
                    " order by impression desc " +
                    " limit 6")
    List<TopCampagneCPM> listaTopCampagne(@Param("affiliateid") Long affiliateId, @Param("days") Integer days);
}


