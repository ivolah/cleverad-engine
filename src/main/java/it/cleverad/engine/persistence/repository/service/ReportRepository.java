package it.cleverad.engine.persistence.repository.service;

import it.cleverad.engine.persistence.model.service.Report;
import it.cleverad.engine.web.dto.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long>, JpaSpecificationExecutor<Report> {

    @Query(nativeQuery = true)
    List<ReportDailyDTO> searchReportDaily(@Param("dateFrom") LocalDateTime dateFrom,
                                           @Param("dateTo") LocalDateTime dateTo,
                                           @Param("statusId") Long statusId,
                                           @Param("dictionaryId") Long dictionaryId,
                                           @Param("affiliateId") Long affiliateId,
                                           @Param("channelId") Long channelId,
                                           @Param("campaignId") Long campaignId,
                                           @Param("advertiserId") Long advertiserId,
                                           @Param("dictionaryId") Long inDictionaryId,
                                           @Param("inStausId") List<Long> inStausId);

    @Query(nativeQuery = true)
    List<ReportCampagneDTO> searchReportCampaign(@Param("dateFrom") LocalDateTime dateFrom,
                                                 @Param("dateTo") LocalDateTime dateTo,
                                                 @Param("statusId") Long statusId,
                                                 @Param("dictionaryId") Long dictionaryId,
                                                 @Param("affiliateId") Long affiliateId,
                                                 @Param("channelId") Long channelId,
                                                 @Param("campaignId") Long campaignId,
                                                 @Param("advertiserId") Long advertiserId,
                                                 @Param("dictionaryId") Long inDictionaryId,
                                                 @Param("inStausId") List<Long> inStausId);

    @Query(nativeQuery = true)
    List<ReportAffiliatesDTO> searchReportAffiliate(@Param("dateFrom") LocalDateTime dateFrom,
                                                    @Param("dateTo") LocalDateTime dateTo,
                                                    @Param("statusId") Long statusId,
                                                    @Param("dictionaryId") Long dictionaryId,
                                                    @Param("affiliateId") Long affiliateId,
                                                    @Param("channelId") Long channelId,
                                                    @Param("campaignId") Long campaignId,
                                                    @Param("advertiserId") Long advertiserId,
                                                    @Param("dictionaryId") Long inDictionaryId,
                                                    @Param("inStausId") List<Long> inStausId);

    @Query(nativeQuery = true)
    List<ReportAffiliatesChannelDTO> searchReportAffiliateChannel(@Param("dateFrom") LocalDateTime dateFrom,
                                                                  @Param("dateTo") LocalDateTime dateTo,
                                                                  @Param("statusId") Long statusId,
                                                                  @Param("dictionaryId") Long dictionaryId,
                                                                  @Param("affiliateId") Long affiliateId,
                                                                  @Param("channelId") Long channelId,
                                                                  @Param("campaignId") Long campaignId,
                                                                  @Param("advertiserId") Long advertiserId,
                                                                  @Param("dictionaryId") Long inDictionaryId,
                                                                  @Param("inStausId") List<Long> inStausId);

    @Query(nativeQuery = true)
    List<ReportAffiliatesChannelCampaignDTO> searchReportAffiliateChannelCampaign(@Param("dateFrom") LocalDateTime dateFrom,
                                                                                  @Param("dateTo") LocalDateTime dateTo,
                                                                                  @Param("statusId") Long statusId,
                                                                                  @Param("dictionaryId") Long dictionaryId,
                                                                                  @Param("affiliateId") Long affiliateId,
                                                                                  @Param("channelId") Long channelId,
                                                                                  @Param("campaignId") Long campaignId,
                                                                                  @Param("advertiserId") Long advertiserId,
                                                                                  @Param("dictionaryId") Long inDictionaryId,
                                                                                  @Param("inStausId") List<Long> inStausId);

    //=========================================================================================================================
    //=========================================================================================================================

}