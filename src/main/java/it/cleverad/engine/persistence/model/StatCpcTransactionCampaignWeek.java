package it.cleverad.engine.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "v_cpc_transaction_campaign_media_week")
@Getter
@Setter
@NoArgsConstructor
public class StatCpcTransactionCampaignWeek {

    @Id
    private Long id;

    private Long totale;
    private Long campaignId;
    private String campaign;
    private Long mediaId;
    private String media;
    private Long fileId;
    private Double year;
    private Double week;

}
