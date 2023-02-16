package it.cleverad.engine.persistence.model.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "v_cpc_transaction_campaign_media")
@Getter
@Setter
@NoArgsConstructor
public class StatCpcTransactionCampaign {

    @Id
    private Long id;

    private Long totale;
    private Long campaignId;
    private String campaign;
    private Long mediaId;
    private String media;
    private Long fileId;

}
