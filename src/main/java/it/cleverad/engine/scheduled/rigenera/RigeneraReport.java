package it.cleverad.engine.scheduled.rigenera;

import it.cleverad.engine.service.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
public class RigeneraReport {

    @Autowired
    private ReportService reportService;

    @Scheduled(cron = "40 0/15 * * * ?")
    public void rigeneraReportTOPMese() {
//        reportService.topCampagne(LocalDate.now().getYear(), LocalDate.now().getMonthValue(), null, null, null);
    }//consolidaAffiliateBudgetAndCap

}