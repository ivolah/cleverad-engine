package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.CampaignCost;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CampaignCostDTO {

    private Long id;

    private Long campaignId;
    private String campaignName;

    private String nome;
    private Integer numero;
    private Double costo;
    private String note;

    private LocalDateTime creationDate;
    private LocalDate startDate;
    private LocalDate endDate;

    private Boolean status;

    public static CampaignCostDTO from(CampaignCost campaignCost) {
        return new CampaignCostDTO(
                campaignCost.getId(),
                campaignCost.getCampaign().getId(), campaignCost.getCampaign().getName(),
                campaignCost.getNome(),
                campaignCost.getNumero(), campaignCost.getCosto(),
                campaignCost.getNote(),
                campaignCost.getCreationDate(), campaignCost.getStartDate(), campaignCost.getEndDate(),
                campaignCost.getStatus()
        );
    }

}