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
    private Long dictionaryId;
    private LocalDate dueDate;
    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    private Long campaignId;
    private String typeName;

    public CommissionDTO(long id, String name, String value, String description, Boolean status, Long dictionaryId, LocalDate dueDate, LocalDateTime creationDate, LocalDateTime lastModificationDate, Long campaignId, String typeName) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.description = description;
        this.status = status;
        this.dictionaryId = dictionaryId;
        this.dueDate = dueDate;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
        this.campaignId = campaignId;
        this.typeName = typeName;
    }

    public static CommissionDTO from(Commission comission) {
        return new CommissionDTO(comission.getId(), comission.getName(), comission.getValue(), comission.getDescription(),
                comission.getStatus(),
                comission.getDictionary() != null ? comission.getDictionary().getId() : null,
                comission.getDueDate(), comission.getCreationDate(),
                comission.getLastModificationDate(),
                comission.getCampaign() != null ? comission.getCampaign().getId() : null,
                comission.getDictionary().getName());
    }
}
