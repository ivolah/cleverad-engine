package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.RevenueFactor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class RevenueFactorDTO {

    private Long id;
    private Double revenue;
    private LocalDate startDate;
    private LocalDate dueDate;
    private Long campaignId;
    private String campaignName;
    private Long dictionaryId;
    private String dictionaryName;
    private Boolean status;
    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;
    private String action;
    private Double sale;

    public RevenueFactorDTO(Long id, Double revenue, LocalDate startDate, LocalDate dueDate, Long campaignId, String campaignName, Long dictionaryId, String divtionaryName, Boolean status, LocalDateTime creationDate, LocalDateTime lastModificationDate, String action, Double sale) {
        this.id = id;
        this.revenue = revenue;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.campaignId = campaignId;
        this.campaignName = campaignName;
        this.dictionaryId = dictionaryId;
        this.dictionaryName = divtionaryName;
        this.status = status;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
        this.action = action;
        this.sale = sale;
    }

    public static RevenueFactorDTO from(RevenueFactor revenueFactor) {
        return new RevenueFactorDTO(revenueFactor.getId(),
                revenueFactor.getRevenue(),
                revenueFactor.getStartDate(),
                revenueFactor.getDueDate(),
                revenueFactor.getCampaign() != null ? revenueFactor.getCampaign().getId() : null,
                revenueFactor.getCampaign() != null ? revenueFactor.getCampaign().getName() : null,
                revenueFactor.getDictionary() != null ? revenueFactor.getDictionary().getId() : null,
                revenueFactor.getDictionary() != null ? revenueFactor.getDictionary().getName() : null,
                revenueFactor.getStatus(), revenueFactor.getCreationDate(), revenueFactor.getLastModificationDate(), revenueFactor.getAction(),
                revenueFactor.getSale()
                );

    }

}