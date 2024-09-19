package it.cleverad.engine.business;

import it.cleverad.engine.config.model.Refferal;
import it.cleverad.engine.persistence.model.service.QueryTransaction;
import it.cleverad.engine.persistence.model.service.RevenueFactor;
import it.cleverad.engine.persistence.model.tracking.Cps;
import it.cleverad.engine.persistence.repository.tracking.CpsRepository;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@Transactional
public class RigeneraCPSBusiness {

    @Autowired
    private CpsBusiness cpsBusiness;
    @Autowired
    private TransactionStatusBusiness transactionStatusBusiness;
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
    private CpsRepository cpsRepository;
    @Autowired
    private TransactionCPSBusiness transactionCPSBusiness;
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
            TransactionStatusBusiness.QueryFilter request = new TransactionStatusBusiness.QueryFilter();
            request.setCreationDateFrom(dataDaGestireStart);
            request.setCreationDateTo(dataDaGestireEnd);
            request.setTipo("CPS");
            if (affiliateId != null)
                request.setAffiliateId(affiliateId);
            if (camapignId != null)
                request.setCampaignId(camapignId);
            List<Long> not = new ArrayList<>();
            not.add(68L); // MANUALE
            request.setNotInDictionaryId(not);
            not = new ArrayList<>();
            not.add(74L); // RIGETTATO
            request.setNotInStausId(not);
            Page<QueryTransaction> ls = transactionStatusBusiness.searchPrefiltratoN(request, Pageable.ofSize(Integer.MAX_VALUE));
            log.info(">>> TOT :: " + ls.getTotalElements());
            for (QueryTransaction tcps : ls) {
                log.trace("CANCELLO PER RIGENERA CP :: {} : {} :: {}", tcps.getid(), tcps.getValue(), tcps.getDateTime());

                // aggiorno budget affiliato schedualto
                // aggiorno wallet in modo schedulato
                // aggiorno campaign buget in modo schedualto

                transactionCPSBusiness.delete(tcps.getid());
                Thread.sleep(50L);
            }

            for (int gg = start; gg <= end; gg++) {
                cpsBusiness.getAllDay(anno, mese, gg, affiliateId).stream().filter(cpsDTO -> StringUtils.isNotBlank(cpsDTO.getRefferal())).forEach(cpsDTO -> {

                    // leggo sempre i cpc precedenti per trovare il click riferito alla lead
                    cpcBusiness.findByIp24HoursBefore(cpsDTO.getIp(), cpsDTO.getDate(), cpsDTO.getRefferal())
                            .stream()
                            .filter(cpcDTO -> StringUtils.isNotBlank(cpcDTO.getRefferal()))
                            .forEach(cpcDTO -> {
                                cpsDTO.setRefferal(cpcDTO.getRefferal());
                                cpsDTO.setCpcId(cpcDTO.getId());
                            });
                    log.trace("Refferal :: {} con ID CPC {}", cpsDTO.getRefferal(), cpsDTO.getCpcId());
                    cpsBusiness.setCpcId(cpsDTO.getId(), cpsDTO.getCpcId());

                    // prendo reffereal e lo leggo
                    Refferal refferal = referralService.decodificaReferral(cpsDTO.getRefferal());
                    if (refferal != null && refferal.getAffiliateId() != null) {
                        //aggiorno dati CPS
                        Cps cccps = cpsRepository.findById(cpsDTO.getId()).orElseThrow(() -> new ElementCleveradException("Cps", cpsDTO.getId()));
                        cccps.setMediaId(refferal.getMediaId());
                        cccps.setCampaignId(refferal.getCampaignId());
                        cccps.setAffiliateId(refferal.getAffiliateId());
                        cccps.setChannelId(refferal.getChannelId());
                        cccps.setTargetId(refferal.getTargetId());
                        cpsRepository.save(cccps);

                        // setta transazione
                        TransactionCPSBusiness.BaseCreateRequest transaction = new TransactionCPSBusiness.BaseCreateRequest();
                        transaction.setRefferal(cpsDTO.getRefferal());
                        transaction.setAffiliateId(refferal.getAffiliateId());
                        transaction.setCampaignId(refferal.getCampaignId());
                        transaction.setChannelId(refferal.getChannelId());
                        transaction.setMediaId(refferal.getMediaId());
                        transaction.setDateTime(cpsDTO.getDate());
                        transaction.setApproved(true);
                        transaction.setPayoutPresent(false);
                        transaction.setIp(cpsDTO.getIp());
                        transaction.setData(cpsDTO.getData().trim().replace("[REPLACE]", ""));
                        transaction.setMediaId(refferal.getMediaId());
                        transaction.setCpcId(cpsDTO.getCpcId());

                        if (StringUtils.isNotBlank(cpsDTO.getAgent())) transaction.setAgent(cpsDTO.getAgent());
                        else transaction.setAgent("");

                        // controlla data scadneza camapgna
                        try {
                            CampaignDTO campaignDTO = campaignBusiness.findByIdAdmin(refferal.getCampaignId());
                            LocalDate endDate = campaignDTO.getEndDate();
                            if (endDate.isBefore(cpsDTO.getDate().toLocalDate())) {
                                // setto a campagna scaduta
                                transaction.setDictionaryId(49L);
                            } else {
                                //setto pending
                                transaction.setDictionaryId(42L);
                            }

                            // associo a wallet
                            Long affiliateID = refferal.getAffiliateId();

                            Long walletID;
                            if (affiliateID != null) {
                                walletID = walletBusiness.findByIdAffilaite(refferal.getAffiliateId()).stream().findFirst().get().getId();
                                transaction.setWalletId(walletID);
                            }

                            // trovo revenue

                            RevenueFactor rf = revenueFactorBusiness.getbyIdCampaignAndDictionrayId(refferal.getCampaignId(), 51L);
                            if (rf != null) {
                                transaction.setRevenueId(rf.getId());
                            } else {
                                log.warn("Non trovato revenue factor di tipo 51 per campagna {} , setto default", refferal.getCampaignId());
                                transaction.setRevenueId(2L);
                            }


                            // gesione commisione
                            Double sellValue = 0D;
                            Long commissionId = 0L;
                            AffiliateChannelCommissionCampaignBusiness.Filter req = new AffiliateChannelCommissionCampaignBusiness.Filter();

                            // non è settato l'actionId allora faccio il solito giro
                            req.setAffiliateId(refferal.getAffiliateId());
                            req.setChannelId(refferal.getChannelId());
                            req.setCampaignId(refferal.getCampaignId());
                            req.setCommissionDicId(51L);
                            AffiliateChannelCommissionCampaignDTO acccFirst = affiliateChannelCommissionCampaignBusiness.search(req).stream().findFirst().orElse(null);
                            if (acccFirst != null) {
                                // trovo in info di cps il ORDER VALUE
                                String info = cpsDTO.getInfo();
                                Map<String, String> infos = referralService.estrazioneInfo(info);
                                String orderValue = infos.get("ORDER_VALUE");
                                log.info("OrderValue: " + orderValue);
                                if (StringUtils.isNotBlank(orderValue)) {
                                    Double valore = Double.valueOf(orderValue);
                                    Double percentuale = acccFirst.getSale();
                                    log.info("Valore {} - PErcenutale {}", valore, percentuale);
                                    sellValue = (percentuale / valore) * 100.0;
                                    log.info("SELL VALUE :: {}", sellValue);
                                }
                                commissionId = acccFirst.getCommissionId();
                            } else
                                log.warn("No Commission CPS C: {} e A: {}, setto default ({})", refferal.getCampaignId(), refferal.getAffiliateId(), cpsDTO.getRefferal());

                            transaction.setCommissionId(commissionId);
                            Double totale = DoubleRounder.round(sellValue * 1, 2);
                            transaction.setValue(totale);
                            transaction.setLeadNumber(1L);

                            // incemento valore scheduled

                            // decremento budget Affiliato
                            AffiliateBudgetDTO bb = affiliateBudgetBusiness.getByIdCampaignAndIdAffiliate(refferal.getCampaignId(), refferal.getAffiliateId()).stream().findFirst().orElse(null);

                            if (bb != null && bb.getBudget() != null && (bb.getBudget() - totale) < 0) {
                                transaction.setDictionaryId(47L);
                            }

                            // setto stato transazione a ovebudget editore se totale < 0
                            CampaignBudgetDTO campBudget = campaignBudgetBusiness.searchByCampaignAndDate(camapignId, cpsDTO.getDate().toLocalDate()).stream().findFirst().orElse(null);
                            if (campBudget != null && campBudget.getBudgetErogato() != null && campBudget.getBudgetErogato() - totale < 0)
                                transaction.setDictionaryId(48L);

                            if (cccps.getBlacklisted() != null && cccps.getBlacklisted()) transaction.setStatusId(74L);
                            else transaction.setStatusId(72L);

                            // creo la transazione
                            TransactionCPSDTO cps = transactionCPSBusiness.createCps(transaction);
                            log.trace(">>>RIGENERATO CPS :::: {} ", cps.getId());

                            // setto a gestito
                            cpsBusiness.setRead(cpsDTO.getId());

                        } catch (Exception ecc) {
                            log.error("ECCEZIONE CPS :> ", ecc);
                        }
                    }// creo solo se ho affiliate
                });
            }// ciclo se prendo in considerazione tutto il mese

        } catch (Exception e) {
            log.error("CUSTOM Eccezione Scheduler CPS --  {}", e.getMessage(), e);
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