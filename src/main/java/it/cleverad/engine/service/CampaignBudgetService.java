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
            // CPL
            if (dto.getTipologiaId().equals(97L)) {
                List<TransactionCPL> cpls = transactionCPLBusiness.searchByCampaignMese(dto.getCampaignId());
                for (TransactionCPL cpl : cpls) {
                    capErogato += 1;
                    budgetErogato += cpl.getValue();
                    commissioniErogate += cpl.getCommission().getValue();
                    //revenue += revenueFactorBusiness.findById(cpl.getRevenueId()).getRevenue();
                }
            }
            // CPC
            else if (dto.getTipologiaId().equals(96L)) {
                List<TransactionCPC> cpcs = transactionCPCBusiness.searchByCampaignMese(dto.getCampaignId());
                for (TransactionCPC cpc : cpcs) {
                    log.trace("{}-{}-{}", cpc.getValue(), cpc.getCommission().getValue(), revenueFactorBusiness.findById(cpc.getRevenueId()).getRevenue());
                    capErogato += 1;
                    budgetErogato += cpc.getValue();
                    commissioniErogate += cpc.getCommission().getValue();
//                    revenue += revenueFactorBusiness.findById(cpc.getRevenueId()).getRevenue();
                }
            }
            log.trace(" :: {} - {} - {}", capErogato, budgetErogato, commissioniErogate);

            double scarto;
            if (dto.getScarto() == null) scarto = 0D;
            else scarto = dto.getScarto();

            CampaignBudgetBusiness.BaseCreateRequest request = new CampaignBudgetBusiness.BaseCreateRequest();
            mapper.map(dto, request);

            request.setCapErogato(capErogato);

            if (capErogato != 0D) {
                request.setCapPc(br.round((double) (capErogato * 100) / dto.getCapIniziale()));
            } else request.setCapPc(0D);

            budgetErogato = br.round(capErogato * dto.getPayout());
            request.setBudgetErogato(budgetErogato);
            request.setCommissioniErogate(br.round(commissioniErogate));
            double revenue = budgetErogato - commissioniErogate;
            request.setRevenue(br.round(revenue));

            if (budgetErogato != 0D) {
                request.setRevenuePC(br.round((revenue / budgetErogato) * 100));
            } else request.setRevenuePC(0D);

            if (budgetErogato != 0D) {
                request.setBudgetErogatoPS(br.round(budgetErogato - (budgetErogato / 100 * scarto)));
            } else request.setBudgetErogatoPS(0D);

            if (commissioniErogate != 0D) {
                request.setCommissioniErogatePS(br.round(commissioniErogate - (commissioniErogate / 100 * scarto)));
            } else request.setCommissioniErogatePS(0D);

            if (scarto != 0D) {
                request.setRevenuePS(br.round(revenue - (revenue / 100 * scarto)));
            } else request.setRevenuePS(0D);

            if (request.getBudgetErogatoPS() != 0D) {
                request.setRevenuePCPS(br.round((request.getRevenuePS() / request.getBudgetErogatoPS()) * 100));
            } else request.setRevenuePCPS(0D);

            request.setRevenueDay(br.round(revenue / LocalDate.now().getDayOfMonth()));

            request.setId(null);
            request.setStatus(true);
            CampaignBudgetDTO cb = campaignBudgetBusiness.create(request);
            log.trace("CREATO :: {}\n", cb.getId());

            //  ASSEGNO INVOICE A NUOVO BUDGET
            dto.getFileCampaignBudgetInvoices().stream().forEach(invoice ->
                    fileCampaignBudgetBusiness.updateInterno(invoice.getId(), "INVOICE", cb.getId())
            );
            //  ASSEGNO ORDER A NUOVO BUDGET
            dto.getFileCampaignBudgetOrders().stream().forEach(order ->
                    fileCampaignBudgetBusiness.updateInterno(order.getId(), "ORDER", cb.getId())
            );

            // cancello se la data di creazione e oggi
            // in questo modo mantendo solo l'utimo budget del giorno precedente
            if (dto.getCreationDate().getDayOfYear() < LocalDateTime.now().getDayOfYear()) {
                // lo setto a non attivo
                campaignBudgetBusiness.disable(dto.getId());
                log.info("Disabilito :: {}", dto.getId());
            } else {
                // cancello
                campaignBudgetBusiness.delete(dto.getId());
                log.trace("ELIMINO {}", dto.getId());
            }

        }

        log.trace("END");
    }

}