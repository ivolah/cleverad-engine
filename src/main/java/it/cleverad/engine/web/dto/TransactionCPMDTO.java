package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.TransactionCPM;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCPMDTO {

    private Long id;

    private Long affiliateId;
    private String affiliateName;
    private Long campaignId;
    private String campaignName;
    private Long commissionId;
    private String commissionName;
    private Long channelId;
    private String channelName;
    private Long mediaId;
    private String mediaName;

    private LocalDateTime dateTime;
    private Double value;
    private Boolean approved;

    private String ip;
    private String agent;

    private String payoutReference;
    private String note;

    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    private Long dictionaryId;
    private String dictionaryName;

    private Long impressionNumber;
    private Long revenueId;
    private Long walletId;
    private Boolean payoutPresent;

    private Long statusId;
    private String data;

    public static TransactionCPMDTO from(TransactionCPM transaction) {
        return new TransactionCPMDTO(transaction.getId(),
                transaction.getAffiliate() != null ? transaction.getAffiliate().getId() : null,
                transaction.getAffiliate() != null ? transaction.getAffiliate().getName() : null,

                transaction.getCampaign() != null ? transaction.getCampaign().getId() : null,
                transaction.getCampaign() != null ? transaction.getCampaign().getName() : null,

                transaction.getCommission() != null ? transaction.getCommission().getId() : null,
                transaction.getCommission() != null ? transaction.getCommission().getName() : null,

                transaction.getChannel() != null ? transaction.getChannel().getId() : null,
                transaction.getChannel() != null ? transaction.getChannel().getName() : null,

                transaction.getMedia() != null ? transaction.getMedia().getId() : null,
                transaction.getMedia() != null ? transaction.getMedia().getName() : null,

                transaction.getDateTime(),
                transaction.getValue(),
                transaction.getApproved(),
                transaction.getIp(),
                transaction.getAgent(), transaction.getPayoutReference(), transaction.getNote(),
                transaction.getCreationDate(), transaction.getLastModificationDate(),

                transaction.getDictionary().getId(), transaction.getDictionary().getName(),
                transaction.getImpressionNumber(),
                transaction.getRevenueId(),
                transaction.getWallet() != null ? transaction.getWallet().getId() : null,
                transaction.getPayoutPresent(),
                transaction.getDictionaryStatus() != null ? transaction.getDictionaryStatus().getId() : null,
                transaction.getNote());


    }

}