package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.Campaign;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignDTO {

    private long id;
    private String name;
    private String shortDescription;
    private String longDescription;
    private Boolean status;
    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String idFile;
    private String valuta;
    private Long budget;
    private String trackingCode;
    private String encodedId;

    private Long cookieId;
    private String cookieName;
    private String cookieValue;

    private Long companyId;
    private String companyName;

    private Long dealerId;
    private String dealerName;

    private Long plannerId;
    private String plannerName;

    private List<BasicMediaDTO> medias;
    private List<BasicAffiliateDTO> affiliates;
    private List<CommissionDTO> commissions;
    private List<BasicCategoryDTO> categories;
    //    private List<BasicCookieDTO> cookies;
    private List<RevenueFactorDTO> revenues;
    private List<AffiliateChannelCommissionCampaignDTO> affiliateChannelCommissionCampaigns;
    private List<Long> categoryList;


    public static CampaignDTO from(Campaign campaign) {

        List<BasicMediaDTO> medias = null;
        if (campaign.getMedias() != null) {
            medias = campaign.getMedias().stream().map(media -> {
                BasicMediaDTO mediaDTO = new BasicMediaDTO();
                mediaDTO.setId(media.getId());
                mediaDTO.setName(media.getName());
                mediaDTO.setNote(media.getNote());
                mediaDTO.setTarget(media.getTarget());
                mediaDTO.setUrl(media.getUrl());
                mediaDTO.setTypeId(String.valueOf(media.getMediaType().getId()));
                return mediaDTO;
            }).collect(Collectors.toList());
        }

        List<BasicAffiliateDTO> affiliates = null;
        //        if (campaign.getAffiliateCampaigns() != null) {
        //            affiliates = campaign.getAffiliateCampaigns().stream().map(affiliateCampaign -> {
        //                BasicAffiliateDTO dto = new BasicAffiliateDTO();
        //                dto.setId(affiliateCampaign.getAffiliate().getId());
        //                dto.setName(affiliateCampaign.getAffiliate().getName());
        //                dto.setPrimaryMail(affiliateCampaign.getAffiliate().getPrimaryMail());
        //                return dto;
        //            }).collect(Collectors.toList());
        //        }

        List<CommissionDTO> commissions = null;
        if (campaign.getCommissionCampaigns() != null) {
            commissions = campaign.getCommissionCampaigns().stream().map(commissionCampaign -> {
                CommissionDTO dto = new CommissionDTO();
                dto.setId(commissionCampaign.getId());
                dto.setName(commissionCampaign.getName());
                dto.setValue(commissionCampaign.getValue());
                dto.setDescription(commissionCampaign.getDescription());
                dto.setStatus(commissionCampaign.getStatus());
                dto.setDueDate(commissionCampaign.getDueDate());
                dto.setCampaignId(commissionCampaign.getCampaign().getId());
                dto.setCreationDate(commissionCampaign.getCreationDate());
                dto.setLastModificationDate(commissionCampaign.getLastModificationDate());
                return dto;
            }).collect(Collectors.toList());
        }

        List<BasicCategoryDTO> categoryDTOS = new ArrayList<>();
        if (campaign.getCampaignCategories() != null) {
            categoryDTOS = campaign.getCampaignCategories().stream().map(campaignCategory -> {
                BasicCategoryDTO dto = new BasicCategoryDTO();
                dto.setId(campaignCategory.getCategory().getId());
                dto.setName(campaignCategory.getCategory().getName());
                dto.setCode(campaignCategory.getCategory().getCode());
                dto.setDescription(campaignCategory.getCategory().getDescription());
                return dto;
            }).collect(Collectors.toList());
        }

        List<Long> categoryList = new ArrayList<>();
        //        String catergoryList = "";
        if (categoryDTOS.size() > 0)
            for (BasicCategoryDTO basicCategoryDTO : Objects.requireNonNull(categoryDTOS)) {
                categoryList.add(basicCategoryDTO.getId());
            }

        //        List<BasicCookieDTO> cookie = null;
        //        if (campaign.getCampaignCookies() != null) {
        //            cookie = campaign.getCampaignCookies().stream().map(campaignCookie -> {
        //                BasicCookieDTO dto = new BasicCookieDTO();
        //                dto.setId(campaignCookie.getCookie().getId());
        //                dto.setName(campaignCookie.getCookie().getName());
        //                dto.setStatus(campaignCookie.getCookie().getStatus());
        //                dto.setValue(campaignCookie.getCookie().getValue());
        //                return dto;
        //            }).collect(Collectors.toList());
        //        }

        List<RevenueFactorDTO> revenues = null;
        if (campaign.getRevenueFactors() != null) {
            revenues = campaign.getRevenueFactors().stream().map(factor -> {
                RevenueFactorDTO dto = new RevenueFactorDTO();
                dto.setId(factor.getId());
                dto.setRevenue(factor.getRevenue());
                dto.setStatus(factor.getStatus());
                dto.setDictionaryName(factor.getDictionary().getName());
                dto.setDueDate(factor.getDueDate());
                dto.setCreationDate(factor.getCreationDate());
                dto.setLastModificationDate(factor.getLastModificationDate());
                return dto;
            }).collect(Collectors.toList());
        }

        List<AffiliateChannelCommissionCampaignDTO> accc = null;
        if (campaign.getAffiliateChannelCommissionCampaigns() != null) {
            accc = campaign.getAffiliateChannelCommissionCampaigns().stream().map(acccc -> {
                AffiliateChannelCommissionCampaignDTO dto = new AffiliateChannelCommissionCampaignDTO();
                dto.setId(acccc.getId());
                dto.setCampaignId(acccc.getCampaign().getId());
                dto.setAffiliateId(acccc.getAffiliate().getId());
                dto.setAffilateName(acccc.getAffiliate().getName());
                dto.setChannelId(acccc.getChannel().getId());
                dto.setChannelName(acccc.getChannel().getName());
                dto.setCommissionId(acccc.getCommission().getId());
                dto.setCommissionName(acccc.getCommission().getName());
                dto.setCreationDate(acccc.getCreationDate());
                dto.setLastModificationDate(acccc.getLastModificationDate());
                return dto;
            }).collect(Collectors.toList());
        }

        return new CampaignDTO(campaign.getId(), campaign.getName(), campaign.getShortDescription(),
                campaign.getLongDescription(), campaign.getStatus(), campaign.getCreationDate(), campaign.getLastModificationDate(),
                campaign.getStartDate(), campaign.getEndDate(), campaign.getIdFile(), campaign.getValuta(), campaign.getBudget(),
                campaign.getTrackingCode(), campaign.getEncodedId(),
                campaign.getCookie().getId(), campaign.getCookie().getName(), campaign.getCookie().getValue(),
                campaign.getAdvertiser().getId(), campaign.getAdvertiser().getName(),
                campaign.getDealer().getId(), campaign.getDealer().getName(),
                campaign.getPlanner().getId(), campaign.getPlanner().getName(),
                medias, affiliates, commissions, categoryDTOS, revenues, accc, categoryList);
    }

}
