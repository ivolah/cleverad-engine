package it.cleverad.engine.persistence.model.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "v_transactions_status")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ViewTransactionStatus {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    private String tipo;
    @Column(name = "creation_date")
    private LocalDateTime creationDate;
    @Column(name = "date_time")
    private LocalDate dateTime;
    @Column(name = "status_id")
    private Long statusId;
    @Column(name = "status_name")
    private String statusName;
    @Column(name = "dictionary_id")
    private Long dictionaryId;
    @Column(name = "dictionary_name")
    private String dictionaryName;
    @Column(name = "affiliate_id")
    private Long affiliateId;
    @Column(name = "affiliate_name")
    private String affiliateName;
    @Column(name = "channel_id")
    private Long channelId;
    @Column(name = "channel_name")
    private String channelName;
    @Column(name = "campaign_id")
    private Long campaignId;
    @Column(name = "campaign_name")
    private String campaignName;
    @Column(name = "media_id")
    private Long mediaId;
    @Column(name = "media_name")
    private String mediaName;
    @Column(name = "commission_id")
    private Long commissionId;
    @Column(name = "commission_name")
    private String commissionName;
    @Column(name = "commission_value")
    private Double commissionValue;
    @Column(name = "commission_value_rigettato")
    private Double commissionValueRigettato;
    private Double value;
    @Column(name = "value_rigettato")
    private Double valueRigettato;
    @Column(name = "revenue_id")
    private Long revenueId;
    @Column(name = "revenue")
    private Long revenue;
    @Column(name = "revenue_rigettato")
    private Long revenueRigettato;
    @Column(name = "click_number")
    private Long clickNumber;
    @Column(name = "click_number_rigettato")
    private Long clickNumberRigettato;
    @Column(name = "impression_number")
    private Long impressionNumber;
    @Column(name = "lead_number")
    private Long leadNumber;
    private String data;
    @Column(name = "wallet_id")
    private Long walletId;
    @Column(name = "payout_present")
    private Boolean payoutPresent;
    @Nullable
    @Column(name = "payout_id")
    private Long payoutId;
    @Column(name = "payout_reference")
    private String payoutReference;

}