package it.cleverad.engine.business;

import it.cleverad.engine.config.model.Refferal;
import it.cleverad.engine.persistence.model.service.Commission;
import it.cleverad.engine.persistence.model.service.RevenueFactor;
import it.cleverad.engine.persistence.model.tracking.Cpl;
import it.cleverad.engine.persistence.repository.service.CommissionRepository;
import it.cleverad.engine.persistence.repository.tracking.CplRepository;
import it.cleverad.engine.service.ReferralService;
import it.cleverad.engine.web.dto.*;
import it.cleverad.engine.web.dto.tracking.CpcDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.decimal4j.util.DoubleRounder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional
public class RigeneraCPLBusiness {

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
    @Autowired
    private CommissionRepository commissionRepository;

    public void rigenera(Integer anno, Integer mese, Integer giorno, Long affiliateId) {
        try {

            Integer start = giorno;
            Integer end = giorno;
            if (giorno == null) {
                start = 1;
                end = LocalDate.of(anno, mese, 1).lengthOfMonth();
            }

            LocalDate dataDaGestireStart = LocalDate.of(anno, mese, start);
            LocalDate dataDaGestireEnd = LocalDate.of(anno, mese, end);
            log.info(anno + "-" + mese + "-" + giorno + " >> " + dataDaGestireStart + " || " + dataDaGestireEnd + " per " + affiliateId);

            // cancello le transazioni
            TransactionAllBusiness.Filter request = new TransactionAllBusiness.Filter();
            request.setCreationDateFrom(dataDaGestireStart);
            request.setCreationDateTo(dataDaGestireEnd);
            request.setTipo("CPL");
            request.setAffiliateId(affiliateId);
            List<Long> not = new ArrayList<>();
            not.add(68L); // MANUALE
            request.setNotInDictionaryId(not);
            not = new ArrayList<>();
            not.add(74L); // RIGETTATO
            request.setNotInStatusId(not);

            Page<TransactionStatusDTO> ls = transactionAllBusiness.searchPrefiltrato(request, PageRequest.of(0, Integer.MAX_VALUE));
            log.info(">>> TOT :: " + ls.getTotalElements());

            for (TransactionStatusDTO tcpl : ls) {
                log.trace("CANCELLO PER RIGENERA CP :: {} : {} :: {}", tcpl.getId(), tcpl.getValue(), tcpl.getDateTime());
                transactionBusiness.delete(tcpl.getId(), "CPL");
                Thread.sleep(50L);
            }

            for (int gg = start; gg <= end; gg++) {
                cplBusiness.getAllDay(anno, mese, gg, affiliateId).stream().filter(cplDTO -> StringUtils.isNotBlank(cplDTO.getRefferal())).forEach(cplDTO -> {

                    // leggo sempre i cpc precedenti per trovare il click riferito alla lead
                    cpcBusiness.findByIp24HoursBefore(cplDTO.getIp(), cplDTO.getDate(), cplDTO.getRefferal())
                            .stream()
                            .filter(cpcDTO -> StringUtils.isNotBlank(cpcDTO.getRefferal()))
                            .forEach(cpcDTO -> {
                                log.info("R ORIG {} --> R CPC {}", cplDTO.getRefferal(), cpcDTO.getRefferal());
                                cplDTO.setRefferal(cpcDTO.getRefferal());
                                cplDTO.setCpcId(cpcDTO.getId());
                            });
                    log.info("Refferal :: {} con ID CPC {}", cplDTO.getRefferal(), cplDTO.getCpcId());
                    cplBusiness.setCpcId(cplDTO.getId(), cplDTO.getCpcId());

                    // prendo reffereal e lo leggo
                    Refferal refferal = referralService.decodificaReferral(cplDTO.getRefferal());
                    if (refferal != null && refferal.getAffiliateId() != null) {
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
                            Long commissionId = 0L;
                            AffiliateChannelCommissionCampaignBusiness.Filter req = new AffiliateChannelCommissionCampaignBusiness.Filter();
                            if (StringUtils.isNotBlank(cplDTO.getActionId())) {
                                // con action Id settanto in cpl vado a cercare la commissione associata
                                req.setAffiliateId(refferal.getAffiliateId());
                                req.setChannelId(refferal.getChannelId());
                                req.setCampaignId(refferal.getCampaignId());
                                req.setActionId(cplDTO.getActionId().trim());
                                AffiliateChannelCommissionCampaignDTO accc = affiliateChannelCommissionCampaignBusiness.search(req).stream().findFirst().orElse(null);
                                Commission cm = commissionRepository.findById(accc.getCommissionId()).get();
                                commVal = cm.getValue();
                                commissionId = cm.getId();
                                log.warn("Commissione >{}< trovata da action ID >{}<", cm.getId(), cplDTO.getActionId());
                            } else {
                                // non è settato l'actionId allora faccio il solito giro
                                req.setAffiliateId(refferal.getAffiliateId());
                                req.setChannelId(refferal.getChannelId());
                                req.setCampaignId(refferal.getCampaignId());
                                req.setCommissionDicId(11L);
                                AffiliateChannelCommissionCampaignDTO acccFirst = affiliateChannelCommissionCampaignBusiness.search(req).stream().findFirst().orElse(null);
                                if (acccFirst != null) {
                                    commVal = acccFirst.getCommissionValue();
                                    commissionId = acccFirst.getCommissionId();
                                } else
                                    log.warn("Non trovato Commission di tipo 10 per campagna {}, setto default", refferal.getCampaignId());
                            }
                            transaction.setCommissionId(commissionId);

                            Double totale = commVal * 1;
                            transaction.setValue(DoubleRounder.round(totale, 2));
                            transaction.setLeadNumber(1L);

                            // incemento valore
                            if (walletID != null && totale > 0D) walletBusiness.incement(walletID, totale);

                            // decremento budget Affiliato
                            BudgetDTO bb = budgetBusiness.getByIdCampaignAndIdAffiliate(refferal.getCampaignId(), refferal.getAffiliateId()).stream().findFirst().orElse(null);
                            if (bb != null && bb.getBudget() != null) {
                                Double totBudgetDecrementato = bb.getBudget() - totale;
                                budgetBusiness.updateBudget(bb.getId(), totBudgetDecrementato);

                                // setto stato transazione a ovebudget editore se totale < 0
                                if (totBudgetDecrementato < 0) {
                                    transaction.setDictionaryId(47L);
                                }
                            }

                            // decremento budget Campagna
                            Double budgetCampagna = campaignDTO.getBudget() - totale;
                            campaignBusiness.updateBudget(campaignDTO.getId(), budgetCampagna);

                            // setto stato transazione a ovebudget editore se totale < 0
                            if (budgetCampagna < 0) {
                                transaction.setDictionaryId(48L);
                            }

                            if (cccpl.getBlacklisted() != null && cccpl.getBlacklisted()) transaction.setStatusId(74L);
                            else transaction.setStatusId(72L);

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
            }// ciclo se prendo in considerazione tutto il mese

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
        private Long affiliateId;
    }
}