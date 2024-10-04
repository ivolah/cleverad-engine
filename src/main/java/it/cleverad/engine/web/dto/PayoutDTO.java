package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.Payout;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import jakarta.persistence.Column;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@ToString
public class PayoutDTO {

    private Long id;
    private Long affiliateId;
    private String affiliateName;
    private Double totale;
    private Double iva;
    private Double imponibile;
    private String valuta;
    private String note;
    private LocalDate data;
    private LocalDate dataScadenza;
    private Long dictionaryId;
    private String dictionaryName;
    @Column(name = "creation_date")
    private LocalDateTime creationDate = LocalDateTime.now();
    @Column(name = "last_modification_date")
    private LocalDateTime lastModificationDate = LocalDateTime.now();
    private List<TransactionCPCDTO> transactionCPCS;
    private List<TransactionCPLDTO> transactionCPLS;

    public PayoutDTO(Long id, Long affiliateId, String affiliateName, Double totale, Double iva, String valuta, String note, LocalDate data, LocalDate dataScadenza, Long dictionaryId, String dictionaryName, LocalDateTime creationDate, LocalDateTime lastModificationDate, List<TransactionCPCDTO> transactionCPCS, List<TransactionCPLDTO> transactionCPLS, Double imponibile) {
        this.id = id;
        this.affiliateId = affiliateId;
        this.affiliateName = affiliateName;
        this.totale = totale;
        this.iva = iva;
        this.valuta = valuta;
        this.note = note;
        this.data = data;
        this.dataScadenza = dataScadenza;
        this.dictionaryId = dictionaryId;
        this.dictionaryName = dictionaryName;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
        this.transactionCPCS = transactionCPCS;
        this.transactionCPLS = transactionCPLS;
        this.imponibile = imponibile;
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

        List<TransactionCPLDTO> transactionCPLS = new ArrayList<>();
        if (payout.getTransactionCPLS() != null) transactionCPLS = payout.getTransactionCPLS().stream().map(cpl -> {
            TransactionCPLDTO dto = new TransactionCPLDTO();
            dto.setId(cpl.getId());
            dto.setAffiliateId(cpl.getAffiliate() != null ? cpl.getAffiliate().getId() : null);
            dto.setAffiliateName(cpl.getAffiliate() != null ? cpl.getAffiliate().getName() : null);
            dto.setCampaignId(cpl.getCampaign() != null ? cpl.getCampaign().getId() : null);
            dto.setCampaignName(cpl.getCampaign() != null ? cpl.getCampaign().getName() : null);
            dto.setChannelId(cpl.getChannel() != null ? cpl.getChannel().getId() : null);
            dto.setChannelName(cpl.getChannel() != null ? cpl.getChannel().getName() : null);
            dto.setMediaId(cpl.getMedia() != null ? cpl.getMedia().getId() : null);
            dto.setMediaName(cpl.getMedia() != null ? cpl.getMedia().getName() : null);
            dto.setCommissionId(cpl.getCommission() != null ? cpl.getCommission().getId() : null);
            dto.setCommissionName(cpl.getCommission() != null ? cpl.getCommission().getName() : null);
            dto.setValue(cpl.getValue());
            dto.setData(cpl.getData());
            dto.setNote(cpl.getNote());
            dto.setCreationDate(cpl.getCreationDate());
            dto.setDateTime(cpl.getDateTime());
            return dto;
        }).collect(Collectors.toList());

        return new PayoutDTO(payout.getId(),
                payout.getAffiliate() != null ? payout.getAffiliate().getId() : null,
                payout.getAffiliate() != null ? payout.getAffiliate().getName() : null,
                payout.getTotale(), payout.getIva(), payout.getValuta(), payout.getNote(),
                payout.getData(),
                payout.getDataScadenza(),
                payout.getDictionary() != null ? payout.getDictionary().getId() : null,
                payout.getDictionary() != null ? payout.getDictionary().getName() : null,
                payout.getCreationDate(), payout.getLastModificationDate(), transactionCPCS, transactionCPLS, payout.getImponibile());
    }
    public static PayoutDTO fromNoTransazioni(Payout payout) {

        return new PayoutDTO(payout.getId(),
                payout.getAffiliate() != null ? payout.getAffiliate().getId() : null,
                payout.getAffiliate() != null ? payout.getAffiliate().getName() : null,
                payout.getTotale(), payout.getIva(), payout.getValuta(), payout.getNote(),
                payout.getData(),
                payout.getDataScadenza(),
                payout.getDictionary() != null ? payout.getDictionary().getId() : null,
                payout.getDictionary() != null ? payout.getDictionary().getName() : null,
                payout.getCreationDate(), payout.getLastModificationDate(), null, null, payout.getImponibile());
    }

}