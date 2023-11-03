package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.Campaign;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class CampaignBrandBuddiesDTO {

    private long id;
    private String name;
    private String shortDescription;
    private String longDescription;
    private String note;
    private Boolean status;
    private LocalDate startDate;
    private LocalDate endDate;
    private String idFile;
    private String encodedId;
    private List<CommissionDTO> commissions;

    public static CampaignBrandBuddiesDTO from(Campaign campaign) {

        List<CommissionDTO> commissions = null;
        if (campaign.getCommissionCampaigns() != null) {
            commissions = campaign.getCommissionCampaigns().stream().filter(commission -> commission.getDictionary().getId() == 84L || commission.getDictionary().getId() == 85L).map(commissionCampaign -> {
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
                dto.setDictionaryId(commissionCampaign.getDictionary().getId());
                dto.setTypeName(commissionCampaign.getDictionary().getName());
                dto.setBase(commissionCampaign.getBase());
                return dto;
            }).collect(Collectors.toList());
        }

        return new CampaignBrandBuddiesDTO(
                campaign.getId(),
                campaign.getName(),
                campaign.getShortDescription(),
                campaign.getLongDescription(),
                campaign.getNote(), campaign.getStatus(),
                campaign.getStartDate(), campaign.getEndDate(),
                campaign.getIdFile(),
                campaign.getEncodedId(),
                commissions
        );
    }

}