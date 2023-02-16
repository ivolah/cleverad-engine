package it.cleverad.engine.persistence.model.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "v_cpc_click_campaign_media")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class StatCpcClickCampaign {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    private Long  click;
    private Long campaignId;
    private String campaign;
    private Long mediaId;
    private String media;
    private Long fileId;

}
