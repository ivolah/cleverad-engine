package it.cleverad.engine.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "v_cpc_click_campaign_media_week")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class StatCpcClickCampaignWeek {

    @Id
    private Long id;

    private Long click;
    private Long campaignId;
    private String campaign;
    private Long mediaId;
    private String media;
    private Long fileId;
    private Double year;
    private Double week;

}
