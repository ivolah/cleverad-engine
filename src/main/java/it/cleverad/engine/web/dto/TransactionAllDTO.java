package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.TransactionAll;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TransactionAllDTO {

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
    private Long campaignId;
    private Long channelId;
    private Long commissionId;
    private Long payoutId;
    private Long walletId;
    private Long mediaId;
    private Long clickNumber;
    private String refferal;
    private Long companyId;
    private Long advertiserId;
    private String data;
    private String tipo;

    public TransactionAllDTO(Long id, String agent, Boolean approved, LocalDateTime creationDate, LocalDateTime dateTime, String ip, String note, String payoutReference, Double value, Long affiliateId, Long campaignId, Long channelId, Long commissionId, Long payoutId, Long walletId, Long mediaId, Long clickNumber, String refferal, Long companyId, Long advertiserId, String data, String tipo) {
        this.id = id;
        this.agent = agent;
        this.approved = approved;
        this.creationDate = creationDate;
        this.dateTime = dateTime;
        this.ip = ip;
        this.note = note;
        this.payoutReference = payoutReference;
        this.value = value;
        this.affiliateId = affiliateId;
        this.campaignId = campaignId;
        this.channelId = channelId;
        this.commissionId = commissionId;
        this.payoutId = payoutId;
        this.walletId = walletId;
        this.mediaId = mediaId;
        this.clickNumber = clickNumber;
        this.refferal = refferal;
        this.companyId = companyId;
        this.advertiserId = advertiserId;
        this.data = data;
        this.tipo = tipo;
    }

    public static TransactionAllDTO from(TransactionAll transaction) {
        return new TransactionAllDTO(
                transaction.getId(),
                transaction.getAgent(),
                transaction.getApproved(),
                transaction.getCreationDate(),
                transaction.getDateTime(),
                transaction.getIp(),
                transaction.getNote(),
                transaction.getPayoutReference(),
                transaction.getValue(),
                transaction.getAffiliateId(),
                transaction.getCampaignId(),
                transaction.getChannelId(),
                transaction.getCommissionId(),
                transaction.getPayoutId(),
                transaction.getWalletId(),
                transaction.getMediaId(),
                transaction.getClickNumber(),
                transaction.getRefferal(),
                transaction.getCompanyId(),
                transaction.getAdvertiserId(),
                transaction.getData(),
                transaction.getTipo()
        );
    }

}
