package it.cleverad.engine.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TopCampagneDTO {

    private Long id;

    private Long campaignId;
    private String campaignName;
    private Long dictionaryId;
    private String dictionaryName;

    private Double impressionNumber;
    private Double clickNumber;
    private Double leadNumber;

    private Double ctr;
    private Double lr;

    private Double commssionNumber;
    private Double revenueNumber;
    private Double marginNumber;
    private Double marginPercentage;
    private Double eCPL;
    private Double eCPM;
    private Double eCPC;
    private Double eCPS;

    private LocalDateTime dateTime;


}
