package it.cleverad.engine.service;

import it.cleverad.engine.business.TransactionCPCBusiness;
import it.cleverad.engine.business.TransactionCPLBusiness;
import it.cleverad.engine.business.TransactionCPMBusiness;
import it.cleverad.engine.persistence.model.service.TransactionCPC;
import it.cleverad.engine.persistence.model.service.TransactionCPL;
import it.cleverad.engine.web.dto.AffiliateBudgetDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.decimal4j.util.DoubleRounder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional
@Service
public class ReportService {

    @Autowired
    private TransactionCPLBusiness transactionCPLBusiness;
    @Autowired
    private TransactionCPCBusiness transactionCPCBusiness;
    @Autowired
    private TransactionCPMBusiness transactionCPMBusiness;

    public void topCampagne(Integer anno, Integer mese, Integer giorno, Long affiliateId, Long campaignId) {

        int start = (giorno == null) ? 1 : giorno;
        int end = (giorno == null) ? LocalDate.of(anno, mese, 1).lengthOfMonth() : giorno;

        LocalDate dataDtart = LocalDate.of(anno, mese, start);
        LocalDate dataEnd = LocalDate.of(anno, mese, end);

        // 1. --- TUTTE LE TRANSAZIONI
        List<Long> campaignIdsCPL = transactionCPLBusiness.searchForCampaignAffiliateBudget(campaignId, affiliateId, dataDtart, dataEnd).stream().mapToLong(value -> value.getCampaign().getId()).boxed().collect(Collectors.toList());
        List<Long> campaignIdsCPC = transactionCPCBusiness.searchForCampaignAffiliateBudget(campaignId, affiliateId, dataDtart, dataEnd).stream().mapToLong(value -> value.getCampaign().getId()).boxed().collect(Collectors.toList());
        List<Long> campaignIdsCPM = transactionCPMBusiness.searchForCampaignAffiliateBudget(campaignId, affiliateId, dataDtart, dataEnd).stream().mapToLong(value -> value.getCampaign().getId()).boxed().collect(Collectors.toList());
        log.info("TRANS SIZE: " + campaignIdsCPL.size() + "  " + campaignIdsCPC.size() + "  " + campaignIdsCPM.size());

        campaignIdsCPL.addAll(campaignIdsCPC);
        campaignIdsCPL.addAll(campaignIdsCPM);
        campaignIdsCPL = campaignIdsCPL.stream().distinct().collect(Collectors.toList());
        log.info(" NUM CAMP :: {}", campaignIdsCPL.size());

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class FilterUpdate {
        private Integer year;
        private Integer month;
        private Integer day;
        private Long affiliateId;
        private Long campaignId;
    }

}