package it.cleverad.engine.persistence.repository.service;

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
            "       al.campaignId as campaignId," +
            "       al.campaignName as campaignName," +
            "       sum(al.impressionNumber)                  as impressionNumber ," +
            "       sum(al.clickNumber)                       as clickNumber ," +
            "       sum(al.leadNumber)                        as leadNumber ," +
            "       sum(al.clickNumber) / sum(al.impressionNumber) as CTR," +
            "       sum(al.leadNumber) / sum(al.clickNumber)       as LR," +
            "       sum(al.value)                              as commssion," +
            "       tr.revenue                                 as revenue," +
            "       tr.revenue - sum(al.value)                 as margine," +
            "       (tr.revenue - sum(al.value)) / tr.revenue  as marginePC," +
            "       sum(al.value) / sum(al.impressionNumber)     as ecpm," +
            "       sum(al.value) / sum(al.leadNumber)           as ecpl," +
            "       sum(al.value) / sum(al.clickNumber)          as ecpc,"+
            "       al.dictionaryId as distionaryId" +
            " from TransactionAll al" +
            " left join RevenueFactor tr on al.campaignId = tr.campaign.id" +
            " where (:dateFrom < al.dateTime) " +
            " and (:dateTo > al.dateTime) " +
            " and (al.dictionaryId in (:dictionaryList)) " +
            " group by al.campaignId, al.campaignName, al.dictionaryId, tr.revenue")
    Page<TopCampaings> findGroupByCampaignId(Pageable pageableRequest, @Param("dateFrom") LocalDateTime dateFrom, @Param("dateTo") LocalDateTime dateTo, @Param("dictionaryList") List<Long> dictionaryList);

}

