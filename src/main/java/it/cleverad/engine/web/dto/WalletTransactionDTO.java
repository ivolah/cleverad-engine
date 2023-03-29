package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.Wallet;
import it.cleverad.engine.persistence.model.service.WalletTransaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletTransactionDTO {

    private Long id;

    private Double totalBefore;
    private Double payedBefore;
    private Double residualBefore;
    private Double totalAfter;
    private Double payedAfter;
    private Double residualAfter;

    private LocalDateTime date;

    private Long walletId;

    public static WalletTransactionDTO from(WalletTransaction wallet) {
        return new WalletTransactionDTO(
                wallet.getId(),
                wallet.getTotalBefore(), wallet.getTotalAfter(),
                wallet.getPayedBefore(), wallet.getPayedAfter(),
                wallet.getResidualAfter(), wallet.getResidualBefore(),
                wallet.getDate(),
                wallet.getWallet() != null ? wallet.getWallet().getId() : null);
    }

}
