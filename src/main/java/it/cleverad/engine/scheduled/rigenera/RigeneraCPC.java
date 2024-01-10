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
    private RigeneraCPCBusiness rigeneraCPC;

    @Scheduled(cron = "14 14 2 * * ?")
    public void consolidaCPC() {

        LocalDate localDate = LocalDate.now();
        LocalDate yesterday = localDate.minusDays(1);
        rigeneraCPC.rigenera(yesterday.getYear(), yesterday.getMonthValue(), yesterday.getDayOfMonth(), null, null);

    }//rigeneraCPC

}