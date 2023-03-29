package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.TransactionAll;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    private Long dictionaryId;
    private String dictionaryName;

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
                transaction.getAffiliateName(),
                transaction.getCampaignId(),
                transaction.getCampaignName(),
                transaction.getChannelId(),
                transaction.getChannelName(),
                transaction.getCommissionId(),
                transaction.getCommissionName(),
                transaction.getPayoutId(),
                transaction.getWalletId(),
                transaction.getMediaId(),
                transaction.getMediaName(),
                transaction.getClickNumber(),
                transaction.getRefferal(),
                transaction.getCompanyId(),
                transaction.getAdvertiserId(),
                transaction.getData(),
                transaction.getTipo(),
                transaction.getDictionaryId(), transaction.getDictionaryName()
        );
    }

}
