package it.cleverad.engine.persistence.model.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_transaction_cpl")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class TransactionCPL {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_time")
    private LocalDateTime dateTime;
    private Double value;
    @Column(name = "initial_value")
    private Double initialValue;
    private Boolean approved;

    private String refferal;

    private String ip;
    private String agent;
    private String data;
    @Column(name = "payout_reference")
    private String payoutReference;
    private String note;
    @Column(name = "lead_number")
    private Long leadNumber;
    @Column(name = "payout_present")
    private Boolean payoutPresent;
    @Column(name = "creation_date")
    private LocalDateTime creationDate = LocalDateTime.now();
    @Column(name = "last_modification_date")
    private LocalDateTime lastModificationDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    @ManyToOne
    @JoinColumn(name = "advertiser_id")
    private Advertiser advertiser;

    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

    @ManyToOne
    @JoinColumn(name = "affiliate_id")
    private Affiliate affiliate;

    @ManyToOne
    @JoinColumn(name = "commission_id")
    private Commission commission;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    private Channel channel;

    @ManyToOne
    @JoinColumn(name = "media_id")
    private Media media;

    @ManyToOne
    @JoinColumn(name = "payout_id")
    private Payout payout;

    @ManyToOne
    @JoinColumn(name = "dictionary_id")
    private Dictionary dictionary;

    @Column(name = "revenue_id")
    private Long revenueId;

    @ManyToOne
    @JoinColumn(name = "status_id")
    private Dictionary dictionaryStatus;

    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "phone_verified")
    private Boolean phoneVerified = false;

}