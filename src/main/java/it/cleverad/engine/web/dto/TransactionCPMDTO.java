package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.TransactionCPM;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
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

    private LocalDateTime dateTime;
    private Double value;
    private Boolean approved;

    private String ip;
    private String agent;

    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    public TransactionCPMDTO(Long id, Long affiliateId, String affiliateName, Long campaignId, String campaignName, Long commissionId, String commissionName, Long channelId, String channelName, LocalDateTime dateTime, Double value, Boolean approved, String ip, String agent, LocalDateTime creationDate, LocalDateTime lastModificationDate) {
        this.id = id;
        this.affiliateId = affiliateId;
        this.affiliateName = affiliateName;
        this.campaignId = campaignId;
        this.campaignName = campaignName;
        this.commissionId = commissionId;
        this.commissionName = commissionName;
        this.channelId = channelId;
        this.channelName = channelName;
        this.dateTime = dateTime;
        this.value = value;
        this.approved = approved;
        this.ip = ip;
        this.agent = agent;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
    }

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

                transaction.getDateTime(),
                transaction.getValue(),
                transaction.getApproved(),
                transaction.getIp(),
                transaction.getAgent(),
                transaction.getCreationDate(), transaction.getLastModificationDate());
    }

}
