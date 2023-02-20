package it.cleverad.engine.persistence.model.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "v_transactions_all")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class TransactionAll {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    private String agent;
    private Boolean approved;
    @Column(name = "creation_date")
    private LocalDateTime creationDate;
    @Column(name = "date_time")
    private LocalDateTime dateTime;
    private String ip;
    private String note;
    @Column(name = "payout_reference")
    private String payoutReference;
    private Double value;
    @Column(name = "affiliate_id")
    private Long affiliateId;
    @Column(name = "affiliate_name")
    private String affiliateName;
    @Column(name = "campaign_id")
    private Long campaignId;
    @Column(name = "campaign_name")
    private String campaignName;
    @Column(name = "channel_id")
    private Long channelId;
    @Column(name = "channel_name")
    private String channelName;
    @Column(name = "commission_id")
    private Long commissionId;
    @Column(name = "commission_name")
    private String commissionName;
    @Column(name = "payout_id")
    private Long payoutId;
    @Column(name = "wallet_id")
    private Long walletId;
    @Column(name = "media_id")
    private Long mediaId;
    @Column(name = "media_name")
    private String mediaName;
    @Column(name = "click_number")
    private Long clickNumber;
    private String refferal;
    @Column(name = "company_id")
    private Long companyId;
    @Column(name = "advertiser_id")
    private Long advertiserId;
    private String data;
    private String tipo;

}
