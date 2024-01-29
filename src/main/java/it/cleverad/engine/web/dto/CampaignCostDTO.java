package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.CampaignCost;
import it.cleverad.engine.persistence.model.service.FileCost;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    private Long typeId;
    private String typeName;

    private List<FileCostDTO> docs;

    public static CampaignCostDTO from(CampaignCost campaignCost) {

        List<FileCostDTO> docs = null;
        if (campaignCost.getFileCosts() != null) {
            docs = campaignCost.getFileCosts() .stream().map(doc -> {
                FileCostDTO dto = new FileCostDTO();
                dto.setId(doc.getId());
                dto.setName(doc.getName());
                dto.setPath(doc.getPath());
                dto.setType(doc.getType());
                return dto;
            }).collect(Collectors.toList());
        }

        return new CampaignCostDTO(
                campaignCost.getId(),
                campaignCost.getCampaign().getId(), campaignCost.getCampaign().getName(),
                campaignCost.getNome(),
                campaignCost.getNumero(), campaignCost.getCosto(),
                campaignCost.getNote(),
                campaignCost.getCreationDate(), campaignCost.getStartDate(), campaignCost.getEndDate(),
                campaignCost.getStatus(),
                campaignCost.getDictionary() != null ? campaignCost.getDictionary().getId() : null,
                campaignCost.getDictionary() != null ? campaignCost.getDictionary().getName() : null,
                docs
        );
    }

}