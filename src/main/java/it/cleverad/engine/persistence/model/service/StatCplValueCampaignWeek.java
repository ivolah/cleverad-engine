package it.cleverad.engine.persistence.model.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "v_cpl_value_campaign_media_week")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class StatCplValueCampaignWeek {

    @Id
    private Long id;

    private Double value;
    private Long campaignId;
    private String campaign;
    private Long mediaId;
    private String media;
    private Long fileId;
    private Double year;
    private Double week;
    
}
