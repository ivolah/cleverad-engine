package it.cleverad.engine.persistence.model.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "v_cpc_click_campaign_media_day")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class StatCpcClickCampaignMediaDay {

    @Id
    private Long id;

    private Long click;
    @Column(name = "campaign_id")
    private Long campaignId;
    private String campaign;
    @Column(name = "media_id")
    private Long mediaId;
    private String media;
    @Column(name = "file_id")
    private Long fileId;
    private Double year;
    private Double month;
    private Double day;
    private Double week;


}
