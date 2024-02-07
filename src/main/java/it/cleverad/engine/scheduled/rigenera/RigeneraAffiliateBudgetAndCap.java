package it.cleverad.engine.scheduled.rigenera;

import it.cleverad.engine.service.AffiliaiteBudgetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
public class RigeneraAffiliateBudgetAndCap {

    @Autowired
    private AffiliaiteBudgetService affiliaiteBudgetService;

    @Scheduled(cron = "33 33 0/1 * * ?")
    public void consolidaAffiliateBudgetAndCap() {
       affiliaiteBudgetService.rigeneraAffiliateBudget(LocalDate.now().getYear(), LocalDate.now().getMonthValue(),LocalDate.now().getDayOfMonth(),null, null);
    }//consolidaAffiliateBudgetAndCap

}