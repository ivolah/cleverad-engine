package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.Wallet;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class WalletDTO {

    private Long id;
    private String nome;
    private Double total;
    private Double payed;
    private Double residual;
    private Long affiliateId;
    private String affiliateName;

    public WalletDTO(Long id, String nome, Double total, Double payed, Double residual, Long affiliateId, String affiliateName) {
        this.id = id;
        this.nome = nome;
        this.total = total;
        this.payed = payed;
        this.residual = residual;
        this.affiliateId = affiliateId;
        this.affiliateName = affiliateName;
    }

    public static WalletDTO from(Wallet wallet) {
        return new WalletDTO(wallet.getId(), wallet.getNome(), wallet.getTotal(), wallet.getPayed(), wallet.getResidual(),  wallet.getAffiliate() != null ? wallet.getAffiliate().getId() : null, wallet.getAffiliate() != null ? wallet.getAffiliate().getName() : null);
    }

}