package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.RevenueFactor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class RevenueFactorDTO {

    private Long id;
    private Long revenue;
    private LocalDate dueDate;
    private Long campaignId;
    private String  campaignName;
    private Long dictionaryId;
    private String dictionaryName;
    private Boolean status;
    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    public RevenueFactorDTO(Long id, Long revenue, LocalDate dueDate, Long campaignId, String campaignName, Long dictionaryId, String divtionaryName, Boolean status, LocalDateTime creationDate, LocalDateTime lastModificationDate) {
        this.id = id;
        this.revenue = revenue;
        this.dueDate = dueDate;
        this.campaignId = campaignId;
        this.campaignName = campaignName;
        this.dictionaryId = dictionaryId;
        this.dictionaryName = divtionaryName;
        this.status = status;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
    }

    public static RevenueFactorDTO from(RevenueFactor revenueFactor) {
        return new RevenueFactorDTO(revenueFactor.getId(),
                revenueFactor.getRevenue(),
                revenueFactor.getDueDate(),
                revenueFactor.getCampaign() != null ? revenueFactor.getCampaign().getId() : null,
                revenueFactor.getCampaign() != null ? revenueFactor.getCampaign().getName() : null,
                revenueFactor.getDictionary() != null ? revenueFactor.getDictionary().getId() : null,
                revenueFactor.getDictionary() != null ? revenueFactor.getDictionary().getName() : null,
                revenueFactor.getStatus(), revenueFactor.getCreationDate(), revenueFactor.getLastModificationDate());
    }

}
