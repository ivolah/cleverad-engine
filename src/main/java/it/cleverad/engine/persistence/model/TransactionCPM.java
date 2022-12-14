package it.cleverad.engine.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_transaction_cpm")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class TransactionCPM {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateTime;
    private String type;
    private Double value;
    private Boolean approved;

    private Long imageId;

    private String ip;
    private String agent;
    private String data;
    private String payoutReference;
    private String note;

    private LocalDateTime creationDate = LocalDateTime.now();
    private LocalDateTime lastModificationDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "wallet_id")
    private Wallet wallet;

    @ManyToOne
    @JoinColumn(name = "affiliate_id")
    private Affiliate affiliate;

    @ManyToOne
    @JoinColumn(name = "campaign_id")
    private Campaign campaign;

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

}
