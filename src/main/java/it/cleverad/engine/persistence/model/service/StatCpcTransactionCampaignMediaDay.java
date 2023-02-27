package it.cleverad.engine.persistence.model.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "v_cpc_transaction_campaign_media_week")
@Getter
@Setter
@NoArgsConstructor
public class StatCpcTransactionCampaignMediaDay {

    @Id
    private Long id;

    private Long totale;
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
    private Double week;
    private Double day;

}
