package it.cleverad.engine.scheduled.rigenera;

import it.cleverad.engine.business.RigeneraCPCBusiness;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
public class RigeneraCPC {

    @Autowired
    private RigeneraCPCBusiness rigeneraCPCBusiness;

    @Scheduled(cron = "14 14 2 * * ?")
    public void rigeneraCPCdiIeri() {
        LocalDate localDate = LocalDate.now();
        LocalDate yesterday = localDate.minusDays(1);
        rigeneraCPCBusiness.rigenera(yesterday.getYear(), yesterday.getMonthValue(), yesterday.getDayOfMonth(), null, null);
    }//rigeneraCPCdiIeri

}