package it.cleverad.engine.persistence.model.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "v_cpc_value_campaign_media")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class StatCpcValueCampaign {

    @Id
    private Long id;

    private Double value;
    @Column(name = "campaign_id")
    private Long campaignId;
    private String campaign;
    @Column(name = "media_id")
    private Long mediaId;
    private String media;
    @Column(name = "file_id")
    private Long fileId;

}
