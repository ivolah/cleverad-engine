package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.FileCampaignBudgetInvoice;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileCampaignBudgetInvoiceDTO {

    private Long id;
    private String name;
    private LocalDateTime creationDate;
    private String path;
    private String type;

    public static FileCampaignBudgetInvoiceDTO from(FileCampaignBudgetInvoice file) {
        return new FileCampaignBudgetInvoiceDTO(file.getId(), file.getName(), file.getCreationDate(), file.getPath(), file.getType());
    }

}