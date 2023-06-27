package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.Report;
import it.cleverad.engine.persistence.model.service.ReportDaily;
import it.cleverad.engine.persistence.model.service.ReportTopAffiliates;
import it.cleverad.engine.persistence.model.service.ReportTopCampaings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long>, JpaSpecificationExecutor<Report> {


    @Query(nativeQuery = true, value =
            "WITH w as (Select distinct campaignid, " +
                    "                           campaignname, " +
                    "                           fileid, " +
                    "                           COALESCE(SUM(impression), 0)                                                 as impressionNumber, " +
                    "                           COALESCE(SUM(clicknumber), 0)                                                as clickNumber, " +
                    "                           COALESCE(SUM(leadnumber), 0)                                                 as leadNumber, " +
                    "                           COALESCE(round((SUM(clicknumber) / SUM(nullif(impression, 0)) * 100), 2), " +
                    "                                    0)                                                                  as CTR, " +
                    "                           COALESCE(round((SUM(leadnumber) / SUM(nullif(clicknumber, 0)) * 100), 2), 0) as LR, " +
                    "                           COALESCE(round(CAST(SUM(commssion) AS numeric), 2), 0)                       as commission, " +
                    "                           COALESCE(round(CAST(SUM(revenue) AS numeric), 2), 0)                         as revenue, " +
                    "                           COALESCE(round(CAST((SUM(revenue) - SUM(commssion)) AS numeric), 2), " +
                    "                                    0)                                                                  as margine, " +
                    "                           COALESCE(round( " +
                    "                                            CAST((SUM(revenue) - SUM(commssion)) / SUM(nullif(revenue, 0)) * 100 AS numeric), " +
                    "                                            2), " +
                    "                                    0)                                                                  as marginePC, " +
                    "                           COALESCE(round(SUM(commssion) / SUM(nullif(impression, 0)) * 1000, 2), " +
                    "                                    0)                                                                  as ecpm, " +
                    "                           COALESCE(round(SUM(commssion) / SUM(nullif(clicknumber, 0)), 2), " +
                    "                                    0)                                                                  as ecpc, " +
                    "                           COALESCE(round(SUM(commssion) / SUM(nullif(leadnumber, 0)), 2), " +
                    "                                    0)                                                                  as ecpl, " +
                    "                           COALESCE(tc.initial_budget, 0)                                               as initialBudget, " +
                    "                           COALESCE(tc.budget, 0)                                                       as budget, " +
                    "                           COALESCE(round( " +
                    "                                            CAST((tc.initial_budget - tc.budget) / nullif(tc.initial_budget, 0) * 100 AS numeric), " +
                    "                                            2), " +
                    "                                    0)                                                                  as budgetGivenPC, " +
                    "                           COALESCE(round(CAST(tc.budget / nullif(tc.initial_budget, 0) * 100 AS numeric), 2), " +
                    "                                    0)                                                                  as budgetPC " +
                    "           from v_widget_all vall " +
                    "                    left join t_campaign tc on campaignid = tc.id " +
                    "           where (cast(:dateFrom as date) IS NULL OR (:dateFrom <= datetime)) " +
                    "             AND (cast(:dateTo as date) IS NULL OR (:dateTo >= datetime + interval '24 hours')) " +
                    "             AND ((:affiliateId) IS NULL OR (affiliateid = (:affiliateId))) " +
                    "             AND ((:campaignid) IS NULL OR (campaignid = (:campaignid))) " +
                    "           group by fileid, campaignname, campaignid, tc.initial_budget, tc.budget) " +
                    "SELECT campaignid, " +
                    "       campaignname, " +
                    "       fileid, " +
                    "       COALESCE(SUM(impressionnumber), 0)                                                 as impressionNumber, " +
                    "       COALESCE(SUM(clicknumber), 0)                                                      as clickNumber, " +
                    "       COALESCE(SUM(leadnumber), 0)                                                       as leadNumber, " +
                    "       COALESCE(round((SUM(clicknumber) / SUM(nullif(impressionnumber, 0)) * 100), 2), 0) as CTR, " +
                    "       COALESCE(round((SUM(leadnumber) / SUM(nullif(clicknumber, 0)) * 100), 2), 0)       as LR, " +
                    "       CONCAT(COALESCE(SUM(commission), 0), '')                                           as commission, " +
                    "       CONCAT(COALESCE(SUM(revenue), 0), '')                                              as revenue, " +
                    "       CONCAT(COALESCE(SUM(margine), 0), '')                                              as margine, " +
                    "       COALESCE(round(CAST((SUM(revenue) - SUM(commission)) / SUM(nullif(revenue, 0)) * 100 AS numeric), 2), " +
                    "                0)                                                                        as marginePC, " +
                    "       COALESCE(round(SUM(commission) / SUM(nullif(impressionnumber, 0)) * 1000, 2), 0)   as ecpm, " +
                    "       COALESCE(round(SUM(commission) / SUM(nullif(clicknumber, 0)), 2), 0)               as ecpc, " +
                    "       COALESCE(round(SUM(commission) / SUM(nullif(leadnumber, 0)), 2), 0)                as ecpl, " +
                    "       CONCAT(COALESCE(SUM(budget), 0), '')                                               as initialBudget, " +
                    "       CONCAT(COALESCE(SUM(budget), 0), '')                                               as budget, " +
                    "       CONCAT(COALESCE(round(AVG(budgetGivenPC), 2), 0), '')                              as budgetGivenPC, " +
                    "       CONCAT(COALESCE(round(AVG(budgetPC), 2), 0), '')                                   as budgetPC " +
                    "FROM w " +
                    "GROUP BY ROLLUP ((campaignid, campaignname, fileid, " +
                    "                  impressionNumber, clickNumber, leadnumber, " +
                    "                  ctr, lr, " +
                    "                  commission, revenue, margine, marginePC, " +
                    "                  ecpm, ecpc, ecpl, " +
                    "                  initialBudget, budget, budgetGivenPC, budgetPC)) " +
                    "order by campaignname nulls last")
    List<ReportTopCampaings> searchTopCampaigns(@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo, @Param("affiliateId") Long affiliateId, @Param("campaignid") Long campaignid);
    //"  AND ((:dictionaryList)IS NULL OR (dictionaryId in (:dictionaryList))) " +

    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================

    @Query(nativeQuery = true, value ="WITH w as (Select distinct campaignid, " +
            "                           campaignname                                                      as campaignname, " +
            "                           fileid, " +
            "                           channelid                                                         AS channelId, " +
            "                           channelname                                                       AS channelName, " +
            "                           affiliateId                                                       as affiliateId, " +
            "                           affiliateName                                                     as affiliateName, " +
            "                           COALESCE(SUM(impression), 0)                                      as impressionNumber, " +
            "                           COALESCE(SUM(clicknumber), 0)                                     as clickNumber, " +
            "                           COALESCE(SUM(leadnumber), 0)                                      as leadNumber, " +
            "                           COALESCE(round((SUM(clicknumber) / SUM(impression) * 100), 2), 0) as CTR, " +
            "                           COALESCE(round((SUM(leadnumber) / SUM(clicknumber) * 100), 2), 0) as LR, " +
            "                           COALESCE(round(CAST(SUM(commssion) AS numeric), 2), 0)            as commission, " +
            "                           COALESCE(round(CAST(SUM(revenue) AS numeric), 2), 0)              as revenue, " +
            "                           COALESCE(round(CAST((SUM(revenue) - SUM(commssion)) AS numeric), " +
            "                                          2), 0)                                             as margine, " +
            "                           case " +
            "                               when SUM(revenue) > 0 then " +
            "                                   round(CAST((SUM(revenue) - SUM(commssion)) / SUM(revenue) * 100 AS numeric), 2) " +
            "                               else 0 end                                                    as marginePC, " +
            "                           COALESCE(round(SUM(commssion) / SUM(impression) * 1000, 2), 0)    as ecpm, " +
            "                           COALESCE(round(SUM(commssion) / SUM(clicknumber), 2), 0)          as ecpc, " +
            "                           COALESCE(round(SUM(commssion) / SUM(leadnumber), 2), 0)           as ecpl, " +
            "                           COALESCE(tc.initial_budget, 0)                                    as initialBudget, " +
            "                           COALESCE(tc.budget, 0)                                            as budget, " +
            "                           case " +
            "                               when tc.initial_budget > 0 then " +
            "                                   round(CAST((tc.initial_budget - tc.budget) / tc.initial_budget * 100 AS numeric), 2) " +
            "                               else 0 end                                                    as budgetGivenPC, " +
            "                           case " +
            "                               when tc.initial_budget > 0 then " +
            "                                   round(CAST(tc.budget / tc.initial_budget * 100 AS numeric), 2) " +
            "                               else 0 end                                                    as budgetPC " +
            "           from v_widget_all vall " +
            "                    left join t_campaign tc on campaignid = tc.id " +
            "           where (cast(:dateFrom as date) IS NULL OR (:dateFrom <= datetime)) " +
            "             AND (cast(:dateTo as date) IS NULL OR (:dateTo >= datetime + interval '24 hours')) " +
            "             AND ((:affiliateId) IS NULL OR (affiliateid = (:affiliateId))) " +
            "             AND ((:campaignid) IS NULL OR (campaignid = (:campaignid))) " +
            "           group by fileid, campaignname, campaignid, tc.initial_budget, tc.budget, tc.name, channelid, channelname, " +
            "                    affiliateid, affiliatename) " +
            "SELECT campaignid, " +
            "       campaignname, " +
            "       fileid, " +
            "       channelId, " +
            "       channelName, " +
            "       affiliateId, " +
            "       affiliateName, " +
            "       COALESCE(SUM(impressionnumber), 0)                                                 as impressionNumber, " +
            "       COALESCE(SUM(clicknumber), 0)                                                      as clickNumber, " +
            "       COALESCE(SUM(leadnumber), 0)                                                       as leadNumber, " +
            "       COALESCE(round((SUM(clicknumber) / SUM(nullif(impressionnumber, 0)) * 100), 2), 0) as CTR, " +
            "       COALESCE(round((SUM(leadnumber) / SUM(nullif(clicknumber, 0)) * 100), 2), 0)       as LR, " +
            "       CONCAT(COALESCE(SUM(commission), 0), '')                                           as commission, " +
            "       CONCAT(COALESCE(SUM(revenue), 0), '')                                              as revenue, " +
            "       CONCAT(COALESCE(SUM(margine), 0), '')                                              as margine, " +
            "       COALESCE(round(CAST((SUM(revenue) - SUM(commission)) / SUM(nullif(revenue, 0)) * 100 AS numeric), 2), " +
            "                0)                                                                        as marginePC, " +
            "       COALESCE(round(SUM(commission) / SUM(nullif(impressionnumber, 0)) * 1000, 2), 0)   as ecpm, " +
            "       COALESCE(round(SUM(commission) / SUM(nullif(clicknumber, 0)), 2), 0)               as ecpc, " +
            "       COALESCE(round(SUM(commission) / SUM(nullif(leadnumber, 0)), 2), 0)                as ecpl, " +
            "       COALESCE(initialBudget, 0)                                                         as initialBudget, " +
            "       CONCAT(COALESCE(SUM(budget), 0), '')                                               as budget, " +
            "       CONCAT(COALESCE(round(AVG(budgetGivenPC), 2), 0), '')                              as budgetGivenPC, " +
            "       CONCAT(COALESCE(round(AVG(budgetPC), 2), 0), '')                                   as budgetPC " +
            "FROM w " +
            "GROUP BY ROLLUP ((campaignid, campaignname, fileid, " +
            "                  channelId, channelName, affiliateId, affiliateName, " +
            "                  impressionNumber, clickNumber, leadnumber, " +
            "                  ctr, lr, " +
            "                  commission, revenue, margine, marginePC, " +
            "                  ecpm, ecpc, ecpl, " +
            "                  initialBudget, budget, budgetGivenPC, budgetPC)) " +
            "order by campaignname nulls last;")
    List<ReportTopCampaings> searchTopCampaignsChannel(@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo, @Param("affiliateId") Long affiliateId, @Param("campaignid") Long campaignid);

    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================

    @Query(nativeQuery = true, value = "Select distinct affiliateId                                                                as affiliateId, " +
            "                affiliateName                                                              as affiliateName, " +
            "                channelid                                                                  as channelId, " +
            "                channelName                                                                as channelName, " +
            "                COALESCE(SUM(impression), 0)                                           as impressionNumber, " +
            "                COALESCE(SUM(clicknumber), 0)                                          as clickNumber, " +
            "                COALESCE(SUM(leadnumber), 0)                                           as leadNumber, " +
            "                COALESCE(round((SUM(clicknumber) / SUM(impression) * 100), 2), 0) as CTR, " +
            "                COALESCE(round((SUM(leadnumber) / SUM(clicknumber) * 100), 2), 0) as LR, " +
            "                COALESCE(round(CAST(SUM(commssion) AS numeric), 2), 0)                 as commission, " +
            "                COALESCE(round(CAST(SUM(revenue) AS numeric), 2), 0)                   as revenue, " +
            "                COALESCE(round(CAST((SUM(revenue) - SUM(commssion)) AS numeric), " +
            "                               2), 0)                                                       as margine, " +
            "                COALESCE(round(CAST((SUM(revenue) - SUM(commssion)) AS numeric) " +
            "                                   / CAST(SUM(nullif(revenue, 0)) AS numeric) * 100, 2), 0) " +
            "                                                                                            as marginePC, " +
            "                COALESCE(round(SUM(commssion) / SUM(impression) * 1000, 2), 0)    as ecpm, " +
            "                COALESCE(round(SUM(commssion) / SUM(clicknumber), 2), 0)   as ecpc, " +
            "                COALESCE(round(SUM(commssion) / SUM(leadnumber), 2), 0)           as ecpl" +
            " from v_widget_all vall " +
            " where " +
            "  (cast(:dateFrom as date) IS NULL OR (:dateFrom <= datetime)) " +
            "  AND (cast(:dateTo as date) IS NULL OR (:dateTo >= datetime )) " +
            "  AND ( (:affiliateId)IS NULL OR (affiliateid = (:affiliateId)))  " +
            "  AND ((:campaignid) IS NULL OR (campaignid = (:campaignid)))  " +
            " group by affiliateName, affiliateId, channelid, channelName" +
            " ORDER BY impressionNumber DESC;")
    List<ReportTopAffiliates> searchTopAffilaites(@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo, @Param("affiliateId") Long affiliateId, @Param("campaignid") Long campaignid);

    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================

    @Query(nativeQuery = true, value = "WITH w as ( " +
            "Select distinct datadx                                                                          as giorno, " +
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
            "  AND (cast(:dateTo as date) IS NULL OR (:dateTo >= dt.datadx + interval '24 hours')) " +
            "  AND ((:affiliateId) IS NULL OR (dt.affilaiteid = (:affiliateId)))  " +
            "  AND ((:campaignid) IS NULL OR (dt.campaignid = (:campaignid)))  " +
            " group by dt.datadx " +
            " ) " +
            "SELECT giorno, " +
            "       COALESCE(SUM(impressionnumber), 0)                 as impressionNumber, " +
            "       COALESCE(SUM(clicknumber), 0)                      as clickNumber, " +
            "       COALESCE(SUM(leadnumber), 0)                       as leadNumber, " +
            "       COALESCE(round((SUM(clicknumber) / SUM(impressionnumber) * 100), 2), 0)       as CTR, " +
            "       COALESCE(round(CAST((SUM(leadnumber) / SUM(clicknumber) * 100) AS numeric), 2), 0)       as LR, " +
            "       CONCAT(COALESCE(SUM(commission), 0), '')          as commission, " +
            "       CONCAT(COALESCE(SUM(revenue), 0), '')             as revenue, " +
            "       CONCAT(COALESCE(SUM(margine), 0), '')             as margine, " +
            "                COALESCE(round(CAST((SUM(revenue) - SUM(commission)) AS numeric) " +
            "                                   / CAST(SUM(nullif(revenue, 0)) AS numeric) * 100, 2), 0) " +
            "                                                                                                as marginePC, " +
            "       CONCAT(COALESCE(round(AVG(ecpm), 2), 0), '')      as ecpm, " +
            "       CONCAT(COALESCE(round(AVG(ecpc), 2), 0), '')      as ecpc, " +
            "       CONCAT(COALESCE(round(AVG(ecpc), 2), 0), '')      as ecpl " +
            "FROM w " +
            "GROUP BY ROLLUP ((giorno, impressionNumber, clickNumber, leadnumber, ctr, lr, commission, revenue, margine, marginePC, ecpm, ecpc, ecpl)) " +
            "order by giorno nulls last"
    )
    List<ReportDaily> searchDaily(@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo, @Param("affiliateId") Long affiliateId, @Param("campaignid") Long campaignid);

}