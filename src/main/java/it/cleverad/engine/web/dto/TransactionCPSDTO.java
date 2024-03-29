package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.TransactionCPS;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionCPSDTO {

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
    private String data;
    private String payoutReference;
    private Long payoutId;
    private String note;

    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    private Long dictionaryId;
    private String dictionaryName;
    private Boolean payoutPresent;

    private Long statusId;

    public static TransactionCPSDTO from(TransactionCPS transaction) {
        return new TransactionCPSDTO(transaction.getId(), transaction.getAffiliate() != null ? transaction.getAffiliate().getId() : null, transaction.getAffiliate() != null ? transaction.getAffiliate().getName() : null,

                transaction.getCampaign() != null ? transaction.getCampaign().getId() : null, transaction.getCampaign() != null ? transaction.getCampaign().getName() : null,

                transaction.getCommission() != null ? transaction.getCommission().getId() : null, transaction.getCommission() != null ? transaction.getCommission().getName() : null,

                transaction.getChannel() != null ? transaction.getChannel().getId() : null, transaction.getChannel() != null ? transaction.getChannel().getName() : null,

                transaction.getMedia() != null ? transaction.getMedia().getId() : null,
                transaction.getMedia() != null ? transaction.getMedia().getName() : null,

                transaction.getDateTime(), transaction.getValue(), transaction.getApproved(), transaction.getIp(),
                transaction.getAgent(), transaction.getData(),

                transaction.getPayoutReference(),
                transaction.getPayout() != null ? transaction.getPayout().getId() : null,

                transaction.getNote(), transaction.getCreationDate(), transaction.getLastModificationDate(),

                transaction.getDictionary().getId(), transaction.getDictionary().getName(),
                transaction.getPayoutPresent(),
                transaction.getDictionaryStatus().getId());
    }

}
