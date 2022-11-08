package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.Payout;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class PayoutDTO {

    private Long id;

    private Long affiliateId;

    private Double totale;
    private String valuta;
    private String note;
    private LocalDate data;
    private String stato;

    private LocalDateTime creationDate = LocalDateTime.now();
    private LocalDateTime lastModificationDate = LocalDateTime.now();

    public PayoutDTO(Long id, Long affiliateId, Double totale, String valuta, String note, LocalDate data, String stato, LocalDateTime creationDate, LocalDateTime lastModificationDate) {
        this.id = id;
        this.affiliateId = affiliateId;
        this.totale = totale;
        this.valuta = valuta;
        this.note = note;
        this.data = data;
        this.stato = stato;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
    }

    public static PayoutDTO from(Payout payout) {
        return new PayoutDTO(payout.getId(), payout.getAffiliateId(), payout.getTotale(), payout.getValuta(), payout.getNote(), payout.getData(), payout.getStato(), payout.getCreationDate(), payout.getLastModificationDate());
    }

}
