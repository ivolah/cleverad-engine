package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.CampaignCategory;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class CampaignCategoryDTO {

    private Long id;
    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;
    private Long categoryId;
    private String categoryName;
    private Long campaignId;
    private String campaignName;

    public CampaignCategoryDTO(Long id, LocalDateTime creationDate, LocalDateTime lastModificationDate, Long categoryId, String categoryName, Long campaignId, String campaignName) {
        this.id = id;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.campaignId = campaignId;
        this.campaignName = campaignName;
    }

    public static CampaignCategoryDTO from(CampaignCategory campaignCategory) {
        return new CampaignCategoryDTO(campaignCategory.getId(), campaignCategory.getCreationDate(), campaignCategory.getLastModificationDate(), campaignCategory.getCategory().getId(), campaignCategory.getCategory().getName(), campaignCategory.getCampaign().getId(), campaignCategory.getCampaign().getName()
        );
    }

}
