package it.cleverad.engine.config.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Refferal {

    private String refferal;
    private Long mediaId;
    private Long campaignId;
    private Long affiliateId;
    private Long channelId;
    private Long targetId;
    private Boolean success;

}