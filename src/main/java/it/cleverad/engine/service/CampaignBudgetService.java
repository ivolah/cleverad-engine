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
    @Autowired
    private CampaignCostBusiness campaignCostBusiness;

    public void gestisciCampaignBudget(Long id, Boolean interno) {
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

            // >>>>>>>>>>>>>>>>>>> CPL
            List<TransactionCPL> cpls = transactionCPLBusiness.searchForCampaignBudget(dto.getCampaignId(), dto.getStartDate(), dto.getEndDate());
            log.trace("NUMERO TRANS CPL {} :: {} ({})", dto.getCampaignId(), cpls.size(), dto.getCapIniziale());
            for (TransactionCPL cpl : cpls) {
                capErogato += 1;
                if (revenueFactorBusiness.findById(cpl.getRevenueId(), interno) != null)
                    budgetErogato += revenueFactorBusiness.findById(cpl.getRevenueId(), interno).getRevenue();
                commissioniErogate += cpl.getCommission().getValue();
            }
            log.trace("CPL budgetErogato :: {}", budgetErogato);
            log.trace("CPL capErogato :: {} ", capErogato);
            log.trace("CPL commissioniErogate :: {}", commissioniErogate);

            // >>>>>>>>>>>>>>>>>>> CPC
            List<TransactionCPC> cpcs = transactionCPCBusiness.searchForCampaignBudget(dto.getCampaignId(), dto.getStartDate(), dto.getEndDate());
            log.trace("NUMERO TRANS CPC {} :: {} ({})", dto.getCampaignId(), cpcs.size(), dto.getCapIniziale());
            for (TransactionCPC cpc : cpcs) {
                capErogato += cpc.getClickNumber().intValue();
                if (revenueFactorBusiness.findById(cpc.getRevenueId(), false) != null)
                    budgetErogato += revenueFactorBusiness.findById(cpc.getRevenueId(), false).getRevenue() * cpc.getClickNumber().intValue();
                commissioniErogate += (cpc.getCommission().getValue() * cpc.getClickNumber().intValue());
                log.trace("{} :: comm value  {} :: {} * {} == {}", capErogato, cpc.getValue(), cpc.getCommission().getValue(), cpc.getClickNumber().intValue(), (cpc.getCommission().getValue() * cpc.getClickNumber().intValue()));
            }
            log.trace("CPC budgetErogato :: {}", budgetErogato);
            log.trace("CPC capErogato :: {} ", capErogato);
            log.trace("CPC commissioniErogate :: {}", commissioniErogate);

            CampaignBudgetBusiness.BaseCreateRequest request = new CampaignBudgetBusiness.BaseCreateRequest();
            mapper.map(dto, request);

            double scarto = 0D;
            if (dto.getScarto() != null) scarto = dto.getScarto();

            //round
            budgetErogato = br.round(budgetErogato);
            commissioniErogate = br.round(commissioniErogate);

            // set base
            request.setCommissioniErogate(br.round(commissioniErogate));
            request.setBudgetErogato(budgetErogato);
            request.setCapErogato(capErogato);

            double revenue;
            // nel caso venga valorizzato il volume erogato
            if (dto.getVolume() != null && dto.getVolume() > 0) {
                request.setVolume(dto.getVolume());
                request.setVolumeDelta(dto.getVolume() - capErogato);
                capErogato = dto.getVolume();
                revenue = budgetErogato - commissioniErogate;
            } else {
                request.setCapVolume(capErogato);
                revenue = budgetErogato - commissioniErogate;
            }
            request.setVolumeDate(dto.getVolumeDate());

            // Cap Percentuale
            Double ce = 0D;
            if (capErogato != 0D) ce = br.round((double) (capErogato * 100) / dto.getCapIniziale());
            request.setCapPc(ce);

            // Revenue Percentuale
            Double rpc = 0D;
            if (budgetErogato != 0D) rpc = br.round((revenue / budgetErogato) * 100);
            request.setRevenuePC(rpc);

            // Budget Erogato Post Scarto
            Double beps = 0D;
            if (budgetErogato != 0D) beps = br.round(budgetErogato - (budgetErogato / 100 * scarto));
            request.setBudgetErogatops(beps);

            // Commisioni Erogate Post Scarto
            Double ceps = 0D;
            if (commissioniErogate != 0D) ceps = br.round(commissioniErogate - (commissioniErogate / 100 * scarto));
            request.setCommissioniErogateps(ceps);

            // Revenu Post Scarto
            Double rps = 0D;
            if (scarto != 0D) rps = br.round(revenue - (revenue / 100 * scarto));
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

            request.setStatoPagato(dto.getStatoPagato());
            request.setStatoFatturato(dto.getStatoFatturato());
            request.setInvoiceDueDate(dto.getInvoiceDueDate());
            Double costi = campaignCostBusiness.searchByCampaignIdUnpaged(dto.getId()).toList().stream().mapToDouble(campaignCostDTO -> campaignCostDTO.getCosto()).sum();
            request.setCosti(costi);

            CampaignBudgetDTO cb = campaignBudgetBusiness.create(request);
            log.trace("CREATO :: {}", cb.getId());

            //  ASSEGNO INVOICE A NUOVO BUDGET
            dto.getFileCampaignBudgetInvoices().stream().forEach(invoice -> fileCampaignBudgetBusiness.updateInterno(invoice.getId(), "INVOICE", cb.getId()));
            //  ASSEGNO ORDER A NUOVO BUDGET
            dto.getFileCampaignBudgetOrders().stream().forEach(order -> fileCampaignBudgetBusiness.updateInterno(order.getId(), "ORDER", cb.getId()));

            // cancello
            campaignBudgetBusiness.delete(dto.getId());
            log.trace("ELIMINO {}", dto.getId());
        }
        log.trace("END");
    }

}