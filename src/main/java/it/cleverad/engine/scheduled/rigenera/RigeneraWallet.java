package it.cleverad.engine.scheduled.rigenera;

import it.cleverad.engine.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RigeneraWallet {

    @Autowired
    private WalletService walletService;

    @Scheduled(cron = "8 1 0/2 * * ?")
    public void consolidaWallet() {
        walletService.rigenera(null);
    }//consolidaWallet

}