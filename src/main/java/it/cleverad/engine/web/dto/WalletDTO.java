package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.Wallet;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WalletDTO {

    private Long id;

    private String nome;
    private String description;

    private Double total;
    private Double payed;
    private Double residual;

    private Boolean status;

    private Long affiliateId;
    private String affiliateName;

    public WalletDTO(Long id, String nome, String description, Double total, Double payed, Double residual, Boolean status, Long affiliateId, String affiliateName) {
        this.id = id;
        this.nome = nome;
        this.description = description;
        this.total = total;
        this.payed = payed;
        this.residual = residual;
        this.status = status;
        this.affiliateId = affiliateId;
        this.affiliateName = affiliateName;
    }

    public static WalletDTO from(Wallet wallet) {
        return new WalletDTO(wallet.getId(), wallet.getNome(), wallet.getDescription(), wallet.getTotal(), wallet.getPayed(), wallet.getResidual(), wallet.getStatus(),
                wallet.getAffiliate() != null ? wallet.getAffiliate().getId() : null,
                wallet.getAffiliate() != null ? wallet.getAffiliate().getName() : null );
    }

}
