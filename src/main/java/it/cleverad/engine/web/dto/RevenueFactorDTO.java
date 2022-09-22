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
    private String idType;
    private Long revenue;
    private LocalDate dueDate;

    private boolean status;
    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    public RevenueFactorDTO(Long id, String idType, Long revenue, LocalDate dueDate, boolean status, LocalDateTime creationDate, LocalDateTime lastModificationDate) {
        this.id = id;
        this.idType = idType;
        this.revenue = revenue;
        this.dueDate = dueDate;
        this.status = status;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
    }

    public static RevenueFactorDTO from(RevenueFactor revenueFactor) {
        return new RevenueFactorDTO(revenueFactor.getId(), revenueFactor.getIdType(), revenueFactor.getRevenue(), revenueFactor.getDueDate(), revenueFactor.isStatus(), revenueFactor.getCreationDate(), revenueFactor.getLastModificationDate());
    }

}
