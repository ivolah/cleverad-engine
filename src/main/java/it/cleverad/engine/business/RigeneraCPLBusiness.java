package it.cleverad.engine.business;

import it.cleverad.engine.config.model.Refferal;
import it.cleverad.engine.persistence.model.service.Commission;
import it.cleverad.engine.persistence.model.service.RevenueFactor;
import it.cleverad.engine.persistence.model.service.TransactionCPL;
import it.cleverad.engine.persistence.model.tracking.Cpl;
import it.cleverad.engine.persistence.repository.service.CommissionRepository;
import it.cleverad.engine.persistence.repository.service.TransactionCPLRepository;
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
import org.decimal4j.util.DoubleRounder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@Transactional
public class RigeneraCPLBusiness {
    @Autowired
    private TransactionCPLRepository transactionCPLRepository;

    @Autowired
    private CplBusiness cplBusiness;
    @Autowired
    private TransactionAllBusiness transactionAllBusiness;
    @Autowired
    private WalletBusiness walletBusiness;
    @Autowired
    private AffiliateBudgetBusiness affiliateBudgetBusiness;
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
    @Autowired
    private TransactionCPLBusiness transactionCPLBusiness;
    @Autowired
    private CampaignBudgetBusiness campaignBudgetBusiness;

    public void rigenera(Integer anno, Integer mese, Integer giorno, Long affiliateId, Long camapignId) {
        try {

            int start = (giorno == null) ? 1 : giorno;
            int end = (giorno == null) ? LocalDate.of(anno, mese, 1).lengthOfMonth() : giorno;

            LocalDate dataDaGestireStart = LocalDate.of(anno, mese, start);
            LocalDate dataDaGestireEnd = LocalDate.of(anno, mese, end);

            log.info(anno + "-" + mese + "-" + giorno + " >> " + dataDaGestireStart + " || " + dataDaGestireEnd + " per " + affiliateId + " e " + camapignId);

            // cancello le transazioni
            TransactionAllBusiness.Filter request = new TransactionAllBusiness.Filter();
            request.setCreationDateFrom(dataDaGestireStart);
            request.setCreationDateTo(dataDaGestireEnd);
            request.setTipo("CPL");
            request.setAffiliateId(affiliateId);
            request.setCampaignId(camapignId);
            List<Long> not = new ArrayList<>();
            not.add(68L); // MANUALE
            request.setNotInDictionaryId(not);
            not = new ArrayList<>();
            not.add(74L); // RIGETTATO
            request.setNotInStatusId(not);

            Page<TransactionStatusDTO> ls = transactionAllBusiness.searchPrefiltratoInterno(request);
            log.info(">>> TOT :: " + ls.getTotalElements());

            for (TransactionStatusDTO tcpl : ls) {
                log.trace("CANCELLO PER RIGENERA CP :: {} : {} :: {}", tcpl.getId(), tcpl.getValue(), tcpl.getDateTime());

                TransactionCPL trans = transactionCPLRepository.findById(tcpl.getId()).orElseThrow(() -> new ElementCleveradException("Transaction", 0L));
                // aggiorno budget affiliato
                // da gestire in esterna con un azione scheduata
                AffiliateBudgetDTO budgetAff = affiliateBudgetBusiness.getByIdCampaignAndIdAffiliate(trans.getCampaign().getId(), trans.getAffiliate().getId()).stream().findFirst().orElse(null);
                if (budgetAff != null) {
                    affiliateBudgetBusiness.updateBudget(budgetAff.getId(), budgetAff.getBudget() + trans.getValue());
                    affiliateBudgetBusiness.updateCap(budgetAff.getId(), budgetAff.getCap() + 1);
                }

                // aggiorno budget campagna
                // - non serve più  abbiamo campagin budget :
                // aggiorno wallet in modo schedulato
                // aggiorno campaign buget in modo schedualto

                transactionCPLBusiness.delete(tcpl.getId());
                Thread.sleep(50L);
            }

            for (int gg = start; gg <= end; gg++) {
                cplBusiness.getAllDay(anno, mese, gg, affiliateId).stream().filter(cplDTO -> StringUtils.isNotBlank(cplDTO.getRefferal())).forEach(cplDTO -> {

                    // leggo sempre i cpc precedenti per trovare il click riferito alla lead
                    cpcBusiness.findByIp24HoursBefore(cplDTO.getIp(), cplDTO.getDate(), cplDTO.getRefferal())
                            .stream()
                            .filter(cpcDTO -> StringUtils.isNotBlank(cpcDTO.getRefferal()))
                            .forEach(cpcDTO -> {
                                cplDTO.setRefferal(cpcDTO.getRefferal());
                                cplDTO.setCpcId(cpcDTO.getId());
                            });
                    log.trace("Refferal :: {} con ID CPC {}", cplDTO.getRefferal(), cplDTO.getCpcId());
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
                        TransactionCPLBusiness.BaseCreateRequest transaction = new TransactionCPLBusiness.BaseCreateRequest();
                        transaction.setRefferal(cplDTO.getRefferal());
                        transaction.setAffiliateId(refferal.getAffiliateId());
                        transaction.setCampaignId(refferal.getCampaignId());
                        transaction.setChannelId(refferal.getChannelId());
                        transaction.setMediaId(refferal.getMediaId());
                        transaction.setDateTime(cplDTO.getDate());
                        transaction.setApproved(true);
                        transaction.setPayoutPresent(false);
                        transaction.setIp(cplDTO.getIp());
                        transaction.setData(cplDTO.getData().trim().replace("[REPLACE]", ""));
                        transaction.setMediaId(refferal.getMediaId());
                        transaction.setCpcId(cplDTO.getCpcId());

                        if (StringUtils.isNotBlank(cplDTO.getAgent())) transaction.setAgent(cplDTO.getAgent());
                        else transaction.setAgent("");

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
                                Commission cm = commissionRepository.findById(Objects.requireNonNull(accc).getCommissionId()).get();
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

                            Double totale = DoubleRounder.round(commVal * 1, 2);
                            transaction.setValue(totale);
                            transaction.setLeadNumber(1L);

                            // incemento valore
                            if (walletID != null && totale > 0D) walletBusiness.incement(walletID, totale);

                            // decremento budget Affiliato
                            AffiliateBudgetDTO bb = affiliateBudgetBusiness.getByIdCampaignAndIdAffiliate(refferal.getCampaignId(), refferal.getAffiliateId()).stream().findFirst().orElse(null);
                            if (bb != null && bb.getBudget() != null) {
                                double totBudgetDecrementato = bb.getBudget() - totale;
                                affiliateBudgetBusiness.updateBudget(bb.getId(), totBudgetDecrementato);

                                // setto stato transazione a ovebudget editore se totale < 0
                                if (totBudgetDecrementato < 0) {
                                    transaction.setDictionaryId(47L);
                                }
                            }

                            // setto stato transazione a ovebudget editore se totale < 0
                            CampaignBudgetDTO campBudget = campaignBudgetBusiness.searchByCampaignAndDate(camapignId, cplDTO.getDate().toLocalDate()).stream().findFirst().orElse(null);
                            if (campBudget.getBudgetErogato() - totale < 0) transaction.setDictionaryId(48L);

                            if (cccpl.getBlacklisted() != null && cccpl.getBlacklisted()) transaction.setStatusId(74L);
                            else transaction.setStatusId(72L);

                            // creo la transazione
                            TransactionCPLDTO cpl = transactionCPLBusiness.createCpl(transaction);
                            log.trace(">>>RIGENERATO LEAD :::: {} ", cpl.getId());

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
        private Integer year;
        private Integer month;
        private Integer day;
        private Long affiliateId;
        private Long campaignId;
    }
}