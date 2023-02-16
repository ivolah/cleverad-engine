package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.ChannelCategory;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ChannelCategoryDTO {

    private Long id;
    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;
    private Long categoryId;
    private String categoryName;
    private Long channelId;
    private String channelName;

    public ChannelCategoryDTO(Long id, LocalDateTime creationDate, LocalDateTime lastModificationDate, Long categoryId, String categoryName, Long channelId, String channelName) {
        this.id = id;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.channelId = channelId;
        this.channelName = channelName;
    }

    public static ChannelCategoryDTO from(ChannelCategory comission) {
        return new ChannelCategoryDTO(comission.getId(), comission.getCreationDate(), comission.getLastModificationDate(), comission.getCategory().getId(), comission.getCategory().getName(), comission.getChannel().getId(), comission.getChannel().getName()
        );
    }

}
