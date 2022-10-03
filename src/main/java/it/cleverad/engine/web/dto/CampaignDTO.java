package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.Campaign;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
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

    private List<BasicMediaDTO> medias;
    private List<BasicAffiliateDTO> affiliates;
    private List<CommissionDTO> commissions;
    private List<BasicCategoryDTO> categories;
    private List<BasicCookieDTO> cookies;
    private List<RevenueFactorDTO> revenues;
    private List<AffiliateChannelCommissionCampaignDTO> affiliateChannelCommissionCampaigns;

    public CampaignDTO(long id, String name, String shortDescription, String longDescription, Boolean status, LocalDateTime creationDate, LocalDateTime lastModificationDate, LocalDate startDate, LocalDate endDate, String idFile, String valuta, Long budget, List<BasicMediaDTO> medias, List<BasicAffiliateDTO> affiliates, List<CommissionDTO> commissions, List<BasicCategoryDTO> categories, List<BasicCookieDTO> cookies, List<RevenueFactorDTO> revenues, List<AffiliateChannelCommissionCampaignDTO> affiliateChannelCommissionCampaigns) {
        this.id = id;
        this.name = name;
        this.shortDescription = shortDescription;
        this.longDescription = longDescription;
        this.status = status;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.idFile = idFile;
        this.valuta = valuta;
        this.budget = budget;
        this.medias = medias;
        this.affiliates = affiliates;
        this.commissions = commissions;
        this.categories = categories;
        this.cookies = cookies;
        this.revenues = revenues;
        this.affiliateChannelCommissionCampaigns = affiliateChannelCommissionCampaigns;
    }

    public static CampaignDTO from(Campaign campaign) {

        List<BasicMediaDTO> medias = null;
        if (campaign.getMediaCampaignList() != null) {
            medias = campaign.getMediaCampaignList().stream().map(mediaCampaign -> {
                BasicMediaDTO mediaDTO = new BasicMediaDTO();
                mediaDTO.setId(mediaCampaign.getMedia().getId());
                mediaDTO.setName(mediaCampaign.getMedia().getName());
                mediaDTO.setNote(mediaCampaign.getMedia().getNote());
                mediaDTO.setTarget(mediaCampaign.getMedia().getTarget());
                mediaDTO.setUrl(mediaCampaign.getMedia().getUrl());
                mediaDTO.setTypeId(String.valueOf(mediaCampaign.getMedia().getTypeId()));

                return mediaDTO;
            }).collect(Collectors.toList());
        }

        List<BasicAffiliateDTO> affiliates = null;
        if (campaign.getAffiliateCampaigns() != null) {
            affiliates = campaign.getAffiliateCampaigns().stream().map(affiliateCampaign -> {
                BasicAffiliateDTO dto = new BasicAffiliateDTO();
                dto.setId(affiliateCampaign.getAffiliate().getId());
                dto.setName(affiliateCampaign.getAffiliate().getName());
                dto.setPrimaryMail(affiliateCampaign.getAffiliate().getPrimaryMail());
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

        List<BasicCategoryDTO> campaigns = null;
        if (campaign.getCampaignCategories() != null) {
            campaigns = campaign.getCampaignCategories().stream().map(campaignCategory -> {
                BasicCategoryDTO dto = new BasicCategoryDTO();
                dto.setId(campaignCategory.getCategory().getId());
                dto.setName(campaignCategory.getCategory().getName());
                dto.setCode(campaignCategory.getCategory().getCode());
                dto.setDescription(campaignCategory.getCategory().getDescription());
                return dto;
            }).collect(Collectors.toList());
        }

        List<BasicCookieDTO> cookie = null;
        if (campaign.getCampaignCookies() != null) {
            cookie = campaign.getCampaignCookies().stream().map(campaignCookie -> {
                BasicCookieDTO dto = new BasicCookieDTO();
                dto.setId(campaignCookie.getCookie().getId());
                dto.setName(campaignCookie.getCookie().getName());
                dto.setStatus(campaignCookie.getCookie().getStatus());
                dto.setValue(campaignCookie.getCookie().getValue());
                return dto;
            }).collect(Collectors.toList());
        }

        List<RevenueFactorDTO> revenues = null;
        if (campaign.getRevenueFactors() != null) {
            revenues = campaign.getRevenueFactors().stream().map(factor -> {
                RevenueFactorDTO dto = new RevenueFactorDTO();
                dto.setId(factor.getId());
                dto.setRevenue(factor.getRevenue());
                dto.setStatus(factor.isStatus());
                dto.setTypeName(factor.getDictionary().getName());
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

        return new CampaignDTO(campaign.getId(), campaign.getName(), campaign.getShortDescription(), campaign.getLongDescription(), campaign.getStatus(), campaign.getCreationDate(), campaign.getLastModificationDate(), campaign.getStartDate(), campaign.getEndDate(), campaign.getIdFile(), campaign.getValuta(), campaign.getBudget(), medias, affiliates, commissions, campaigns, cookie, revenues, accc);
    }
}
