package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.TopAffiliates;
import it.cleverad.engine.persistence.model.service.TopCampaings;
import it.cleverad.engine.persistence.model.service.WidgetCPM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface WidgetCPMRepository extends JpaRepository<WidgetCPM, Long>, JpaSpecificationExecutor<WidgetCPM> {

    @Query(nativeQuery = true, value =
            "	Select vtcm.campaignid,		" +
                    "	       vtcm.campaignname,		" +
                    "			" +
                    "	       (SELECT SUM(cc.impression)		" +
                    "	        FROM v_widget_cpm cc		" +
                    "	        WHERE cc.campaignid = vtcm.campaignid)                                                        as impressionNumber,		" +
                    "			" +
                    "	       (SELECT SUM(cc.clicknumber)		" +
                    "	        FROM v_widget_cpc cc		" +
                    "	        WHERE cc.campaignid = vtcm.campaignid)                                                        as clickNumber,		" +
                    "			" +
                    "	       (SELECT SUM(cc.leadnumber)		" +
                    "	        FROM v_widget_cpl cc		" +
                    "	        WHERE cc.campaignid = vtcm.campaignid)                                                        as leadNumber,		" +
                    "			" +
                    "	       round(CAST(((SELECT SUM(cc.clicknumber)		" +
                    "	                    FROM v_widget_cpc cc		" +
                    "	                    WHERE cc.campaignid = vtcm.campaignid) /		" +
                    "	                   (SELECT SUM(cc.impression)		" +
                    "	                    FROM v_widget_cpm cc		" +
                    "	                    WHERE cc.campaignid = vtcm.campaignid) * 100) AS numeric), 2)                     as CTR,		" +
                    "			" +
                    "	       round(CAST(((SELECT SUM(cc.leadnumber)		" +
                    "	                    FROM v_widget_cpl cc		" +
                    "	                    WHERE cc.campaignid = vtcm.campaignid) /		" +
                    "	                   (SELECT SUM(cc.clicknumber)		" +
                    "	                    FROM v_widget_cpc cc		" +
                    "	                    WHERE cc.campaignid = vtcm.campaignid) * 100) AS numeric), 2)                     as LR,		" +
                    "			" +
                    "	       (SELECT COALESCE(SUM(cc.commssion), 0)		" +
                    "	        FROM v_widget_cpc cc		" +
                    "	        WHERE cc.campaignid = vtcm.campaignid +		" +
                    "	                              (SELECT COALESCE(SUM(cl.commssion), 0)		" +
                    "	                               FROM v_widget_cpl cl		" +
                    "	                               WHERE cl.campaignid = vtcm.campaignid) +		" +
                    "	                              (SELECT COALESCE(SUM(cl.commssion), 0)		" +
                    "	                               FROM v_widget_cpl cl		" +
                    "	                               WHERE cl.campaignid = vtcm.campaignid))                                as commission,		" +
                    "			" +
                    "	       (SELECT COALESCE(SUM(cc.revenue), 0)		" +
                    "	        FROM v_widget_cpc cc		" +
                    "	        WHERE cc.campaignid = vtcm.campaignid +		" +
                    "	                              (SELECT COALESCE(SUM(cl.revenue), 0)		" +
                    "	                               FROM v_widget_cpl cl		" +
                    "	                               WHERE cl.campaignid = vtcm.campaignid) +		" +
                    "	                              (SELECT COALESCE(SUM(cl.revenue), 0)		" +
                    "	                               FROM v_widget_cpl cl		" +
                    "	                               WHERE cl.campaignid = vtcm.campaignid))                                as revenue,		" +
                    "			" +
                    "	       round(CAST((((SELECT COALESCE(SUM(cc.revenue), 0)		" +
                    "	                     FROM v_widget_cpc cc		" +
                    "	                     WHERE cc.campaignid = vtcm.campaignid +		" +
                    "	                                           (SELECT COALESCE(SUM(cl.revenue), 0)		" +
                    "	                                            FROM v_widget_cpl cl		" +
                    "	                                            WHERE cl.campaignid = vtcm.campaignid) +		" +
                    "	                                           (SELECT COALESCE(SUM(cl.revenue), 0)		" +
                    "	                                            FROM v_widget_cpl cl		" +
                    "	                                            WHERE cl.campaignid = vtcm.campaignid)) -		" +
                    "	                    (SELECT COALESCE(SUM(cc.commssion), 0)		" +
                    "	                     FROM v_widget_cpc cc		" +
                    "	                     WHERE cc.campaignid = vtcm.campaignid +		" +
                    "	                                           (SELECT COALESCE(SUM(cl.commssion), 0)		" +
                    "	                                            FROM v_widget_cpl cl		" +
                    "	                                            WHERE cl.campaignid = vtcm.campaignid) +		" +
                    "	                                           (SELECT COALESCE(SUM(cl.commssion), 0)		" +
                    "	                                            FROM v_widget_cpl cl		" +
                    "	                                            WHERE cl.campaignid = vtcm.campaignid)))) AS numeric), 2) as margine,		" +
                    "			" +
                    "	       round(CAST((		" +
                    "	               ((SELECT COALESCE(SUM(cc.revenue), 0)		" +
                    "	                 FROM v_widget_cpc cc		" +
                    "	                 WHERE cc.campaignid = vtcm.campaignid +		" +
                    "	                                       (SELECT COALESCE(SUM(cl.revenue), 0)		" +
                    "	                                        FROM v_widget_cpl cl		" +
                    "	                                        WHERE cl.campaignid = vtcm.campaignid) +		" +
                    "	                                       (SELECT COALESCE(SUM(cl.revenue), 0)		" +
                    "	                                        FROM v_widget_cpl cl		" +
                    "	                                        WHERE cl.campaignid = vtcm.campaignid))		" +
                    "	                   -		" +
                    "	                (SELECT COALESCE(SUM(cc.commssion), 0)		" +
                    "	                 FROM v_widget_cpc cc		" +
                    "	                 WHERE cc.campaignid = vtcm.campaignid +		" +
                    "	                                       (SELECT COALESCE(SUM(cl.commssion), 0)		" +
                    "	                                        FROM v_widget_cpl cl		" +
                    "	                                        WHERE cl.campaignid = vtcm.campaignid) +		" +
                    "	                                       (SELECT COALESCE(SUM(cl.commssion), 0)		" +
                    "	                                        FROM v_widget_cpl cl		" +
                    "	                                        WHERE cl.campaignid = vtcm.campaignid))		" +
                    "	                   ) /		" +
                    "	               (SELECT COALESCE(SUM(cc.revenue), 0)		" +
                    "	                FROM v_widget_cpc cc		" +
                    "	                WHERE cc.campaignid = vtcm.campaignid +		" +
                    "	                                      (SELECT COALESCE(SUM(cl.revenue), 0)		" +
                    "	                                       FROM v_widget_cpl cl		" +
                    "	                                       WHERE cl.campaignid = vtcm.campaignid) +		" +
                    "	                                      (SELECT COALESCE(SUM(cl.revenue), 0)		" +
                    "	                                       FROM v_widget_cpl cl		" +
                    "	                                       WHERE cl.campaignid = vtcm.campaignid))) AS NUMERIC), 2) * 100 as marginePC,		" +
                    "			" +
                    "	       (SELECT COALESCE(SUM(tt.ecpm), 0)		" +
                    "	        FROM v_widget_cpm tt		" +
                    "	        WHERE tt.campaignid = vtcm.campaignid)                                                        as ecpm,		" +
                    "	       (SELECT COALESCE(SUM(tt.ecpc), 0)		" +
                    "	        FROM v_widget_cpc tt		" +
                    "	        WHERE tt.campaignid = vtcm.campaignid)                                                        as ecpc,		" +
                    "	       (SELECT COALESCE(SUM(tt.ecpl), 0)		" +
                    "	        FROM v_widget_cpl tt		" +
                    "	        WHERE tt.campaignid = vtcm.campaignid)                                                        as ecpl		" +
                    "			" +
                    "	from v_widget_cpm vtcm		" +
                    "	         left join v_widget_cpc vtcc on vtcm.campaignid = vtcc.campaignid		" +
                    "	         left join v_widget_cpl vtcl on vtcc.campaignid = vtcl.campaignid		" +
                    "	where (		" +
                    "	        ((:dateFrom < vtcm.date) AND (:dateTo > vtcm.date)) or		" +
                    "	        ((:dateFrom < vtcc.date) AND (:dateTo > vtcc.date)) or		" +
                    "	        ((:dateFrom < vtcl.date) AND (:dateTo > vtcl.date))		" +
                    "	    )		" +
                    "	  AND (		" +
                    "	        (vtcm.dictionaryId in (:dictionaryList)) or		" +
                    "	        (vtcc.dictionaryId in (:dictionaryList)) or		" +
                    "	        (vtcl.dictionaryId in (:dictionaryList))		" +
                    "	    )		" +
                    "	group by vtcm.campaignid, vtcm.campaignname;		" +
                    "			"

    )
    List<TopCampaings> findGroupByCampaignId(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo, @Param("dictionaryList") List<Long> dictionaryList);

    @Query(nativeQuery = true, value = "	Select		" +
            "	       vtcm.affiliateId                                                                               as affiliateId,		" +
            "	       vtcm.affiliateName                                                                             as affiliateName,		" +
            "	       vtcm.channelid                                                                                 as channelId,		" +
            "	       vtcm.channelName                                                                               as channelName,		" +
            "			" +
            "			" +
            "	       (SELECT SUM(cc.impression)		" +
            "	        FROM v_widget_cpm cc		" +
            "	        WHERE cc.affiliateid = vtcm.affiliateid)                                                        as impressionNumber,		" +
            "			" +
            "			" +
            "	       (SELECT SUM(cc.clicknumber)		" +
            "	        FROM v_widget_cpc cc		" +
            "	        WHERE cc.affiliateid = vtcm.affiliateid)                                                        as clickNumber,		" +
            "			" +
            "			" +
            "	       (SELECT SUM(cc.leadnumber)		" +
            "	        FROM v_widget_cpl cc		" +
            "	        WHERE cc.affiliateid = vtcm.affiliateid)                                                        as leadNumber,		" +
            "			" +
            "			" +
            "	       round(CAST(((SELECT SUM(cc.clicknumber)		" +
            "	                    FROM v_widget_cpc cc		" +
            "	                    WHERE cc.affiliateid = vtcm.affiliateid) /		" +
            "	                   (SELECT SUM(cc.impression)		" +
            "	                    FROM v_widget_cpm cc		" +
            "	                    WHERE cc.affiliateid = vtcm.affiliateid) * 100) AS numeric), 2)                     as CTR,		" +
            "			" +
            "			" +
            "	       round(CAST(((SELECT SUM(cc.leadnumber)		" +
            "	                    FROM v_widget_cpl cc		" +
            "	                    WHERE cc.affiliateid = vtcm.affiliateid) /		" +
            "	                   (SELECT SUM(cc.clicknumber)		" +
            "	                    FROM v_widget_cpc cc		" +
            "	                    WHERE cc.affiliateid = vtcm.affiliateid) * 100) AS numeric), 2)                     as LR,		" +
            "			" +
            "			" +
            "	       (SELECT COALESCE(SUM(cc.commssion), 0)		" +
            "	        FROM v_widget_cpc cc		" +
            "	        WHERE cc.affiliateid = vtcm.affiliateid +		" +
            "	                              (SELECT COALESCE(SUM(cl.commssion), 0)		" +
            "	                               FROM v_widget_cpl cl		" +
            "	                               WHERE cl.affiliateid = vtcm.affiliateid) +		" +
            "	                              (SELECT COALESCE(SUM(cl.commssion), 0)		" +
            "	                               FROM v_widget_cpl cl		" +
            "	                               WHERE cl.affiliateid = vtcm.affiliateid))                                as commission,		" +
            "			" +
            "			" +
            "	       (SELECT COALESCE(SUM(cc.revenue), 0)		" +
            "	        FROM v_widget_cpc cc		" +
            "	        WHERE cc.affiliateid = vtcm.affiliateid +		" +
            "	                              (SELECT COALESCE(SUM(cl.revenue), 0)		" +
            "	                               FROM v_widget_cpl cl		" +
            "	                               WHERE cl.affiliateid = vtcm.affiliateid) +		" +
            "	                              (SELECT COALESCE(SUM(cl.revenue), 0)		" +
            "	                               FROM v_widget_cpl cl		" +
            "	                               WHERE cl.affiliateid = vtcm.affiliateid))                                as revenue,		" +
            "			" +
            "			" +
            "	       round(CAST((((SELECT COALESCE(SUM(cc.revenue), 0)		" +
            "	                     FROM v_widget_cpc cc		" +
            "	                     WHERE cc.affiliateid = vtcm.affiliateid +		" +
            "	                                           (SELECT COALESCE(SUM(cl.revenue), 0)		" +
            "	                                            FROM v_widget_cpl cl		" +
            "	                                            WHERE cl.affiliateid = vtcm.affiliateid) +		" +
            "	                                           (SELECT COALESCE(SUM(cl.revenue), 0)		" +
            "	                                            FROM v_widget_cpl cl		" +
            "	                                            WHERE cl.affiliateid = vtcm.affiliateid)) -		" +
            "	                    (SELECT COALESCE(SUM(cc.commssion), 0)		" +
            "	                     FROM v_widget_cpc cc		" +
            "	                     WHERE cc.affiliateid = vtcm.affiliateid +		" +
            "	                                           (SELECT COALESCE(SUM(cl.commssion), 0)		" +
            "	                                            FROM v_widget_cpl cl		" +
            "	                                            WHERE cl.affiliateid = vtcm.affiliateid) +		" +
            "	                                           (SELECT COALESCE(SUM(cl.commssion), 0)		" +
            "	                                            FROM v_widget_cpl cl		" +
            "	                                            WHERE cl.affiliateid = vtcm.affiliateid)))) AS numeric), 2) as margine,		" +
            "			" +
            "			" +
            "	       round(CAST((		" +
            "	               ((SELECT COALESCE(SUM(cc.revenue), 0)		" +
            "	                 FROM v_widget_cpc cc		" +
            "	                 WHERE cc.affiliateid = vtcm.affiliateid +		" +
            "	                                       (SELECT COALESCE(SUM(cl.revenue), 0)		" +
            "	                                        FROM v_widget_cpl cl		" +
            "	                                        WHERE cl.affiliateid = vtcm.affiliateid) +		" +
            "	                                       (SELECT COALESCE(SUM(cl.revenue), 0)		" +
            "	                                        FROM v_widget_cpl cl		" +
            "	                                        WHERE cl.affiliateid = vtcm.affiliateid))		" +
            "	                   -		" +
            "	                (SELECT COALESCE(SUM(cc.commssion), 0)		" +
            "	                 FROM v_widget_cpc cc		" +
            "	                 WHERE cc.affiliateid = vtcm.affiliateid +		" +
            "	                                       (SELECT COALESCE(SUM(cl.commssion), 0)		" +
            "	                                        FROM v_widget_cpl cl		" +
            "	                                        WHERE cl.affiliateid = vtcm.affiliateid) +		" +
            "	                                       (SELECT COALESCE(SUM(cl.commssion), 0)		" +
            "	                                        FROM v_widget_cpl cl		" +
            "	                                        WHERE cl.affiliateid = vtcm.affiliateid))		" +
            "	                   ) /		" +
            "	               (SELECT COALESCE(SUM(cc.revenue), 0)		" +
            "	                FROM v_widget_cpc cc		" +
            "	                WHERE cc.affiliateid = vtcm.affiliateid +		" +
            "	                                      (SELECT COALESCE(SUM(cl.revenue), 0)		" +
            "	                                       FROM v_widget_cpl cl		" +
            "	                                       WHERE cl.affiliateid = vtcm.affiliateid) +		" +
            "	                                      (SELECT COALESCE(SUM(cl.revenue), 0)		" +
            "	                                       FROM v_widget_cpl cl		" +
            "	                                       WHERE cl.affiliateid = vtcm.affiliateid))) AS NUMERIC), 2) * 100 as marginePC,		" +
            "			" +
            "			" +
            "	       (SELECT COALESCE(SUM(tt.ecpm), 0)		" +
            "	        FROM v_widget_cpm tt		" +
            "	        WHERE tt.affiliateid = vtcm.affiliateid)                                                        as ecpm,		" +
            "			" +
            "	       (SELECT COALESCE(SUM(tt.ecpc), 0)		" +
            "	        FROM v_widget_cpc tt		" +
            "	        WHERE tt.affiliateid = vtcm.affiliateid)                                                        as ecpc,		" +
            "			" +
            "	       (SELECT COALESCE(SUM(tt.ecpl), 0)		" +
            "	        FROM v_widget_cpl tt		" +
            "	        WHERE tt.affiliateid = vtcm.affiliateid)                                                        as ecpl		" +
            "			" +
            "	from v_widget_cpm vtcm		" +
            "	         left join v_widget_cpc vtcc on vtcm.affiliateid = vtcc.affiliateid		" +
            "	         left join v_widget_cpl vtcl on vtcc.affiliateid = vtcl.affiliateid		" +
            "	where (		" +
            "	        ((:dateFrom < vtcm.date) AND (:dateTo > vtcm.date)) or		" +
            "	        ((:dateFrom < vtcc.date) AND (:dateTo > vtcc.date)) or		" +
            "	        ((:dateFrom < vtcl.date) AND (:dateTo > vtcl.date))		" +
            "	    )		" +
            "	  AND (		" +
            "	        (vtcm.dictionaryId in (:dictionaryList)) or		" +
            "	        (vtcc.dictionaryId in (:dictionaryList)) or		" +
            "	        (vtcl.dictionaryId in (:dictionaryList))		" +
            "	    )		" +
            "	group by  vtcm.affiliateName, vtcm.affiliateId, vtcm.channelid, vtcm.channelName;		")
    List<TopAffiliates> findAffiliatesGroupByCampaignId(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo, @Param("dictionaryList") List<Long> dictionaryList);

}

