package it.cleverad.engine.web.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BasicCampaignDTO {

    private long id;
    private String name;
    private String shortDescription;
    private String longDescription;
    private Boolean status;


}
