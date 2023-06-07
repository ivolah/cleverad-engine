package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.Report;
import it.cleverad.engine.persistence.model.service.ReportDaily;
import it.cleverad.engine.persistence.model.service.ReportTopAffiliates;
import it.cleverad.engine.persistence.model.service.ReportTopCampaings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long>, JpaSpecificationExecutor<Report> {


    @Query(nativeQuery = true, value =
            "Select distinct vall.campaignid, " +
                    "                vall.campaignname, " +
                    "                vall.fileid, " +
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
                    "                    else 0 end                                                              as marginePC," +
                    "                COALESCE(round(SUM(vall.commssion) / SUM(vall.impression) * 1000, 2), 0)    as ecpm, " +
                    "                COALESCE(round(SUM(vall.commssion) / SUM(vall.clicknumber), 2), 0)          as ecpc, " +
                    "                COALESCE(round(SUM(vall.commssion) / SUM(vall.leadnumber), 2), 0)           as ecpl, " +
                    "                COALESCE(tc.initial_budget, 0)                                              as initialBudget, " +
                    "                COALESCE(tc.budget, 0)                                                      as budget, " +
                    "                case" +
                    "                    when tc.initial_budget > 0 then " +
                    "                        round(CAST((tc.initial_budget - tc.budget) / tc.initial_budget * 100 AS numeric), 2) " +
                    "                    else 0 end                                                              as budgetGivenPC, " +
                    "                case" +
                    "                    when tc.initial_budget > 0 then " +
                    "                        round(CAST(tc.budget / tc.initial_budget * 100 AS numeric), 2) " +
                    "                    else 0 end                                                              as budgetPC " +
                    "  from v_widget_all vall " +
                    "          left join t_campaign tc on vall.campaignid = tc.id " +
                    "  where ((:dateFrom <= vall.date) AND (:dateTo >= vall.date)) " +
                    "  AND ((:dictionaryList)IS NULL OR (vall.dictionaryId in (:dictionaryList))) " +
                    "  AND ( (:affiliateId)IS NULL OR (vall.affiliateid = (:affiliateId)))  " +
                    "  AND ((:campaignid) IS NULL OR (vall.campaignid = (:campaignid)))  " +
                    " group by vall.fileid, vall.campaignname, vall.campaignid, tc.initial_budget, tc.budget " +
                    " ORDER BY impressionNumber DESC;")
    List<ReportTopCampaings> searchTopCampaigns(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo, @Param("dictionaryList") List<Long> dictionaryList, @Param("affiliateId") Long affiliateId, @Param("campaignid") Long campaignid);

    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================

    @Query(nativeQuery = true, value =
            "Select distinct vall.campaignid, " +
                    "                vall.campaignname, " +
                    "                vall.fileid, " +
                    "                vall.channelid                                                              AS channelId, " +
                    "                vall.channelname                                                            AS channelName, " +
                    "                vall.affiliateId                                                                as affiliateId, " +
                    "                vall.affiliateName                                                              as affiliateName, " +
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
                    "                    else 0 end                                                              as marginePC," +
                    "                COALESCE(round(SUM(vall.commssion) / SUM(vall.impression) * 1000, 2), 0)    as ecpm, " +
                    "                COALESCE(round(SUM(vall.commssion) / SUM(vall.clicknumber), 2), 0)          as ecpc, " +
                    "                COALESCE(round(SUM(vall.commssion) / SUM(vall.leadnumber), 2), 0)           as ecpl, " +
                    "                COALESCE(tc.initial_budget, 0)                                              as initialBudget, " +
                    "                COALESCE(tc.budget, 0)                                                      as budget, " +
                    "                case" +
                    "                    when tc.initial_budget > 0 then " +
                    "                        round(CAST((tc.initial_budget - tc.budget) / tc.initial_budget * 100 AS numeric), 2) " +
                    "                    else 0 end                                                              as budgetGivenPC, " +
                    "                case" +
                    "                    when tc.initial_budget > 0 then " +
                    "                        round(CAST(tc.budget / tc.initial_budget * 100 AS numeric), 2) " +
                    "                    else 0 end                                                              as budgetPC " +
                    "  from v_widget_all vall " +
                    "          left join t_campaign tc on vall.campaignid = tc.id " +
                    "  where ((:dateFrom <= vall.date) AND (:dateTo >= vall.date)) " +
                    "  AND ((:dictionaryList)IS NULL OR (vall.dictionaryId in (:dictionaryList))) " +
                    "  AND ( (:affiliateId)IS NULL OR (vall.affiliateid = (:affiliateId)))  " +
                    "  AND ((:campaignid) IS NULL OR (vall.campaignid = (:campaignid)))  " +
                    " group by vall.fileid, vall.campaignname, vall.campaignid, tc.initial_budget, tc.budget, vall.channelid, vall.channelname, vall.affiliateName, vall.affiliateId " +
                    " ORDER BY impressionNumber DESC;")
    List<ReportTopCampaings> searchTopCampaignsChannel(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo, @Param("dictionaryList") List<Long> dictionaryList, @Param("affiliateId") Long affiliateId, @Param("campaignid") Long campaignid);

    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================

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
            " from v_widget_all vall " +
            " where ((:dateFrom <= vall.date) AND (:dateTo >= vall.date)) " +
            "  AND ((:dictionaryList)IS NULL OR (vall.dictionaryId in (:dictionaryList))) " +
            "  AND ( (:affiliateId)IS NULL OR (vall.affiliateid = (:affiliateId)))  " +
            "  AND ((:campaignid) IS NULL OR (vall.campaignid = (:campaignid)))  " +
            " group by vall.affiliateName, vall.affiliateId, vall.channelid, vall.channelName" +
            " ORDER BY impressionNumber DESC;")
    List<ReportTopAffiliates> searchTopAffilaites(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo, @Param("dictionaryList") List<Long> dictionaryList, @Param("affiliateId") Long affiliateId, @Param("campaignid") Long campaignid);

    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================

    @Query(nativeQuery = true, value = "Select distinct datadx                                                                          as giorno, " +
            "                COALESCE(SUM(dt.impressionnumber), 0)                                           as impressionNumber, " +
            "                COALESCE(SUM(dt.clicknumber), 0)                                                as clickNumber, " +
            "                COALESCE(SUM(dt.leadnumber), 0)                                                 as leadNumber, " +
            "                COALESCE(round((SUM(dt.clicknumber) / SUM(dt.impressionnumber) * 100), 2), 0)   as CTR, " +
            "                COALESCE(round(CAST((SUM(dt.leadnumber) / SUM(dt.clicknumber) * 100) AS numeric), 2), 0)         as LR, " +
            "                COALESCE(round(CAST(SUM(dt.commission) AS numeric), 2), 0)                      as commission, " +
            "                COALESCE(round(CAST(SUM(dt.revenue) AS numeric), 2), 0)                         as revenue, " +
            "                COALESCE(round(CAST((SUM(dt.revenue) - SUM(dt.commission)) AS numeric), " +
            "                               2), 0)                                                           as margine, " +
            "                COALESCE(round(CAST((SUM(dt.revenue) - SUM(dt.commission)) AS numeric) " +
            "                                   / CAST(SUM(nullif(dt.revenue, 0)) AS numeric) * 100, 2), 0) " +
            "                                                                                                as marginePC, " +
            "                COALESCE(round(SUM(dt.commission) / SUM(dt.impressionnumber) * 1000, 2), 0)     as ecpm, " +
            "                COALESCE(round(SUM(dt.commission) / SUM(dt.clicknumber), 2), 0)                 as ecpc, " +
            "                COALESCE(round(CAST(SUM(dt.commission) / SUM(dt.leadnumber) AS numeric), 2), 0) as ecpl " +
            " from v_daily_transactions dt " +
            " where (cast(:dateFrom as date) IS NULL OR (:dateFrom <= dt.datadx)) " +
            "  AND (cast(:dateTo as date) IS NULL OR (:dateTo >= dt.datadx)) " +
            "  AND ((:affiliateId) IS NULL OR (dt.affilaiteid = (:affiliateId)))  " +
            "  AND ((:campaignid) IS NULL OR (dt.campaignid = (:campaignid)))  " +
            " group by dt.datadx" +
            " order by dt.datadx asc"
    )
    List<ReportDaily> searchDaily(@Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo, @Param("affiliateId") Long affiliateId, @Param("campaignid") Long campaignid);

}