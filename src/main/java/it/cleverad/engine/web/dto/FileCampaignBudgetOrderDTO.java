package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.FileCampaignBudgetOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileCampaignBudgetOrderDTO {

    private Long id;
    private String name;
    private LocalDateTime creationDate;
    private String path;
    private String type;

    public static FileCampaignBudgetOrderDTO from(FileCampaignBudgetOrder file) {
        return new FileCampaignBudgetOrderDTO(file.getId(), file.getName(), file.getCreationDate(), file.getPath(), file.getType());
    }

}