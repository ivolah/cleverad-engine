package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.Campaign;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignBaseDTO {

    private long id;
    private String name;
    private String shortDescription;
    private String longDescription;
    private LocalDate startDate;
    private LocalDate endDate;
    private String idFile;
    private String valuta;

    private Long plannerId;
    private String plannerName;
    private String plannerMail;
    private String plannerSkype;

    private List<BasicMediaDTO> medias;
    private List<CommissionDTO> commissions;
    private List<AffiliateChannelCommissionCampaignDTO> affiliateChannelCommissionCampaigns;

    private Long affiliateId;

    public static CampaignBaseDTO from(Campaign campaign, Long affiliateId) {

        List<BasicMediaDTO> medias = null;
        if (campaign.getMedias() != null) {
            medias = campaign.getMedias().stream().map(media -> {
                BasicMediaDTO mediaDTO = new BasicMediaDTO();
                mediaDTO.setId(media.getId());
                mediaDTO.setName(media.getName());
                mediaDTO.setNote(media.getNote());
                //   mediaDTO.setTarget((List<Target>) media.getTargets());
                mediaDTO.setUrl(media.getUrl());
                if (media.getMediaType() != null && media.getMediaType().getId() != null)
                    mediaDTO.setTypeId(String.valueOf(media.getMediaType().getId()));
                return mediaDTO;
            }).collect(Collectors.toList());
        }

        List<AffiliateChannelCommissionCampaignDTO> accc = null;
        if (campaign.getAffiliateChannelCommissionCampaigns() != null) {
            accc = campaign.getAffiliateChannelCommissionCampaigns().stream()
                    .filter(affiliateChannelCommissionCampaign -> affiliateChannelCommissionCampaign.getAffiliate().getId().equals(affiliateId))
                    .map(acccc -> {
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

        List<Long> listaIDS = (List<Long>) accc.stream().mapToLong(value -> value.getCampaignId());
        List<CommissionDTO> commissions = null;
        if (campaign.getCommissionCampaigns() != null) {
            commissions = campaign.getCommissionCampaigns().stream()
                    .filter(commissionCampaign -> listaIDS.contains(commissionCampaign.getCampaign().getId()))
                    .map(commissionCampaign -> {
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

        return new CampaignBaseDTO(
                campaign.getId(),
                campaign.getName(),
                campaign.getShortDescription(),
                campaign.getLongDescription(),
                campaign.getStartDate(),
                campaign.getEndDate(),
                campaign.getIdFile(),
                campaign.getValuta(),
                campaign.getPlanner() != null ? campaign.getPlanner().getId() : null,
                campaign.getPlanner() != null ? campaign.getPlanner().getName() : null,
                campaign.getPlanner() != null ? campaign.getPlanner().getEmail() : null,
                campaign.getPlanner() != null ? campaign.getPlanner().getSkype() : null,
                medias, commissions, accc, affiliateId);
    }

}