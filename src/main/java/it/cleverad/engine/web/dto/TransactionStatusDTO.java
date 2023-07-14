package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.ViewTransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionStatusDTO {

    private Long id;
    private String tipo;

    private LocalDateTime creationDate;
    private LocalDate dateTime;

    private Long statusId;
    private String statusName;
    private Long dictionaryId;
    private String dictionaryName;

    private Long affiliateId;
    private String affiliateName;
    private Long channelId;
    private String channelName;
    private Long campaignId;
    private String campaignName;
    private Long mediaId;
    private String mediaName;
    private Long commissionId;
    private String commissionName;
    private Double commissionValue;

    private Double value;
    private Long revenueId;
    private Long revenue;

    private Long clickNumber;
    private Long impressionNumber;
    private Long leadNumber;
    private String data;
    private Long walletId;
    private Boolean payoutPresent;
    private Long payoutId;
    private String payoutReference;

    public static TransactionStatusDTO from(ViewTransactionStatus transaction) {
        return new TransactionStatusDTO(
                transaction.getId(), transaction.getTipo(),
                transaction.getCreationDate(), transaction.getDateTime(),

                transaction.getStatusId(),
                transaction.getStatusName(),
                transaction.getDictionaryId(),
                transaction.getDictionaryName(),

                transaction.getAffiliateId(),
                transaction.getAffiliateName(),
                transaction.getChannelId(),
                transaction.getChannelName(),
                transaction.getCampaignId(),
                transaction.getCampaignName(),
                transaction.getMediaId(),
                transaction.getMediaName(),
                transaction.getCommissionId(),
                transaction.getCommissionName(),
                transaction.getCommissionValue(),

                transaction.getValue(),
                transaction.getRevenueId(),
                transaction.getRevenue(),

                transaction.getClickNumber(),
                transaction.getImpressionNumber(),
                transaction.getLeadNumber(),
                transaction.getData(),

                transaction.getWalletId(),
                transaction.getPayoutPresent(),
                transaction.getPayoutId(),
                transaction.getPayoutReference()

        );
    }

}
