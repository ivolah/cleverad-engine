package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.Channel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class ChannelDTO {

    private Long id;
    private String name;
    private String shortDescription;
    private String type;
    private Boolean status;
    private String url;
    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    private Long dictionaryId;
    private String dictionaryValue;

    private List<CategoryDTO> categoryDTOS;
    private String campaignID;

    private String affiliateName;
    private Long affiliateId;

    public ChannelDTO(Long id, String name, String shortDescription, String type, Boolean status, String url, LocalDateTime creationDate, LocalDateTime lastModificationDate, Long dictionaryId, String dictionaryValue, List<CategoryDTO> categoryDTOS, String affiliateName, Long affiliateId) {
        this.id = id;
        this.name = name;
        this.shortDescription = shortDescription;
        this.type = type;
        this.status = status;
        this.url = url;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
        this.dictionaryId = dictionaryId;
        this.dictionaryValue = dictionaryValue;
        this.categoryDTOS = categoryDTOS;
        this.affiliateName = affiliateName;
        this.affiliateId = affiliateId;
    }

    public static ChannelDTO from(Channel channel) {

        List<CategoryDTO> categories = null;
        if (channel.getChannelCategories() != null) {
            categories = channel.getChannelCategories().stream().map(channelCategory -> {
                CategoryDTO dto = new CategoryDTO();
                dto.setId(channelCategory.getCategory().getId());
                dto.setName(channelCategory.getCategory().getName());
                dto.setCode(channelCategory.getCategory().getCode());
                dto.setDescription(channelCategory.getCategory().getDescription());
                return dto;
            }).collect(Collectors.toList());
        }


        return new ChannelDTO(channel.getId(), channel.getName(), channel.getShortDescription(), channel.getType(), channel.getStatus(), channel.getUrl(), channel.getCreationDate(), channel.getLastModificationDate(), channel.getDictionary() != null ? channel.getDictionary().getId() : null, channel.getDictionary() != null ? channel.getDictionary().getName() : null, categories, channel.getAffiliate() != null ? channel.getAffiliate().getName() : null, channel.getAffiliate() != null ? channel.getAffiliate().getId() : null);
    }

}
