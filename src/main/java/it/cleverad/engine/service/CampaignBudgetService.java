package it.cleverad.engine.service;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.business.*;
import it.cleverad.engine.persistence.model.service.TransactionCPC;
import it.cleverad.engine.persistence.model.service.TransactionCPL;
import it.cleverad.engine.web.dto.CampaignBudgetDTO;
import lombok.extern.slf4j.Slf4j;
import org.decimal4j.util.DoubleRounder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CampaignBudgetService {

    @Autowired
    private CampaignBudgetBusiness campaignBudgetBusiness;
    @Autowired
    private TransactionCPLBusiness transactionCPLBusiness;
    @Autowired
    private TransactionCPCBusiness transactionCPCBusiness;
    @Autowired
    private RevenueFactorBusiness revenueFactorBusiness;
    @Autowired
    private FileCampaignBudgetBusiness fileCampaignBudgetBusiness;
    @Autowired
    private Mapper mapper;

    public void gestisciCampaignBudget(Long id) {
        DoubleRounder br = new DoubleRounder(2);

        List<CampaignBudgetDTO> budgets = new ArrayList<>();
        if (id == null) {
            // listo i budget attivi
            budgets = campaignBudgetBusiness.searchAttivi().stream().collect(Collectors.toList());
        } else {
            // cerco il budget specifico
            budgets.add(campaignBudgetBusiness.findById(id));
        }

        for (CampaignBudgetDTO dto : budgets) {
            log.trace("GESTISCO {}:{} ", dto.getCampaignId(), dto.getCampaignName());

            Integer capErogato = 0;
            double budgetErogato = 0D;
            double commissioniErogate = 0D;

            // TODO in futuro aggiungere proprietà per gestire solo quelle non precedentemente lette per consolida budget

            // TODO , usare come date da a quelle del budget

            // >>>>>>>>>>>>>>>>>>> CPL
            List<TransactionCPL> cpls = transactionCPLBusiness.searchForCampaignBudget(dto.getCampaignId(), dto.getStartDate(), dto.getEndDate());
            log.info("NUMERO TRANS CPL {} :: {} ({})", dto.getCampaignId(), cpls.size(), dto.getCapIniziale());
            for (TransactionCPL cpl : cpls) {
                // tolgo limite cap if (capErogato < dto.getCapIniziale()) {
                capErogato += 1;
                budgetErogato += revenueFactorBusiness.findById(cpl.getRevenueId()).getRevenue();
                commissioniErogate += cpl.getCommission().getValue();
                //}
            }
            log.trace("CPL budgetErogato :: {}", budgetErogato);
            log.trace("CPL capErogato :: {} ", capErogato);
            log.trace("CPL commissioniErogate :: {}", commissioniErogate);

            // >>>>>>>>>>>>>>>>>>> CPC
            List<TransactionCPC> cpcs = transactionCPCBusiness.searchForCampaignBudget(dto.getCampaignId(), dto.getStartDate(), dto.getEndDate());
            log.info("NUMERO TRANS CPC {} :: {} ({})", dto.getCampaignId(), cpcs.size(), dto.getCapIniziale());
            for (TransactionCPC cpc : cpcs) {
                // tolgo limite cap if (capErogato < dto.getCapIniziale()) {
                capErogato += cpc.getClickNumber().intValue();
                budgetErogato += revenueFactorBusiness.findById(cpc.getRevenueId()).getRevenue() * cpc.getClickNumber().intValue();
                commissioniErogate += (cpc.getCommission().getValue() * cpc.getClickNumber().intValue());
                log.trace("{} :: comm value  {} :: {} * {} == {}", capErogato, cpc.getValue(), cpc.getCommission().getValue(), cpc.getClickNumber().intValue(), (cpc.getCommission().getValue() * cpc.getClickNumber().intValue()));
                //  }
            }
            log.trace("CPC budgetErogato :: {}", budgetErogato);
            log.trace("CPC capErogato :: {} ", capErogato);
            log.trace("CPC commissioniErogate :: {}", commissioniErogate);

            budgetErogato = br.round(budgetErogato);
            commissioniErogate = br.round(commissioniErogate);

            log.trace("POST budgetErogato :: {}", budgetErogato);
            log.trace("POST capErogato :: {} ", capErogato);
            log.trace("POST commissioniErogate :: {}", commissioniErogate);

            double revenue = budgetErogato - commissioniErogate;
            double scarto = 0D;
            if (dto.getScarto() != null) scarto = dto.getScarto();

            CampaignBudgetBusiness.BaseCreateRequest request = new CampaignBudgetBusiness.BaseCreateRequest();
            mapper.map(dto, request);

            request.setBudgetErogato(budgetErogato);
            request.setCommissioniErogate(br.round(commissioniErogate));
            request.setCapErogato(capErogato);

            // Cap Percentuale
            Double ce = 0D;
            if (capErogato != 0D)
                ce = br.round((double) (capErogato * 100) / dto.getCapIniziale());
            request.setCapPc(ce);

            // Revenue Percentuale
            Double rpc = 0D;
            if (budgetErogato != 0D)
                rpc = br.round((revenue / budgetErogato) * 100);
            request.setRevenuePC(rpc);

            // Budget Erogato Post Scarto
            Double beps = 0D;
            if (budgetErogato != 0D)
                beps = br.round(budgetErogato - (budgetErogato / 100 * scarto));
            request.setBudgetErogatops(beps);

            // Commisioni Erogate Post Scarto
            Double ceps = 0D;
            if (commissioniErogate != 0D)
                ceps = br.round(commissioniErogate - (commissioniErogate / 100 * scarto));
            request.setCommissioniErogateps(ceps);

            // Revenu Post Scarto
            Double rps = 0D;
            if (scarto != 0D)
                rps = br.round(revenue - (revenue / 100 * scarto));
            request.setRevenuePS(rps);

            // Revenue Percentuale Post Scarto
            Double rpcps = 0D;
            if (request.getBudgetErogatops() != 0D)
                rpcps = br.round((request.getRevenuePS() / request.getBudgetErogatops()) * 100);
            request.setRevenuePCPS(rpcps);

            request.setRevenueDay(br.round(revenue / LocalDate.now().getDayOfMonth()));
            request.setId(null);
            request.setStatus(true);
            request.setRevenue(br.round(revenue));

            CampaignBudgetDTO cb = campaignBudgetBusiness.create(request);
            log.trace("CREATO :: {}", cb.getId());

            //  ASSEGNO INVOICE A NUOVO BUDGET
            dto.getFileCampaignBudgetInvoices().stream().forEach(invoice -> fileCampaignBudgetBusiness.updateInterno(invoice.getId(), "INVOICE", cb.getId()));
            //  ASSEGNO ORDER A NUOVO BUDGET
            dto.getFileCampaignBudgetOrders().stream().forEach(order -> fileCampaignBudgetBusiness.updateInterno(order.getId(), "ORDER", cb.getId()));

            // cancello se la data di creazione e oggi
            // in questo modo mantendo solo l'utimo budget del giorno precedente
            if (dto.getCreationDate().getDayOfYear() < LocalDateTime.now().getDayOfYear()) {
                // lo setto a non attivo
                campaignBudgetBusiness.disable(dto.getId());
                log.trace("Disabilito :: {}", dto.getId());
            } else {
                // cancello
                campaignBudgetBusiness.delete(dto.getId());
                log.trace("ELIMINO {}", dto.getId());
            }

        }

        log.trace("END");
    }

}