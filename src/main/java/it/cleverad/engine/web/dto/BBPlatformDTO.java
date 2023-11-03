package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.BBPlatform;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class BBPlatformDTO {

    private Long id;
    private String name;
    private String dimension;
    private Boolean verified;
    private Long brandbuddiesId;
    private String brandbuddiesName;
    private Long platformId;
    private String platformName;

    public static BBPlatformDTO from(BBPlatform platform) {
        return new BBPlatformDTO(platform.getId(), platform.getName(), platform.getDimension(), platform.getVerified(),
                platform.getAffiliate() != null ? platform.getAffiliate().getId() : null,
                platform.getAffiliate() != null ? platform.getAffiliate().getName() + " " + platform.getAffiliate().getLastName() : null,
                platform.getDictionary() != null ? platform.getDictionary().getId() : null,
                platform.getDictionary() != null ? platform.getDictionary().getName() : null
        );
    }

}