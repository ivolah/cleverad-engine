package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.Commission;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CommissionDTO {

    private long id;
    private String name;
    private String value;
    private String description;
    private Boolean status;
    private String idType;
    private LocalDate dueDate;
    private Long campaignId;

    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    public CommissionDTO(long id, String name, String value, String description, Boolean status, LocalDateTime creationDate, LocalDateTime lastModificationDate) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.description = description;
        this.status = status;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
    }

    public static CommissionDTO from(Commission comission) {
        return new CommissionDTO(comission.getId(), comission.getName(), comission.getValue(), comission.getDescription(), comission.getStatus(), comission.getCreationDate(), comission.getLastModificationDate());
    }

}
