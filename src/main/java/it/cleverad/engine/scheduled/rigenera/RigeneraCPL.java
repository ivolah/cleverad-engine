package it.cleverad.engine.scheduled.rigenera;

import it.cleverad.engine.business.RigeneraCPLBusiness;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
public class RigeneraCPL {

    @Autowired
    private RigeneraCPLBusiness rigenera;

    @Scheduled(cron = "24 24 2 * * ?")
    public void rigeneraCPLdiIeri() {
        LocalDate localDate = LocalDate.now();
        LocalDate yesterday = localDate.minusDays(1);
        rigenera.rigenera(yesterday.getYear(), yesterday.getMonthValue(), yesterday.getDayOfMonth(), null, null, false);
    }//rigeneraCPLdiIeri

}