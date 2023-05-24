package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChannelDTO {

    private Long id;
    private String name;
    private String shortDescription;
    private String dimension;
    private String country;
    private Long ownerId;
    private String ownerName;
    private Boolean status;
    private String url;
    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    private Long dictionaryId;
    private String dictionaryValue;

    private Long typeId;
    private String typeValue;

    private Long businessTypeId;
    private String businessTypeValue;

    private List<Long> categoryList;
    private List<CategoryDTO> categories;
    private String campaignID;

    private String affiliateName;
    private Long affiliateId;

    public ChannelDTO(Long id, String name, String shortDescription, String dimension, String country, Long ownerId, String ownerName, Boolean status, String url, LocalDateTime creationDate, LocalDateTime lastModificationDate, Long dictionaryId, String dictionaryValue, Long typeId, String typeValue, Long businessTypeId, String businessTypeValue, List<Long> categoryList, List<CategoryDTO> categories, String affiliateName, Long affiliateId) {
        this.id = id;
        this.name = name;
        this.shortDescription = shortDescription;
        this.dimension = dimension;
        this.country = country;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.status = status;
        this.url = url;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
        this.dictionaryId = dictionaryId;
        this.dictionaryValue = dictionaryValue;
        this.businessTypeId = businessTypeId;
        this.businessTypeValue = businessTypeValue;
        this.typeId = typeId;
        this.typeValue = typeValue;
        this.categoryList = categoryList;
        this.categories = categories;
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

        List<Long> categoryList = new ArrayList<>();
        //        String catergoryList = "";
        if (categories != null && categories.size() > 0)
            for (CategoryDTO basicCategoryDTO : Objects.requireNonNull(categories)) {
                categoryList.add(basicCategoryDTO.getId());
            }
        return new ChannelDTO(channel.getId(), channel.getName(), channel.getShortDescription(), channel.getDimension(),
                channel.getCountry(),

                channel.getDictionaryOwner() != null ? channel.getDictionaryOwner().getId() : null,
                channel.getDictionaryOwner() != null ? channel.getDictionaryOwner().getName() : null,

                channel.getStatus(),
                channel.getUrl(), channel.getCreationDate(), channel.getLastModificationDate(),
                channel.getDictionary() != null ? channel.getDictionary().getId() : null,
                channel.getDictionary() != null ? channel.getDictionary().getName() : null,

                channel.getDictionaryType() != null ? channel.getDictionaryType().getId() : null,
                channel.getDictionaryType() != null ? channel.getDictionaryType().getName() : null,

                channel.getDictionaryBusinessType() != null ? channel.getDictionaryBusinessType().getId() : null,
                channel.getDictionaryBusinessType() != null ? channel.getDictionaryBusinessType().getName() : null,

                categoryList,
                categories,
                channel.getAffiliate() != null ? channel.getAffiliate().getName() : null,
                channel.getAffiliate() != null ? channel.getAffiliate().getId() : null
        );
    }

}
