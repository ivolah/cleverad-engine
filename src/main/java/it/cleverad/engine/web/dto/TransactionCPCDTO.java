package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.TransactionCPC;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TransactionCPCDTO {

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
    private Long clickNumber;
    private String payoutReference;
    private Long payoutId;
    private String note;

    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    public TransactionCPCDTO(Long id, Long affiliateId, String affiliateName, Long campaignId, String campaignName, Long commissionId, String commissionName, Long channelId, String channelName, Long mediaId, String mediaName, LocalDateTime dateTime, Double value, Boolean approved, String ip, String agent, Long clickNumber, String payoutReference, Long payoutId, String note, LocalDateTime creationDate, LocalDateTime lastModificationDate) {
        this.id = id;
        this.affiliateId = affiliateId;
        this.affiliateName = affiliateName;
        this.campaignId = campaignId;
        this.campaignName = campaignName;
        this.commissionId = commissionId;
        this.commissionName = commissionName;
        this.channelId = channelId;
        this.channelName = channelName;
        this.mediaId = mediaId;
        this.mediaName = mediaName;
        this.dateTime = dateTime;
        this.value = value;
        this.approved = approved;
        this.ip = ip;
        this.agent = agent;
        this.clickNumber = clickNumber;
        this.payoutReference = payoutReference;
        this.payoutId = payoutId;
        this.note = note;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
    }

    public static TransactionCPCDTO from(TransactionCPC transaction) {
        return new it.cleverad.engine.web.dto.TransactionCPCDTO(transaction.getId(),
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
                transaction.getAgent(), transaction.getClickNumber(),

                transaction.getPayoutReference(),
                transaction.getPayout() != null ? transaction.getPayout().getId() : null,
                
                transaction.getNote(),
                transaction.getCreationDate(), transaction.getLastModificationDate());
    }

}
