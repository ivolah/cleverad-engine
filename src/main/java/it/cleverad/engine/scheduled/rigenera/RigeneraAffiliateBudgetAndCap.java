package it.cleverad.engine.scheduled.rigenera;

import it.cleverad.engine.service.AffiliaiteBudgetService;
import it.cleverad.engine.service.WalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RigeneraAffiliateBudgetAndCap {

    @Autowired
    private AffiliaiteBudgetService affiliaiteBudgetService;

    @Scheduled(cron = "33 33 0/3 * * ?")
    public void consolidaAffiliateBudgetAndCap() {






    }//consolidaAffiliateBudgetAndCap

}