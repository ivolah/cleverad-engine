package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.Payout;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class PayoutDTO {

    private Long id;

    private Long affiliateId;
    private String affiliateName;

    private Double totale;
    private String valuta;
    private String note;
    private LocalDate data;
    private Boolean stato;

    private LocalDateTime creationDate = LocalDateTime.now();
    private LocalDateTime lastModificationDate = LocalDateTime.now();

    private List<TransactionCPCDTO> transactionCPCS;

    public PayoutDTO(Long id, Long affiliateId, String affiliateName, Double totale, String valuta, String note, LocalDate data, Boolean stato, LocalDateTime creationDate, LocalDateTime lastModificationDate, List<TransactionCPCDTO> transactionCPCS) {
        this.id = id;
        this.affiliateId = affiliateId;
        this.affiliateName = affiliateName;
        this.totale = totale;
        this.valuta = valuta;
        this.note = note;
        this.data = data;
        this.stato = stato;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
        this.transactionCPCS = transactionCPCS;
    }

    public static PayoutDTO from(Payout payout) {

        List<TransactionCPCDTO> transactionCPCS = new ArrayList<>();
        if (payout.getTransactionCPCS() != null) {
            transactionCPCS = payout.getTransactionCPCS().stream().map(cpc -> {
                TransactionCPCDTO dto = new TransactionCPCDTO();
                dto.setId(cpc.getId());
                dto.setAffiliateId(cpc.getAffiliate() != null ? cpc.getAffiliate().getId() : null);
                dto.setAffiliateName(cpc.getAffiliate() != null ? cpc.getAffiliate().getName() : null);
                dto.setCampaignId(cpc.getCampaign() != null ? cpc.getCampaign().getId() : null);
                dto.setCampaignName(cpc.getCampaign() != null ? cpc.getCampaign().getName() : null);
                dto.setChannelId(cpc.getChannel() != null ? cpc.getChannel().getId() : null);
                dto.setChannelName(cpc.getChannel() != null ? cpc.getChannel().getName() : null);
                dto.setMediaId(cpc.getMedia() != null ? cpc.getMedia().getId() : null);
                dto.setMediaName(cpc.getMedia() != null ? cpc.getMedia().getName() : null);
                dto.setCommissionId(cpc.getCommission() != null ? cpc.getCommission().getId() : null);
                dto.setCommissionName(cpc.getCommission() != null ? cpc.getCommission().getName() : null);
                dto.setValue(cpc.getValue());
                dto.setClickNumber(cpc.getClickNumber());
                dto.setNote(cpc.getNote());
                dto.setCreationDate(cpc.getCreationDate());
                dto.setDateTime(cpc.getDateTime());
                return dto;
            }).collect(Collectors.toList());
        }

        return new PayoutDTO(
                payout.getId(),
                payout.getAffiliate() != null ? payout.getAffiliate().getId() : null,
                payout.getAffiliate() != null ? payout.getAffiliate().getName() : null,
                payout.getTotale(), payout.getValuta(), payout.getNote(), payout.getData(), payout.getStato(), payout.getCreationDate(), payout.getLastModificationDate(),
                transactionCPCS
        );
    }

}
