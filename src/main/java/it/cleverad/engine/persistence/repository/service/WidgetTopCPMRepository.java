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
                    "                COALESCE(SUM(vall.impression), 0)                                           as impressionNumber, " +
                    "                COALESCE(SUM(vall.clicknumber), 0)                                          as clickNumber, " +
                    "                COALESCE(SUM(vall.leadnumber), 0)                                           as leadNumber, " +
                    "                COALESCE(round((SUM(vall.clicknumber) / SUM(vall.impression) * 100), 2), 0) as CTR, " +
                    "                COALESCE(round((SUM(vall.leadnumber) / SUM(vall.clicknumber) * 100), 2), 0) as LR, " +
                    "                COALESCE(round(CAST(SUM(vall.commssion) AS numeric), 2), 0)                 as commission, " +
                    "                COALESCE(round(CAST(SUM(vall.revenue) AS numeric), 2), 0)                   as revenue, " +
                    "                COALESCE(round(CAST((SUM(vall.revenue) - SUM(vall.commssion)) AS numeric), " +
                    "                               2), 0)                                                       as margine, " +
                    "                case" +
                    "                    when SUM(vall.revenue) > 0 then" +
                    "                        round(CAST((SUM(vall.revenue) - SUM(vall.commssion)) / SUM(vall.revenue) * 100 AS numeric), 2)" +
                    "                    else 0 end                                                                  as marginePC," +
                    "                COALESCE(round(SUM(vall.commssion) / SUM(vall.impression) * 1000, 2), 0)    as ecpm, " +
                    "                COALESCE(round(SUM(vall.commssion) / SUM(vall.clicknumber), 2), 0)   as ecpc, " +
                    "                COALESCE(round(SUM(vall.commssion) / SUM(vall.leadnumber), 2), 0)           as ecpl" +
                    " " +
                    "from v_widget_all vall " +
                    " " +
                    "where ((:dateFrom <= vall.date) AND (:dateTo >= vall.date)) " +
                    "  AND (vall.dictionaryId in (:dictionaryList)) " +
                    "  AND (vall.affiliateid = (:affiliateId)) " +
                    "  AND (vall.campaignid = (:campaignid)) " +
                    " " +
                    "group by vall.campaignid, vall.campaignname" +
                    " ORDER BY impressionNumber DESC;")
    List<TopCampaings> findGroupByCampaignId(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo, @Param("dictionaryList") List<Long> dictionaryList, @Param("affiliateId") Long affiliateId, @Param("campaignid") Long campaignid );

    @Query(nativeQuery = true, value =
            "Select distinct vall.campaignid, " +
                    "                vall.campaignname, " +
                    "                COALESCE(SUM(vall.impression), 0)                                           as impressionNumber, " +
                    "                COALESCE(SUM(vall.clicknumber), 0)                                          as clickNumber, " +
                    "                COALESCE(SUM(vall.leadnumber), 0)                                           as leadNumber, " +
                    "                COALESCE(round((SUM(vall.clicknumber) / SUM(vall.impression) * 100), 2), 0) as CTR, " +
                    "                COALESCE(round((SUM(vall.leadnumber) / SUM(vall.clicknumber) * 100), 2), 0) as LR, " +
                    "                COALESCE(round(CAST(SUM(vall.commssion) AS numeric), 2), 0)                 as commission, " +
                    "                COALESCE(round(CAST(SUM(vall.revenue) AS numeric), 2), 0)                   as revenue, " +
                    "                COALESCE(round(CAST((SUM(vall.revenue) - SUM(vall.commssion)) AS numeric), " +
                    "                               2), 0)                                                       as margine, " +
                    "                case" +
                    "                    when SUM(vall.revenue) > 0 then" +
                    "                        round(CAST((SUM(vall.revenue) - SUM(vall.commssion)) / SUM(vall.revenue) * 100 AS numeric), 2)" +
                    "                    else 0 end                                                                  as marginePC," +
                    "                COALESCE(round(SUM(vall.commssion) / SUM(vall.impression) * 1000, 2), 0)    as ecpm, " +
                    "                COALESCE(round(SUM(vall.commssion) / SUM(vall.clicknumber), 2), 0)   as ecpc, " +
                    "                COALESCE(round(SUM(vall.commssion) / SUM(vall.leadnumber), 2), 0)           as ecpl" +
                    " " +
                    "from v_widget_all vall " +
                    " " +
                    "where ((:dateFrom <= vall.date) AND (:dateTo >= vall.date)) " +
                    "  AND (vall.dictionaryId in (:dictionaryList)) " +
                    "  AND (vall.affiliateid = (:affiliateId)) " +
                    "  AND (vall.campaignid = (:campaignid)) " +
                    "group by vall.campaignid, vall.campaignname" +
                    " ORDER BY impressionNumber DESC;")
    List<TopCampaings> findGroupByCampaignIdAdmin(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo, @Param("dictionaryList") List<Long> dictionaryList, @Param("affiliateId") Long affiliateId, @Param("campaignid") Long campaignid );


    //===========================================================================================
    //===========================================================================================
    //===========================================================================================
    //===========================================================================================
    //===========================================================================================


    @Query(nativeQuery = true, value = "Select distinct vall.affiliateId                                                                as affiliateId, " +
            "                vall.affiliateName                                                              as affiliateName, " +
            "                vall.channelid                                                                  as channelId, " +
            "                vall.channelName                                                                as channelName, " +
            "                COALESCE(SUM(vall.impression), 0)                                           as impressionNumber, " +
            "                COALESCE(SUM(vall.clicknumber), 0)                                          as clickNumber, " +
            "                COALESCE(SUM(vall.leadnumber), 0)                                           as leadNumber, " +
            "                COALESCE(round((SUM(vall.clicknumber) / SUM(vall.impression) * 100), 2), 0) as CTR, " +
            "                COALESCE(round((SUM(vall.leadnumber) / SUM(vall.clicknumber) * 100), 2), 0) as LR, " +
            "                COALESCE(round(CAST(SUM(vall.commssion) AS numeric), 2), 0)                 as commission, " +
            "                COALESCE(round(CAST(SUM(vall.revenue) AS numeric), 2), 0)                   as revenue, " +
            "                COALESCE(round(CAST((SUM(vall.revenue) - SUM(vall.commssion)) AS numeric), " +
            "                               2), 0)                                                       as margine, " +
            "                COALESCE(round(CAST((SUM(vall.revenue) - SUM(vall.commssion)) AS numeric) " +
            "                                   / CAST(SUM(nullif(vall.revenue, 0)) AS numeric) * 100, 2), 0) " +
            "                                                                                            as marginePC, " +
            "                COALESCE(round(SUM(vall.commssion) / SUM(vall.impression) * 1000, 2), 0)    as ecpm, " +
            "                COALESCE(round(SUM(vall.commssion) / SUM(vall.clicknumber), 2), 0)   as ecpc, " +
            "                COALESCE(round(SUM(vall.commssion) / SUM(vall.leadnumber), 2), 0)           as ecpl" +
            " " +
            "from v_widget_all vall " +
            " " +
            "where ((:dateFrom <= vall.date) AND (:dateTo >= vall.date)) " +
            "  AND (vall.dictionaryId in (:dictionaryList)) " +
            "  AND (vall.affiliateid = (:affiliateId)) " +
            "  AND (vall.campaignid = (:campaignid)) " +
            "group by vall.affiliateName, vall.affiliateId, vall.channelid, vall.channelName" +
            " ORDER BY impressionNumber DESC;")
    List<TopAffiliates> findAffiliatesGroupByCampaignId(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo, @Param("dictionaryList") List<Long> dictionaryList, @Param("affiliateId") Long affiliateId, @Param("campaignid") Long campaignid );

    @Query(nativeQuery = true, value = "Select distinct vall.affiliateId                                                                as affiliateId, " +
            "                vall.affiliateName                                                              as affiliateName, " +
            "                vall.channelid                                                                  as channelId, " +
            "                vall.channelName                                                                as channelName, " +
            "                COALESCE(SUM(vall.impression), 0)                                           as impressionNumber, " +
            "                COALESCE(SUM(vall.clicknumber), 0)                                          as clickNumber, " +
            "                COALESCE(SUM(vall.leadnumber), 0)                                           as leadNumber, " +
            "                COALESCE(round((SUM(vall.clicknumber) / SUM(vall.impression) * 100), 2), 0) as CTR, " +
            "                COALESCE(round((SUM(vall.leadnumber) / SUM(vall.clicknumber) * 100), 2), 0) as LR, " +
            "                COALESCE(round(CAST(SUM(vall.commssion) AS numeric), 2), 0)                 as commission, " +
            "                COALESCE(round(CAST(SUM(vall.revenue) AS numeric), 2), 0)                   as revenue, " +
            "                COALESCE(round(CAST((SUM(vall.revenue) - SUM(vall.commssion)) AS numeric), " +
            "                               2), 0)                                                       as margine, " +
            "                COALESCE(round(CAST((SUM(vall.revenue) - SUM(vall.commssion)) AS numeric) " +
            "                                   / CAST(SUM(nullif(vall.revenue, 0)) AS numeric) * 100, 2), 0) " +
            "                                                                                            as marginePC, " +
            "                COALESCE(round(SUM(vall.commssion) / SUM(vall.impression) * 1000, 2), 0)    as ecpm, " +
            "                COALESCE(round(SUM(vall.commssion) / SUM(vall.clicknumber), 2), 0)   as ecpc, " +
            "                COALESCE(round(SUM(vall.commssion) / SUM(vall.leadnumber), 2), 0)           as ecpl" +
            " " +
            "from v_widget_all vall " +
            " " +
            "where ((:dateFrom <= vall.date) AND (:dateTo >= vall.date)) " +
            "  AND (vall.dictionaryId in (:dictionaryList)) " +
            "  AND (vall.affiliateid = (:affiliateId)) " +
            "  AND (vall.campaignid = (:campaignid)) " +
            "group by vall.affiliateName, vall.affiliateId, vall.channelid, vall.channelName" +
            " ORDER BY impressionNumber DESC;")
    List<TopAffiliates> findAffiliatesGroupByCampaignIdAdmin(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo, @Param("dictionaryList") List<Long> dictionaryList, @Param("affiliateId") Long affiliateId, @Param("campaignid") Long campaignid );

}


