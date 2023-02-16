package it.cleverad.engine.persistence.model.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "v_cpl_value_campaign_media")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class StatCplValueCampaign {

    @Id
    private Long id;

    private Double value;
    private Long campaignId;
    private String campaign;
    private Long mediaId;
    private String media;
    private Long fileId;

}
