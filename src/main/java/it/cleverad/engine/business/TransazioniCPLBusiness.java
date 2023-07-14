package it.cleverad.engine.business;

import it.cleverad.engine.config.model.Refferal;
import it.cleverad.engine.persistence.model.service.RevenueFactor;
import it.cleverad.engine.persistence.model.tracking.Cpl;
import it.cleverad.engine.persistence.repository.tracking.CplRepository;
import it.cleverad.engine.service.ReferralService;
import it.cleverad.engine.web.dto.*;
import it.cleverad.engine.web.exception.ElementCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional
public class TransazioniCPLBusiness {

    @Autowired
    private CplBusiness cplBusiness;
    @Autowired
    private TransactionBusiness transactionBusiness;
    @Autowired
    private TransactionAllBusiness transactionAllBusiness;
    @Autowired
     private WalletBusiness walletBusiness;
    @Autowired
    private BudgetBusiness budgetBusiness;
    @Autowired
    private CampaignBusiness campaignBusiness;
    @Autowired
    private RevenueFactorBusiness revenueFactorBusiness;
    @Autowired
    private AffiliateChannelCommissionCampaignBusiness affiliateChannelCommissionCampaignBusiness;
    @Autowired
    private ReferralService referralService;
    @Autowired
    private CpcBusiness cpcBusiness;
    @Autowired
    private CplRepository cplRepository;

    public void rigenera(Integer anno, Integer mese, Integer giorno) {

        try {
            LocalDate dataDaGestire = LocalDate.of(anno, mese, giorno);
            log.info(anno + "-" + mese + "-" + giorno + " >> " + dataDaGestire);

            // cancello le transazioni
            TransactionAllBusiness.Filter request = new TransactionAllBusiness.Filter();
            request.setCreationDateFrom(dataDaGestire);
            request.setCreationDateTo(dataDaGestire);
            List not = new ArrayList();
            not.add(68L);
            request.setNotInId(not);
            request.setTipo("CPL");
            Page<TransactionAllDTO> ls = transactionAllBusiness.searchPrefiltrato(request, PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("id"))));

            log.info(">>> TOT :: " + ls.getTotalElements());

            for (TransactionAllDTO tcpl : ls) {
                log.info("CANCELLO PER RIGENERA CP :: {} : {} :: {}", tcpl.getId(), tcpl.getValue(), tcpl.getDateTime());
                transactionBusiness.delete(tcpl.getId(), "CPL");
                Thread.sleep(100L);
            }

            cplBusiness.getAllDay(anno, mese, giorno).stream().filter(cplDTO -> StringUtils.isNotBlank(cplDTO.getRefferal())).forEach(cplDTO -> {

                if (cplDTO.getRefferal().length() < 6) {
                    List<CpcDTO> ips = cpcBusiness.findByIp24HoursBefore(cplDTO.getIp(), cplDTO.getDate(), cplDTO.getRefferal()).stream().collect(Collectors.toList());
                    for (CpcDTO dto : ips)
                        if (StringUtils.isNotBlank(dto.getRefferal())) cplDTO.setRefferal(dto.getRefferal());
                }

                // prendo reffereal e lo leggo
                Refferal refferal = referralService.decodificaReferral(cplDTO.getRefferal());

                if (refferal != null && refferal.getAffiliateId() != null) {
                    log.debug("RELEAD :: {} :: ", cplDTO);

                    //aggiorno dati CPL
                    Cpl cccpl = cplRepository.findById(cplDTO.getId()).orElseThrow(() -> new ElementCleveradException("Cpl", cplDTO.getId()));
                    cccpl.setMediaId(refferal.getMediaId());
                    cccpl.setCampaignId(refferal.getCampaignId());
                    cccpl.setAffiliateId(refferal.getAffiliateId());
                    cccpl.setChannelId(refferal.getChannelId());
                    cccpl.setTargetId(refferal.getTargetId());
                    cplRepository.save(cccpl);

                    // setta transazione
                    TransactionBusiness.BaseCreateRequest transaction = new TransactionBusiness.BaseCreateRequest();
                    transaction.setAffiliateId(refferal.getAffiliateId());
                    transaction.setCampaignId(refferal.getCampaignId());
                    transaction.setChannelId(refferal.getChannelId());
                    transaction.setMediaId(refferal.getMediaId());
                    transaction.setDateTime(cplDTO.getDate());
                    transaction.setApproved(true);
                    transaction.setPayoutPresent(false);

                    if (StringUtils.isNotBlank(cplDTO.getAgent())) transaction.setAgent(cplDTO.getAgent());
                    else transaction.setAgent("");

                    transaction.setIp(cplDTO.getIp());
                    transaction.setData(cplDTO.getData());
                    transaction.setMediaId(refferal.getMediaId());

                    // controlla data scadneza camapgna
                    try {
                        CampaignDTO campaignDTO = campaignBusiness.findByIdAdmin(refferal.getCampaignId());
                        LocalDate endDate = campaignDTO.getEndDate();
                        if (endDate.isBefore(cplDTO.getDate().toLocalDate())) {
                            // setto a campagna scaduta
                            transaction.setDictionaryId(49L);
                        } else {
                            //setto pending
                            transaction.setDictionaryId(42L);
                        }

                        // associo a wallet
                        Long affiliateID = refferal.getAffiliateId();

                        Long walletID = null;
                        if (affiliateID != null) {
                            walletID = walletBusiness.findByIdAffilaite(refferal.getAffiliateId()).stream().findFirst().get().getId();
                            transaction.setWalletId(walletID);
                        }

                        // trovo revenue
                        RevenueFactor rf = revenueFactorBusiness.getbyIdCampaignAndDictionrayId(refferal.getCampaignId(), 11L);
                        if (rf != null) {
                            transaction.setRevenueId(rf.getId());
                        } else {
                            transaction.setRevenueId(2L);
                        }

                        // gesione commisione
                        Double commVal = 0D;

                        AffiliateChannelCommissionCampaignBusiness.Filter req = new AffiliateChannelCommissionCampaignBusiness.Filter();
                        req.setAffiliateId(refferal.getAffiliateId());
                        req.setChannelId(refferal.getChannelId());
                        req.setCampaignId(refferal.getCampaignId());
                        req.setCommissionDicId(11L);
                        AffiliateChannelCommissionCampaignDTO acccFirst = affiliateChannelCommissionCampaignBusiness.search(req).stream().findFirst().orElse(null);

                        if (acccFirst != null) {
                            commVal = acccFirst.getCommissionValue();
                            transaction.setCommissionId(acccFirst.getCommissionId());
                        } else {
                            transaction.setCommissionId(0L);
                        }

                        Double totale = commVal * 1;
                        transaction.setValue(totale);
                        transaction.setLeadNumber(Long.valueOf(1));

                        // incemento valore
                        if (walletID != null && totale > 0D) walletBusiness.incement(walletID, totale);

                        // decremento budget Affiliato
                        BudgetDTO bb = budgetBusiness.getByIdCampaignAndIdAffiliate(refferal.getCampaignId(), refferal.getAffiliateId()).stream().findFirst().orElse(null);
                        if (bb != null) {
                            Double totBudgetDecrementato = bb.getBudget() - totale;
                            budgetBusiness.updateBudget(bb.getId(), totBudgetDecrementato);

                            // setto stato transazione a ovebudget editore se totale < 0
                            if (totBudgetDecrementato < 0) {
                                transaction.setDictionaryId(47L);
                            }
                        }

                        // decremento budget Campagna
                        if (campaignDTO != null) {

                            Double budgetCampagna = campaignDTO.getBudget() - totale;
                            campaignBusiness.updateBudget(campaignDTO.getId(), budgetCampagna);

                            // setto stato transazione a ovebudget editore se totale < 0
                            if (budgetCampagna < 0) {
                                transaction.setDictionaryId(48L);
                            }
                        }

                        if (cccpl.getBlacklisted()) transaction.setStatusId(70L);
                        else transaction.setStatusId(42L);

                        // creo la transazione
                        TransactionCPLDTO cpl = transactionBusiness.createCpl(transaction);
                        log.info(">>>RIGENERATO LEAD :::: {} ", cpl.getId());

                        // setto a gestito
                        cplBusiness.setRead(cplDTO.getId());
                    } catch (Exception ecc) {
                        log.error("ECCEZIONE CPL :> ", ecc);
                    }
                }// creo solo se ho affiliate
            });
        } catch (Exception e) {
            log.error("CUSTOM Eccezione Scheduler CPL --  {}", e.getMessage(), e);
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class FilterUpdate {
        private String year;
        private String month;
        private String day;
    }
}
