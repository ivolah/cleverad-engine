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
    private LocalDateTime creationDate;
    private LocalDateTime dateTime;
    private String ip;
    private String note;
    private String payoutReference;
    private Double value;
    private Long affiliateId;
    private String affiliateName;
    private Long campaignId;
    private String campaignName;
    private Long channelId;
    private String channelName;
    private Long commissionId;
    private String commissionName;
    private Long payoutId;
    private Long walletId;
    private Long mediaId;
    private String mediaName;
    private Long clickNumber;
    private String refferal;
    private Long companyId;
    private Long advertiserId;
    private String data;
    private String tipo;

}
