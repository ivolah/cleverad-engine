package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.TopAffiliates;
import it.cleverad.engine.persistence.model.service.TopCampaings;
import it.cleverad.engine.persistence.model.service.TransactionAll;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TransactionAllRepository extends JpaRepository<TransactionAll, Long>, JpaSpecificationExecutor<TransactionAll> {

    @Query("SELECT count(*)," +
            "       al.campaign_id                                               as campaignId," +
            "       al.campaign_name                                             as campaignName," +
            "       sum(impression_number)                                       as impression," +
            "       sum(click_number)                                            as click_number," +
            "       sum(lead_number)                                             as lead_number," +
            "       sum(click_number) / sum(impression_number)                   as CTR," +
            "       sum(lead_number) / sum(click_number)                         as LR," +
            "       sum(al.value)                                                as commssion," +
            "       revenue * sum(click_number)                                  as revenue," +
            "       (revenue * sum(click_number)) - sum(al.value)                as margine," +
            "       ((revenue * sum(click_number)) - sum(al.value)) / tr.revenue as marginePC," +
            "       (sum(al.value) / sum(impression_number)) * 1000              as ecpm," +
            "       sum(al.value) / sum(lead_number)                             as ecpl," +
            "       sum(al.value) / sum(click_number)                            as ecpc," +
            "       al.dictionary_id" +
            " from TransactionAll al" +
            " left join RevenueFactor tr on al.campaignId = tr.campaign.id" +
            " where (:dateFrom < al.dateTime) " +
            " and (:dateTo > al.dateTime) " +
            " and (al.dictionaryId in (:dictionaryList)) " +
            " group by al.campaignId, al.campaignName,al.dictionaryName, al.dictionaryId, tr.revenue, al.budget")
    Page<TopCampaings> findGroupByCampaignId(Pageable pageableRequest, @Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo, @Param("dictionaryList") List<Long> dictionaryList);

    @Query("SELECT count(*),  " +
            "       al.affiliateId                            as affiliateId,  " +
            "       al.affiliateName                          as affiliateName,  " +
            "       al.channelId                              as channelId,  " +
            "       al.channelName                            as channelName,  " +
            "       sum(al.impressionNumber)                  as impressionNumber ," +
            "       sum(al.clickNumber)                       as clickNumber ," +
            "       sum(al.leadNumber)                        as leadNumber ," +
            "       sum(al.clickNumber) / sum(al.impressionNumber) *100 as CTR," +
            "       sum(al.leadNumber) / sum(al.clickNumber)  *100     as LR," +
            "       sum(al.value)                              as commission,  " +
            "       tr.revenue * sum(al.clickNumber)          as revenue," +
            "       (tr.revenue * sum(al.clickNumber)) - sum(al.value)                 as margine," +
            "       ((tr.revenue * sum(al.clickNumber)) - sum(al.value)) / tr.revenue  as marginePC," +
            "       sum(al.value) / sum(al.impressionNumber)  *1000   as ecpm," +
            "       sum(al.value) / (sum(al.leadNumber))           as ecpl," +
            "       sum(al.value) / sum(al.clickNumber)          as ecpc," +
            "       al.dictionaryId as dictionaryId, " +
            "       al.dictionaryName as dictionaryName " +
            " from TransactionAll al" +
            " left join RevenueFactor tr on al.campaignId = tr.campaign.id" +
            " where (:dateFrom < al.dateTime) " +
            " and (:dateTo > al.dateTime) " +
            " and (al.dictionaryId in (:dictionaryList)) " +
            " group by al.affiliateId, al.affiliateName, al.dictionaryId,al.dictionaryName, al.channelId, al.channelName, tr.revenue, al.budget")
    Page<TopAffiliates> findAffiliatesGroupByCampaignId(Pageable pageableRequest, @Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo, @Param("dictionaryList") List<Long> dictionaryList);


//    @Query("SELECT distinct(cpc.ip), " +
//            "               count(cpc.ip), " +
//            "               cpc.refferal, " +
//            "               cpc.agent, " +
//            "               date_trunc('day', cpc.date) as day" +
//            "from t_cpc as cpc " +
//            " where (:dateFrom < al.dateTime) " +
//            " and (:dateTo > al.dateTime) " +
//            "group by cpc.ip, cpc.refferal, cpc.agent, date_trunc('day', cpc.date)")
//    Page<StatClickCpc> getGroupedCpcClicks(Pageable pageableRequest, @Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo);
//

}

