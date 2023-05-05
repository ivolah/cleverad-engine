package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.TopAffiliates;
import it.cleverad.engine.persistence.model.service.TopCampaings;
import it.cleverad.engine.persistence.model.service.WidgetTopCPM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface WidgetTopCPMRepository extends JpaRepository<WidgetTopCPM, Long>, JpaSpecificationExecutor<WidgetTopCPM> {

    @Query(nativeQuery = true, value =
            "Select distinct vall.campaignid, " +
                    "                vall.campaignname, " +
                    "                SUM(vall.impression)                                                            as impressionNumber, " +
                    "                SUM(vall.clicknumber)                                                           as clickNumber, " +
                    "                SUM(vall.leadnumber)                                                            as leadNumber, " +
                    "                round(CAST((SUM(vall.clicknumber) / SUM(vall.impression) * 100) AS numeric), 2) as CTR, " +
                    "                round(CAST((SUM(vall.leadnumber) / SUM(vall.clicknumber) * 100) AS numeric), 2) as LR, " +
                    "                round(CAST(SUM(vall.commssion) AS numeric), 2)                                  as commission, " +
                    "                round(CAST(SUM(vall.revenue) AS numeric), 2)                                    as revenue, " +
                    "                round(CAST((SUM(vall.revenue) - SUM(vall.commssion)) AS numeric), 2)            as margine, " +
                    "                case" +
                    "                    when SUM(vall.revenue) > 0 then" +
                    "                        round(CAST((SUM(vall.revenue) - SUM(vall.commssion)) / SUM(vall.revenue) * 100 AS numeric), 2)" +
                    "                    else 0 end                                                                  as marginePC," +
                    "                round(CAST(SUM(vall.ecpm) AS numeric), 2)                                       as ecpm, " +
                    "                round(CAST(SUM(vall.ecpc) AS numeric), 2)                                       as ecpc, " +
                    "                round(CAST(SUM(vall.ecpl) AS numeric), 2)                                       as ecpl " +
                    " " +
                    "from v_widget_all vall " +
                    " " +
                    "where ((:dateFrom < vall.date) AND (:dateTo > vall.date)) " +
                    "  AND (vall.dictionaryId in (:dictionaryList)) " +
                    "  AND (vall.affiliateid = (:affiliateId)) " +
                    " " +
                    "group by vall.campaignid, vall.campaignname; "
    )
    List<TopCampaings> findGroupByCampaignId(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo, @Param("dictionaryList") List<Long> dictionaryList, @Param("affiliateId") Long affiliateId);

    @Query(nativeQuery = true, value =
            "Select distinct vall.campaignid, " +
                    "                vall.campaignname, " +
                    "                SUM(vall.impression)                                                            as impressionNumber, " +
                    "                SUM(vall.clicknumber)                                                           as clickNumber, " +
                    "                SUM(vall.leadnumber)                                                            as leadNumber, " +
                    "                round(CAST((SUM(vall.clicknumber) / SUM(vall.impression) * 100) AS numeric), 2) as CTR, " +
                    "                round(CAST((SUM(vall.leadnumber) / SUM(vall.clicknumber) * 100) AS numeric), 2) as LR, " +
                    "                round(CAST(SUM(vall.commssion) AS numeric), 2)                                  as commission, " +
                    "                round(CAST(SUM(vall.revenue) AS numeric), 2)                                    as revenue, " +
                    "                round(CAST((SUM(vall.revenue) - SUM(vall.commssion)) AS numeric), 2)            as margine, " +
                    "                case" +
                    "                    when SUM(vall.revenue) > 0 then" +
                    "                        round(CAST((SUM(vall.revenue) - SUM(vall.commssion)) / SUM(vall.revenue) * 100 AS numeric), 2)" +
                    "                    else 0 end                                                                  as marginePC," +
                    "                round(CAST(SUM(vall.ecpm) AS numeric), 2)                                       as ecpm, " +
                    "                round(CAST(SUM(vall.ecpc) AS numeric), 2)                                       as ecpc, " +
                    "                round(CAST(SUM(vall.ecpl) AS numeric), 2)                                       as ecpl " +
                    " " +
                    "from v_widget_all vall " +
                    " " +
                    "where ((:dateFrom < vall.date) AND (:dateTo > vall.date)) " +
                    "  AND (vall.dictionaryId in (:dictionaryList)) " +
                    " " +
                    "group by vall.campaignid, vall.campaignname; "
    )
    List<TopCampaings> findGroupByCampaignIdAdmin(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo, @Param("dictionaryList") List<Long> dictionaryList);


    //===========================================================================================
    //===========================================================================================
    //===========================================================================================
    //===========================================================================================
    //===========================================================================================


    @Query(nativeQuery = true, value = "Select distinct vall.affiliateId                                                                as affiliateId, " +
            "                vall.affiliateName                                                              as affiliateName, " +
            "                vall.channelid                                                                  as channelId, " +
            "                vall.channelName                                                                as channelName, " +

            "                SUM(vall.impression)                                                            as impressionNumber, " +
            " " +
            "                SUM(vall.clicknumber)                                                           as clickNumber, " +
            " " +
            "                SUM(vall.leadnumber)                                                            as leadNumber, " +
            " " +
            "                round(CAST((SUM(vall.clicknumber) / SUM(vall.impression) * 100) AS numeric), 2) as CTR, " +
            " " +
            "                round(CAST((SUM(vall.leadnumber) / SUM(vall.clicknumber) * 100) AS numeric), 2) as LR, " +
            " " +
            "                round(CAST(SUM(vall.commssion) AS numeric), 2)                                  as commission, " +
            " " +
            "                round(CAST(SUM(vall.revenue) AS numeric), 2)                                    as revenue, " +
            " " +
            "                round(CAST((SUM(vall.revenue) - SUM(vall.commssion)) AS numeric), 2)            as margine, " +
            " " +
            "                round(CAST((SUM(vall.revenue) - SUM(vall.commssion)) / SUM(vall.revenue) * 100 " +
            "                          AS numeric), 2)                                                       as marginePC, " +
            " " +
            "                round(CAST(SUM(vall.ecpm) AS numeric), 2)                                       as ecpm, " +
            "                round(CAST(SUM(vall.ecpc) AS numeric), 2)                                       as ecpc, " +
            "                round(CAST(SUM(vall.ecpl) AS numeric), 2)                                       as ecpl " +
            " " +
            "from v_widget_all vall " +
            " " +
            "where ((:dateFrom < vall.date) AND (:dateTo > vall.date)) " +
            "  AND (vall.dictionaryId in (:dictionaryList)) " +
            "  AND (vall.affiliateid = (:affiliateId)) " +
            " " +
            "group by vall.affiliateName, vall.affiliateId, vall.channelid, vall.channelName;")
    List<TopAffiliates> findAffiliatesGroupByCampaignId(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo, @Param("dictionaryList") List<Long> dictionaryList, @Param("affiliateId") Long affiliateId);

    @Query(nativeQuery = true, value = "Select distinct vall.affiliateId                                                                as affiliateId, " +
            "                vall.affiliateName                                                              as affiliateName, " +
            "                vall.channelid                                                                  as channelId, " +
            "                vall.channelName                                                                as channelName, " +

            "                SUM(vall.impression)                                                            as impressionNumber, " +
            " " +
            "                SUM(vall.clicknumber)                                                           as clickNumber, " +
            " " +
            "                SUM(vall.leadnumber)                                                            as leadNumber, " +
            " " +
            "                round(CAST((SUM(vall.clicknumber) / SUM(vall.impression) * 100) AS numeric), 2) as CTR, " +
            " " +
            "                round(CAST((SUM(vall.leadnumber) / SUM(vall.clicknumber) * 100) AS numeric), 2) as LR, " +
            " " +
            "                round(CAST(SUM(vall.commssion) AS numeric), 2)                                  as commission, " +
            " " +
            "                round(CAST(SUM(vall.revenue) AS numeric), 2)                                    as revenue, " +
            " " +
            "                round(CAST((SUM(vall.revenue) - SUM(vall.commssion)) AS numeric), 2)            as margine, " +
            " " +
            "                round(CAST((SUM(vall.revenue) - SUM(vall.commssion)) / SUM(vall.revenue) * 100 " +
            "                          AS numeric), 2)                                                       as marginePC, " +
            " " +
            "                round(CAST(SUM(vall.ecpm) AS numeric), 2)                                       as ecpm, " +
            "                round(CAST(SUM(vall.ecpc) AS numeric), 2)                                       as ecpc, " +
            "                round(CAST(SUM(vall.ecpl) AS numeric), 2)                                       as ecpl " +
            " " +
            "from v_widget_all vall " +
            " " +
            "where ((:dateFrom < vall.date) AND (:dateTo > vall.date)) " +
            "  AND (vall.dictionaryId in (:dictionaryList)) " +
            " " +
            "group by vall.affiliateName, vall.affiliateId, vall.channelid, vall.channelName;")
    List<TopAffiliates> findAffiliatesGroupByCampaignIdAdmin(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo, @Param("dictionaryList") List<Long> dictionaryList);

}

