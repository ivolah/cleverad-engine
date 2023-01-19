package it.cleverad.engine.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasicAffiliateDTO {

    private long id;
    private String name;
    private String followThrough;

}
