package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.Campaign;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignDTO {

    private long id;
    private String name;
    private String shortDescription;
    private String longDescription;
    private String note;
    private Boolean status;
    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private String idFile;
    private String valuta;
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
    private String plannerMail;
    private List<BasicMediaDTO> medias;
    private List<BasicAffiliateDTO> affiliates;
    private List<CommissionDTO> commissions;
    private List<BasicCategoryDTO> categories;
    private List<RevenueFactorDTO> revenues;
    private List<AffiliateChannelCommissionCampaignDTO> affiliateChannelCommissionCampaigns;
    private List<Long> categoryList;
    private Boolean checkPhoneNumber;
    private Boolean suspended;

    public static CampaignDTO from(Campaign campaign) {

        List<BasicMediaDTO> medias = null;
        if (campaign.getMedias() != null) {
            medias = campaign.getMedias().stream().map(media -> {
                BasicMediaDTO mediaDTO = new BasicMediaDTO();
                mediaDTO.setId(media.getId());
                mediaDTO.setName(media.getName());
                mediaDTO.setNote(media.getNote());
                mediaDTO.setUrl(media.getUrl());
                if (media.getMediaType() != null && media.getMediaType().getId() != null)
                    mediaDTO.setTypeId(String.valueOf(media.getMediaType().getId()));
                return mediaDTO;
            }).collect(Collectors.toList());
        }

        List<BasicAffiliateDTO> affiliateDTOList = null;
        if (campaign.getCampaignAffiliates() != null) {
            affiliateDTOList = campaign.getCampaignAffiliates().stream().map(affiliateCampaign -> {
                BasicAffiliateDTO dto = new BasicAffiliateDTO();

                dto.setId(affiliateCampaign.getAffiliate() != null ? affiliateCampaign.getAffiliate().getId() : 0);
                dto.setName(affiliateCampaign.getAffiliate() != null ? affiliateCampaign.getAffiliate().getName() : null);
                dto.setFollowThrough(affiliateCampaign.getFollowThrough());
                return dto;
            }).collect(Collectors.toList());
        }

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

        List<Long> categoryList = categoryDTOS.stream()
                .map(BasicCategoryDTO::getId)
                .collect(Collectors.toList());

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
                return dto;
            }).collect(Collectors.toList());
        }

        return new CampaignDTO(campaign.getId(), campaign.getName(), campaign.getShortDescription(),
                campaign.getLongDescription(), campaign.getNote(), campaign.getStatus(),
                campaign.getCreationDate(), campaign.getLastModificationDate(),
                campaign.getStartDate(), campaign.getEndDate(), campaign.getIdFile(),
                campaign.getValuta(),
                campaign.getEncodedId(),
                campaign.getCookie().getId(),
                campaign.getCookie().getName(),
                campaign.getCookie().getValue(),
                campaign.getAdvertiser().getId(), campaign.getAdvertiser().getName(),
                campaign.getDealer() != null ? campaign.getDealer().getId() : null,
                campaign.getDealer() != null ? campaign.getDealer().getName() : null,
                campaign.getPlanner() != null ? campaign.getPlanner().getId() : null,
                campaign.getPlanner() != null ? campaign.getPlanner().getName() : null,
                campaign.getPlanner() != null ? campaign.getPlanner().getEmail() : null,
                medias, affiliateDTOList, commissions, categoryDTOS, revenues, accc, categoryList,
                campaign.getCheckPhoneNumber(),
                campaign.getSuspended());
    }

    public static CampaignDTO fromList(Campaign campaign) {

        return new CampaignDTO(campaign.getId(), campaign.getName(), campaign.getShortDescription(),
                campaign.getLongDescription(), campaign.getNote(), campaign.getStatus(),
                campaign.getCreationDate(), campaign.getLastModificationDate(),
                campaign.getStartDate(), campaign.getEndDate(), campaign.getIdFile(),
                campaign.getValuta(),
                campaign.getEncodedId(),
                campaign.getCookie().getId(),
                campaign.getCookie().getName(),
                campaign.getCookie().getValue(),
                campaign.getAdvertiser().getId(), campaign.getAdvertiser().getName(),
                campaign.getDealer() != null ? campaign.getDealer().getId() : null,
                campaign.getDealer() != null ? campaign.getDealer().getName() : null,
                campaign.getPlanner() != null ? campaign.getPlanner().getId() : null,
                campaign.getPlanner() != null ? campaign.getPlanner().getName() : null,
                campaign.getPlanner() != null ? campaign.getPlanner().getEmail() : null,
                null, null, null, null, null, null, null,
                campaign.getCheckPhoneNumber(),
                campaign.getSuspended()
        );
    }

}