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
            "WITH w as (Select distinct vall.campaign_id                                                                                as campaignid, " +
                    "                           vall.campaign_name                                                                              as campaignname, " +
                    "                           COALESCE(SUM(impression_number), 0)                                                             as impressionNumber, " +
                    "                           COALESCE(SUM(click_number), 0)                                                                  as clickNumber, " +
                    "                           COALESCE(SUM(lead_number), 0)                                                                   as leadNumber, " +
                    "                           COALESCE(round((SUM(click_number) / SUM(nullif(lead_number, 0)) * 100), 2), 0)                  as CTR, " +
                    "                           COALESCE(round(CAST((SUM(lead_number) / SUM(nullif(click_number, 0)) * 100) AS numeric), 2), 0) as LR, " +
                    "                           COALESCE(round(CAST(SUM(value) AS numeric), 2), 0)                                   as commission, " +
                    "                           COALESCE(round(CAST(SUM(revenue) AS numeric), 2), 0)                                              as revenue, " +
                    "                           COALESCE(round(CAST((SUM(revenue) - SUM(value)) AS numeric), 2), 0)                    as margine, " +
                    "                           COALESCE(round(CAST((SUM(revenue) - SUM(value)) AS numeric) / CAST(SUM(nullif(revenue, 0)) AS numeric) * 100, 2), " +
                    "                                    0)                                                                                     as marginePC, " +
                    "                           COALESCE(round(CAST(SUM(nullif(value, 0)) / SUM(nullif(impression_number, 0)) * 1000 AS numeric), 2), " +
                    "                                    0)                                                                                     as ecpm, " +
                    "                           COALESCE(round(CAST(SUM(value) / SUM(nullif(click_number, 0)) AS numeric), 2), " +
                    "                                    0)                                                                                     as ecpc, " +
                    "                           COALESCE(round(CAST(SUM(value) / SUM(nullif(lead_number, 0)) AS numeric), 2), " +
                    "                                    0)                                                                                     as ecpl, " +
                    "                           COALESCE(tca.initial_budget, 0)                                                                 as initialBudget, " +
                    "                           COALESCE(tca.budget, 0)                                                                         as budget, " +
                    "                           COALESCE(round(CAST((tca.initial_budget - tca.budget) / " +
                    "                                               nullif(tca.initial_budget, 0) * 100 AS numeric), 2), " +
                    "                                    0)                                                                                     as budgetGivenPC, " +
                    "                           COALESCE(round(CAST(tca.budget / nullif(tca.initial_budget, 0) * 100 AS numeric), 2), " +
                    "                                    0)                                                                                     as budgetPC " +
                    "           from v_transactions_status vall " +
                    "                    left join t_channel tc on vall.channel_id = tc.id " +
                    "                    left join t_campaign tca on vall.campaign_id = tca.id " +
                    "           where (cast(:dateFrom as date) IS NULL OR (:dateFrom <= vall.date_time)) " +
                    "             AND (cast(:dateTo as date) IS NULL OR (:dateTo >= vall.date_time)) " +
                    "             AND ((:affiliateId) IS NULL OR (vall.affiliate_id = (:affiliateId))) " +
                    "             AND ((:campaignid) IS NULL OR (vall.campaign_id = (:campaignid))) " +
                    "             AND ((:dictionaryList) IS NULL OR (vall.dictionary_id in (:dictionaryList))) " +
                    "             AND ((:statusList) IS NULL OR (vall.status_id in (:statusList))) " +
                    "           group by vall.campaign_id, vall.campaign_name, tca.id_file, tca.initial_budget, tca.budget) " +
                    "SELECT distinct campaignid, " +
                    "                campaignname, " +
                    "                COALESCE(SUM(impressionnumber), 0)                                                                       as impressionNumber, " +
                    "                COALESCE(SUM(clicknumber), 0)                                                                            as clickNumber, " +
                    "                COALESCE(SUM(leadnumber), 0)                                                                             as leadNumber, " +
                    "                COALESCE(round((SUM(clicknumber) / SUM(nullif(impressionnumber, 0)) * 100), 2), 0)                       as CTR, " +
                    "                COALESCE(round((SUM(leadnumber) / SUM(nullif(clicknumber, 0)) * 100), 2), 0)                             as LR, " +
                    "                CONCAT(COALESCE(SUM(commission), 0), '')                                                                 as commission, " +
                    "                CONCAT(COALESCE(SUM(revenue), 0), '')                                                                    as revenue, " +
                    "                CONCAT(COALESCE(SUM(margine), 0), '')                                                                    as margine, " +
                    "                COALESCE(round(CAST((SUM(revenue) - SUM(commission)) / SUM(nullif(revenue, 0)) * 100 AS numeric), 2), 0) as marginePC, " +
                    "                COALESCE(round(SUM(commission) / SUM(nullif(impressionnumber, 0)) * 1000, 2), 0)                         as ecpm, " +
                    "                COALESCE(round(SUM(commission) / SUM(nullif(clicknumber, 0)), 2), 0)                                     as ecpc, " +
                    "                COALESCE(round(SUM(commission) / SUM(nullif(leadnumber, 0)), 2), 0)                                      as ecpl, " +
                    "                CONCAT(COALESCE(SUM(initialBudget), 0), '')                                                              as initialBudget, " +
                    "                CONCAT(COALESCE(SUM(budget), 0), '')                                                                     as budget, " +
                    "                COALESCE(round(CAST((initialBudget - budget) / nullif(initialBudget, 0) * 100 AS numeric), 2), 0)        as budgetGivenPC, " +
                    "                COALESCE(round(CAST(budget / nullif(initialBudget, 0) * 100 AS numeric), 2), 0)                          as budgetPC " +
                    "FROM w " +
                    "GROUP BY ROLLUP ((w.campaignid, w.campaignname, w.initialBudget, w.budget)) " +
                    "order by campaignname nulls last"
    )
    List<ReportTopCampaings> searchTopCampaigns(@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo, @Param("affiliateId") Long affiliateId, @Param("campaignid") Long campaignid, @Param("dictionaryList") List<Long> dictionaryList, @Param("statusList") List<Long> statusList);

    @Query(nativeQuery = true, value =
           " Select distinct vall.campaign_id as campaignid, " +
                   "                           vall.campaign_name                                                                              as campaignname, " +
                   "                           tca.id_file                                                                                     as fileid, " +
                   "                           vall.affiliate_id                                                                               as affiliateId, " +
                   "                           vall.affiliate_name                                                                             as affiliateName, " +
                   "                           COALESCE(SUM(impression_number), 0)                                                             as impressionNumber, " +
                   "                           COALESCE(SUM(click_number), 0)                                                                  as clickNumber, " +
                   "                           COALESCE(SUM(lead_number), 0)                                                                   as leadNumber, " +
                   "                           COALESCE(round((SUM(click_number) / SUM(nullif(lead_number, 0)) * 100), 2), 0)                  as CTR, " +
                   "                           COALESCE(round(CAST((SUM(lead_number) / SUM(nullif(click_number, 0)) * 100) AS numeric), 2), 0) as LR, " +
                   "                           COALESCE(round(CAST(SUM(value) AS numeric), 2), 0)                                   as commission, " +
                   "                           COALESCE(round(CAST(SUM(revenue) AS numeric), 2), 0)                                              as revenue, " +
                   "                           COALESCE(round(CAST((SUM(revenue) - SUM(value)) AS numeric), 2), 0)                    as margine, " +
                   "                           COALESCE(round(CAST((SUM(revenue) - SUM(value)) AS numeric) / CAST(SUM(nullif(revenue, 0)) AS numeric) * 100, 2), " +
                   "                                    0)                                                                                     as marginePC, " +
                   "                           COALESCE(round(CAST(SUM(nullif(value, 0)) / SUM(nullif(impression_number, 0)) * 1000 AS numeric), 2), " +
                   "                                    0)                                                                                     as ecpm, " +
                   "                           COALESCE(round(CAST(SUM(value) / SUM(nullif(click_number, 0)) AS numeric), 2), " +
                   "                                    0)                                                                                     as ecpc, " +
                   "                           COALESCE(round(CAST(SUM(value) / SUM(nullif(lead_number, 0)) AS numeric), 2), " +
                   "                                    0)                                                                                     as ecpl, " +
                   "                           COALESCE(tca.initial_budget, 0)                                                                 as initialBudget, " +
                   "                           COALESCE(tca.budget, 0)                                                                         as budget, " +
                   "                           COALESCE(round(CAST((tca.initial_budget - tca.budget) / " +
                   "                                               nullif(tca.initial_budget, 0) * 100 AS numeric), 2), " +
                   "                                    0)                                                                                     as budgetGivenPC, " +
                   "                           COALESCE(round(CAST(tca.budget / nullif(tca.initial_budget, 0) * 100 AS numeric), 2), " +
                   "                                    0)                                                                                     as budgetPC " +
                   "           from v_transactions_status vall " +
                   "                    left join t_channel tc on vall.channel_id = tc.id " +
                   "                    left join t_campaign tca on vall.campaign_id = tca.id " +
                   "           where (cast(:dateFrom as date) IS NULL OR (:dateFrom <= vall.date_time)) " +
                   "             AND (cast(:dateTo as date) IS NULL OR (:dateTo >= vall.date_time)) " +
                   "             AND ((:affiliateId) IS NULL OR (vall.affiliate_id = (:affiliateId))) " +
                   "           group by vall.campaign_id, vall.campaign_name, tca.id_file, tca.initial_budget, tca.budget, vall.affiliate_id, vall.affiliate_name, tca.id_file " +
                   "order by impressionNumber desc"
    )
    List<ReportTopCampaings> searchTopCampaignsImp(@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo, @Param("affiliateId") Long affiliateId);

    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================

    @Query(nativeQuery = true, value = "WITH w as (Select distinct vall.campaign_id                                                                                as campaignid, " +
            "                           vall.campaign_name                                                                              as campaignname, " +
            "                           vall.channel_id                                                                                 as channelId, " +
            "                           tc.name                                                                                         as channelName, " +
            "                           vall.affiliate_id                                                                               as affiliateId, " +
            "                           vall.affiliate_name                                                                             as affiliateName, " +
            "                           COALESCE(SUM(impression_number), 0)                                                             as impressionNumber, " +
            "                           COALESCE(SUM(click_number), 0)                                                                  as clickNumber, " +
            "                           COALESCE(SUM(lead_number), 0)                                                                   as leadNumber, " +
            "                           COALESCE(round((SUM(click_number) / SUM(nullif(lead_number, 0)) * 100), 2), 0)                  as CTR, " +
            "                           COALESCE(round(CAST((SUM(lead_number) / SUM(nullif(click_number, 0)) * 100) AS numeric), 2), 0) as LR, " +
            "                           COALESCE(round(CAST(SUM(value) AS numeric), 2), 0)                                   as commission, " +
            "                           COALESCE(round(CAST(SUM(revenue) AS numeric), 2), 0)                                              as revenue, " +
            "                           COALESCE(round(CAST((SUM(revenue) - SUM(value)) AS numeric), 2), 0)                    as margine, " +
            "                           COALESCE(round(CAST((SUM(revenue) - SUM(value)) AS numeric) / CAST(SUM(nullif(revenue, 0)) AS numeric) * 100, 2), " +
            "                                    0)                                                                                     as marginePC, " +
            "                           COALESCE(round(CAST(SUM(nullif(value, 0)) / SUM(nullif(impression_number, 0)) * 1000 AS numeric), 2), " +
            "                                    0)                                                                                     as ecpm, " +
            "                           COALESCE(round(CAST(SUM(value) / SUM(nullif(click_number, 0)) AS numeric), 2), " +
            "                                    0)                                                                                     as ecpc, " +
            "                           COALESCE(round(CAST(SUM(value) / SUM(nullif(lead_number, 0)) AS numeric), 2), " +
            "                                    0)                                                                                     as ecpl, " +
            "                           COALESCE(tca.initial_budget, 0)                                                                 as initialBudget, " +
            "                           COALESCE(tca.budget, 0)                                                                         as budget, " +
            "                           COALESCE(round(CAST((tca.initial_budget - tca.budget) / " +
            "                                               nullif(tca.initial_budget, 0) * 100 AS numeric), 2), " +
            "                                    0)                                                                                     as budgetGivenPC, " +
            "                           COALESCE(round(CAST(tca.budget / nullif(tca.initial_budget, 0) * 100 AS numeric), 2), " +
            "                                    0)                                                                                     as budgetPC " +
            "           from v_transactions_status vall " +
            "                    left join t_channel tc on vall.channel_id = tc.id " +
            "                    left join t_affiliate ta on vall.affiliate_id = ta.id " +
            "                    left join t_campaign tca on vall.campaign_id = tca.id " +
            "           where (cast(:dateFrom as date) IS NULL OR (:dateFrom <= vall.date_time)) " +
            "             AND (cast(:dateTo as date) IS NULL OR (:dateTo >= vall.date_time + interval '24 hours')) " +
            "             AND ((:affiliateId) IS NULL OR (vall.affiliate_id = (:affiliateId))) " +
            "             AND ((:campaignid) IS NULL OR (vall.campaign_id = (:campaignid))) " +
            "             AND ((:dictionaryList) IS NULL OR (vall.dictionary_id in (:dictionaryList))) " +
            "             AND ((:statusList) IS NULL OR (vall.status_id in (:statusList))) " +
            "           group by vall.campaign_id, vall.campaign_name, vall.channel_id, vall.affiliate_name, vall.affiliate_id, tc.name, tca.initial_budget, tca.budget) " +
            "SELECT distinct campaignid, " +
            "                campaignname, " +
            "                channelid                                                                                                AS channelId, " +
            "                channelname                                                                                              AS channelName, " +
            "                affiliateId                                                                                              as affiliateId, " +
            "                affiliateName                                                                                            as affiliateName, " +
            "                COALESCE(SUM(impressionnumber), 0)                                                                       as impressionNumber, " +
            "                COALESCE(SUM(clicknumber), 0)                                                                            as clickNumber, " +
            "                COALESCE(SUM(leadnumber), 0)                                                                             as leadNumber, " +
            "                COALESCE(round((SUM(clicknumber) / SUM(nullif(impressionnumber, 0)) * 100), 2), 0)                       as CTR, " +
            "                COALESCE(round((SUM(leadnumber) / SUM(nullif(clicknumber, 0)) * 100), 2), 0)                             as LR, " +
            "                CONCAT(COALESCE(SUM(commission), 0), '')                                                                 as commission, " +
            "                CONCAT(COALESCE(SUM(revenue), 0), '')                                                                    as revenue, " +
            "                CONCAT(COALESCE(SUM(margine), 0), '')                                                                    as margine, " +
            "                COALESCE(round(CAST((SUM(revenue) - SUM(commission)) / SUM(nullif(revenue, 0)) * 100 AS numeric), 2), 0) as marginePC, " +
            "                COALESCE(round(SUM(commission) / SUM(nullif(impressionnumber, 0)) * 1000, 2), 0)                         as ecpm, " +
            "                COALESCE(round(SUM(commission) / SUM(nullif(clicknumber, 0)), 2), 0)                                     as ecpc, " +
            "                COALESCE(round(SUM(commission) / SUM(nullif(leadnumber, 0)), 2), 0)                                      as ecpl, " +
            "                CONCAT(COALESCE(SUM(initialBudget), 0), '')                                                              as initialBudget, " +
            "                CONCAT(COALESCE(SUM(budget), 0), '')                                                                     as budget, " +
            "                COALESCE(round(CAST((initialBudget - budget) / nullif(initialBudget, 0) * 100 AS numeric), 2), 0)        as budgetGivenPC, " +
            "                COALESCE(round(CAST(budget / nullif(initialBudget, 0) * 100 AS numeric), 2), 0)                          as budgetPC " +
            "FROM w " +
            " " +
            "GROUP BY ROLLUP ((w.campaignid, w.campaignname, w.channelName, w.channelId, w.affiliateName, w.affiliateId, w.initialBudget, w.budget)) " +
            "order by affiliateName nulls last")
    List<ReportTopCampaings> searchTopCampaignsChannel(@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo, @Param("affiliateId") Long affiliateId, @Param("campaignid") Long campaignid, @Param("dictionaryList") List<Long> dictionaryList, @Param("statusList") List<Long> statusList);

    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================

    @Query(nativeQuery = true, value = "WITH w as (Select distinct vall.affiliate_id                                                                                                   as affiliateId, " +
            "                  vall.affiliate_name                                                                                                 as affiliateName, " +
            "                  COALESCE(SUM(impression_number), 0)                                                                                 as impressionNumber, " +
            "                  COALESCE(SUM(click_number), 0)                                                                                      as clickNumber, " +
            "                  COALESCE(SUM(lead_number), 0)                                                                                       as leadNumber, " +
            "                  COALESCE(round((SUM(click_number) / SUM(nullif(impression_number, 0)) * 100), 2), 0)                                as CTR, " +
            "                  COALESCE(round(CAST((SUM(lead_number) / SUM(nullif(click_number, 0)) * 100) AS numeric), 2), 0)                     as LR, " +
            "                  COALESCE(round(CAST(SUM(value) AS numeric), 2), 0)                                                       as commission, " +
            "                  COALESCE(round(CAST(SUM(revenue) AS numeric), 2), 0)                                                                as revenue, " +
            "                  COALESCE(round(CAST((SUM(revenue) - SUM(value)) AS numeric), 2), 0)                                      as margine, " +
            "                  COALESCE(round(CAST((SUM(revenue) - SUM(value)) AS numeric) / CAST(SUM(nullif(revenue, 0)) AS numeric) * 100, 2), " +
            "                           0)                                                                                                         as marginePC, " +
            "                  COALESCE(round(CAST(SUM(nullif(value, 0)) / SUM(nullif(impression_number, 0)) * 1000 AS numeric), 2), 0) as ecpm, " +
            "                  COALESCE(round(CAST(SUM(value) / SUM(nullif(click_number, 0)) AS numeric), 2), 0)                        as ecpc, " +
            "                  COALESCE(round(CAST(SUM(value) / SUM(nullif(lead_number, 0)) AS numeric), 2), 0)                         as ecpl " +
            "           from v_transactions_status vall " +
            "                    left join t_channel tc on vall.channel_id = tc.id " +
            "                    left join t_affiliate ta on vall.affiliate_id = ta.id " +
            "           where (cast(:dateFrom as date) IS NULL OR (:dateFrom <= vall.date_time)) " +
            "             AND (cast(:dateTo as date) IS NULL OR (:dateTo >= vall.date_time)) " +
            "             AND ((:affiliateId) IS NULL OR (vall.affiliate_id = (:affiliateId))) " +
            "             AND ((:campaignid) IS NULL OR (vall.campaign_id = (:campaignid))) " +
            "             AND ((:dictionaryList) IS NULL OR (vall.dictionary_id in (:dictionaryList))) " +
            "             AND ((:statusList) IS NULL OR (vall.status_id in (:statusList))) " +
            "           group by vall.affiliate_name, vall.affiliate_id) " +
            "Select distinct w.affiliateId, " +
            "       w.affiliateName, " +
            "                  COALESCE(SUM(w.impressionNumber), 0)                                                                                 as impressionNumber, " +
            "                  COALESCE(SUM(w.clickNumber), 0)                                                                                      as clickNumber, " +
            "                  COALESCE(SUM(w.leadNumber), 0)                                                                                       as leadNumber, " +
            "                  COALESCE(round((SUM(w.clickNumber) / SUM(nullif(w.impressionNumber, 0)) * 100), 2), 0)                                      as CTR, " +
            "                  COALESCE(round(CAST((SUM(w.leadNumber) / SUM(nullif(w.clickNumber, 0)) * 100) AS numeric), 2), 0)                     as LR, " +
            "                  COALESCE(round(CAST(SUM(w.commission) AS numeric), 2), 0)                                                       as commission, " +
            "                  COALESCE(round(CAST(SUM(revenue) AS numeric), 2), 0)                                                                as revenue, " +
            "                  COALESCE(round(CAST((SUM(revenue) - SUM(w.commission)) AS numeric), 2), 0)                                      as margine, " +
            "                  COALESCE(round(CAST((SUM(revenue) - SUM(w.commission)) AS numeric) / CAST(SUM(nullif(revenue, 0)) AS numeric) * 100, 2), " +
            "                           0)                                                                                                         as marginePC, " +
            "                  COALESCE(round(CAST(SUM(nullif(w.commission, 0)) / SUM(nullif(w.impressionNumber, 0)) * 1000 AS numeric), 2), 0) as ecpm, " +
            "                  COALESCE(round(CAST(SUM(w.impressionNumber) / SUM(nullif(w.clickNumber, 0)) AS numeric), 2), 0)                        as ecpc, " +
            "                  COALESCE(round(CAST(SUM(w.impressionNumber) / SUM(nullif(w.leadNumber, 0)) AS numeric), 2), 0)                         as ecpl " +
            "FROM w " +
            "GROUP BY ROLLUP ((w.affiliateId, w.affiliateName, w.impressionNumber, w.clickNumber, w.leadNumber, w.CTR, w.LR, " +
            "                  w.commission, w.revenue, " +
            "                  w.margine, w.marginePC, " +
            "                  w.ecpc, w.ecpc, w.ecpl)) " +
            "order by w.affiliateName ASC nulls last" +
            "" 
            )
    List<ReportTopAffiliates> searchTopAffilaites(@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo, @Param("affiliateId") Long affiliateId, @Param("campaignid") Long campaignid, @Param("dictionaryList") List<Long> dictionaryList, @Param("statusList") List<Long> statusList);

    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================

    @Query(nativeQuery = true, value = "WITH w as (Select distinct vall.affiliate_id                                                                                                   as affiliateId, " +
            "                  vall.affiliate_name                                                                                                 as affiliateName, " +
            "                  vall.channel_id                                                                                                     as channelId, " +
            "                  tc.name                                                                                                             as channelName, " +
            "                  COALESCE(SUM(impression_number), 0)                                                                                 as impressionNumber, " +
            "                  COALESCE(SUM(click_number), 0)                                                                                      as clickNumber, " +
            "                  COALESCE(SUM(lead_number), 0)                                                                                       as leadNumber, " +
            "                  COALESCE(round((SUM(click_number) / SUM(nullif(impression_number, 0)) * 100), 2), 0)                                as CTR, " +
            "                  COALESCE(round(CAST((SUM(lead_number) / SUM(nullif(click_number, 0)) * 100) AS numeric), 2), 0)                     as LR, " +
            "                  COALESCE(round(CAST(SUM(value) AS numeric), 2), 0)                                                       as commission, " +
            "                  COALESCE(round(CAST(SUM(revenue) AS numeric), 2), 0)                                                                as revenue, " +
            "                  COALESCE(round(CAST((SUM(revenue) - SUM(value)) AS numeric), 2), 0)                                      as margine, " +
            "                  COALESCE(round(CAST((SUM(revenue) - SUM(value)) AS numeric) / CAST(SUM(nullif(revenue, 0)) AS numeric) * 100, 2), " +
            "                           0)                                                                                                         as marginePC, " +
            "                  COALESCE(round(CAST(SUM(nullif(value, 0)) / SUM(nullif(impression_number, 0)) * 1000 AS numeric), 2), 0) as ecpm, " +
            "                  COALESCE(round(CAST(SUM(value) / SUM(nullif(click_number, 0)) AS numeric), 2), 0)                        as ecpc, " +
            "                  COALESCE(round(CAST(SUM(value) / SUM(nullif(lead_number, 0)) AS numeric), 2), 0)                         as ecpl " +
            "           from v_transactions_status vall " +
            "                    left join t_channel tc on vall.channel_id = tc.id " +
            "                    left join t_affiliate ta on vall.affiliate_id = ta.id " +
            "           where (cast(:dateFrom as date) IS NULL OR (:dateFrom <= vall.date_time)) " +
            "             AND (cast(:dateTo as date) IS NULL OR (:dateTo >= vall.date_time)) " +
            "             AND ((:affiliateId) IS NULL OR (vall.affiliate_id = (:affiliateId))) " +
            "             AND ((:campaignid) IS NULL OR (vall.campaign_id = (:campaignid))) " +
            "             AND ((:dictionaryList) IS NULL OR (vall.dictionary_id in (:dictionaryList))) " +
            "             AND ((:statusList) IS NULL OR (vall.status_id in (:statusList))) " +
            "           group by vall.affiliate_name, tc.name, vall.channel_id, vall.affiliate_id) " +
            "Select distinct w.affiliateId, " +
            "       w.affiliateName, " +
            "       w.channelId, " +
            "       w.channelName, " +
            "                  COALESCE(SUM(w.impressionNumber), 0)                                                                                 as impressionNumber, " +
            "                  COALESCE(SUM(w.clickNumber), 0)                                                                                      as clickNumber, " +
            "                  COALESCE(SUM(w.leadNumber), 0)                                                                                       as leadNumber, " +
            "                  COALESCE(round((SUM(w.clickNumber) / SUM(nullif(w.impressionNumber, 0)) * 100), 2), 0)                                      as CTR, " +
            "                  COALESCE(round(CAST((SUM(w.leadNumber) / SUM(nullif(w.clickNumber, 0)) * 100) AS numeric), 2), 0)                     as LR, " +
            "                  COALESCE(round(CAST(SUM(w.commission) AS numeric), 2), 0)                                                       as commission, " +
            "                  COALESCE(round(CAST(SUM(revenue) AS numeric), 2), 0)                                                                as revenue, " +
            "                  COALESCE(round(CAST((SUM(revenue) - SUM(w.commission)) AS numeric), 2), 0)                                      as margine, " +
            "                  COALESCE(round(CAST((SUM(revenue) - SUM(w.commission)) AS numeric) / CAST(SUM(nullif(revenue, 0)) AS numeric) * 100, 2), " +
            "                           0)                                                                                                         as marginePC, " +
            "                  COALESCE(round(CAST(SUM(nullif(w.commission, 0)) / SUM(nullif(w.impressionNumber, 0)) * 1000 AS numeric), 2), 0) as ecpm, " +
            "                  COALESCE(round(CAST(SUM(w.impressionNumber) / SUM(nullif(w.clickNumber, 0)) AS numeric), 2), 0)                        as ecpc, " +
            "                  COALESCE(round(CAST(SUM(w.impressionNumber) / SUM(nullif(w.leadNumber, 0)) AS numeric), 2), 0)                         as ecpl " +
            "FROM w " +
            "GROUP BY ROLLUP ((w.affiliateId, w.affiliateName, w.channelId, w.channelName, w.impressionNumber, w.clickNumber, w.leadNumber, w.CTR, w.LR, " +
            "                  w.commission, w.revenue, " +
            "                  w.margine, w.marginePC, " +
            "                  w.ecpc, w.ecpc, w.ecpl)) " +
            "order by w.affiliateName ASC nulls last" +
            ""
    )
    List<ReportTopAffiliates> searchTopAffilaitesChannel(@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo, @Param("affiliateId") Long affiliateId, @Param("campaignid") Long campaignid, @Param("dictionaryList") List<Long> dictionaryList, @Param("statusList") List<Long> statusList);

    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================
    //=========================================================================================================================

    @Query(nativeQuery = true, value = "WITH w as (Select distinct datadx                                                                                   as giorno, " +
            "                           COALESCE(SUM(dt.impressionnumber), 0)                                                    as impressionNumber, " +
            "                           COALESCE(SUM(dt.impressionnumberrigettato), 0)                                           as impressionNumberRigettato, " +
            "                           COALESCE(SUM(dt.clicknumber), 0)                                                         as clickNumber, " +
            "                           COALESCE(SUM(dt.clicknumberrigettato), 0)                                                as clickNumberRigettato, " +
            "                           COALESCE(SUM(dt.leadnumber), 0)                                                          as leadNumber, " +
            "                           COALESCE(SUM(dt.leadnumberrigettato), 0)                                                 as leadNumberRigettato, " +
            "                           COALESCE(round((SUM(dt.clicknumber) / SUM(dt.impressionnumber) * 100), 2), 0)            as CTR, " +
            "                           COALESCE(round(CAST((SUM(dt.leadnumber) / SUM(dt.clicknumber) * 100) AS numeric), 2), 0) as LR, " +
            "                           COALESCE(round(CAST(SUM(dt.commission) AS numeric), 2), 0)                               as commission, " +
            "                           COALESCE(round(CAST(SUM(dt.revenue) AS numeric), 2), 0)                                  as revenue, " +
            "                           COALESCE(round(CAST((SUM(dt.revenue) - SUM(dt.commission)) AS numeric), 2), 0)           as margine, " +
            "                           COALESCE(round(CAST((SUM(dt.revenue) - SUM(dt.commission)) AS numeric) / CAST(SUM(nullif(dt.revenue, 0)) AS numeric) * 100, " +
            "                                          2), 0)                                                                    as marginePC, " +
            "                           COALESCE(round(SUM(dt.commission) / SUM(dt.impressionnumber) * 1000, 2), 0)              as ecpm, " +
            "                           COALESCE(round(SUM(dt.commission) / SUM(dt.clicknumber), 2), 0)                          as ecpc, " +
            "                           COALESCE(round(CAST(SUM(dt.commission) / SUM(dt.leadnumber) AS numeric), 2), 0)          as ecpl " +
            "           from v_daily_transactions_all dt " +
            "           where (cast(:dateFrom as date) IS NULL OR (:dateFrom <= dt.datadx)) " +
            "             AND (cast(:dateTo as date) IS NULL OR (:dateTo >= dt.datadx + interval '24 hours')) " +
            "             AND ((:affiliateId) IS NULL OR (dt.affilaiteid = (:affiliateId))) " +
            "             AND ((:campaignid) IS NULL OR (dt.campaignid = (:campaignid))) " +
            "             AND ((:dictionaryList) IS NULL OR (dt.dictinaryid in (:dictionaryList))) " +
            "             AND ((:statusList) IS NULL OR (dt.statusid in (:statusList))) " +
            "           group by dt.datadx) " +
            "SELECT giorno, " +
            "       COALESCE(SUM(impressionnumber), 0)                                                                       as impressionNumber, " +
            "       COALESCE(SUM(impressionnumberrigettato), 0)                                                              as impressionNumberRigettato, " +
            "       COALESCE(SUM(clicknumber), 0)                                                                            as clickNumber, " +
            "       COALESCE(SUM(clicknumberrigettato), 0)                                                                   as clickNumberRigettato, " +
            "       COALESCE(SUM(leadnumber), 0)                                                                             as leadNumber, " +
            "       COALESCE(SUM(leadnumberrigettato), 0)                                                                    as leadNumberRigettato, " +
            "       COALESCE(round((SUM(clicknumber) / SUM(nullif(impressionnumber, 0)) * 100), 2), 0)                       as CTR, " +
            "       COALESCE(round(CAST((SUM(leadnumber) / SUM(nullif(clicknumber, 0)) * 100) AS numeric), 2), 0)            as LR, " +
            "       COALESCE(round(CAST(SUM(commission) AS numeric), 2), 0)                                                  as commission, " +
            "       COALESCE(round(CAST(SUM(revenue) AS numeric), 2), 0)                                                     as revenue, " +
            "       COALESCE(round(CAST((SUM(revenue) - SUM(commission)) AS numeric), 2), 0)                                 as margine, " +
            "       COALESCE(round(CAST((SUM(revenue) - SUM(commission)) / SUM(nullif(revenue, 0)) * 100 AS numeric), 2), 0) as marginePC, " +
            "       COALESCE(round(SUM(commission) / SUM(nullif(impressionnumber, 0)) * 1000, 2), 0)                         as ecpm, " +
            "       COALESCE(round(SUM(commission) / SUM(nullif(clicknumber, 0)), 2), 0)                                     as ecpc, " +
            "       COALESCE(round(CAST(SUM(commission) / SUM(nullif(leadNumber, 0)) AS numeric), 2), 0)                     as ecpl " +
            "FROM w " +
            "GROUP BY ROLLUP ((giorno, impressionNumber, clickNumber, leadnumber, ctr, lr, commission, revenue, margine, marginePC, ecpm, ecpc, ecpl)) " +
            "order by giorno nulls last"
    )
    List<ReportDaily> searchDaily(@Param("dateFrom") LocalDate dateFrom, @Param("dateTo") LocalDate dateTo,
                                  @Param("affiliateId") Long affiliateId, @Param("campaignid") Long campaignid,
                                  @Param("dictionaryList") List<Long> dictionaryList, @Param("statusList") List<Long> statusList);

}