package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.TransactionCPL;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TransactionCPLDTO {

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
    private String data;

    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    public TransactionCPLDTO(Long id, Long affiliateId, String affiliateName, Long campaignId, String campaignName, Long commissionId, String commissionName, Long channelId, String channelName, LocalDateTime dateTime, Double value, Boolean approved, String ip, String agent, String data, LocalDateTime creationDate, LocalDateTime lastModificationDate) {
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
        this.data = data;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
    }

    public static TransactionCPLDTO from(TransactionCPL transaction) {
        return new TransactionCPLDTO(transaction.getId(),
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                transaction.getCreationDate(), transaction.getLastModificationDate());
    }

}
