package it.cleverad.engine.scheduled.manage;

import it.cleverad.engine.business.*;
import it.cleverad.engine.config.model.Refferal;
import it.cleverad.engine.persistence.model.service.RevenueFactor;
import it.cleverad.engine.persistence.model.tracking.Cpm;
import it.cleverad.engine.persistence.repository.service.WalletRepository;
import it.cleverad.engine.persistence.repository.tracking.CpmRepository;
import it.cleverad.engine.service.ReferralService;
import it.cleverad.engine.web.dto.AffiliateChannelCommissionCampaignDTO;
import it.cleverad.engine.web.dto.CampaignBudgetDTO;
import it.cleverad.engine.web.dto.CampaignDTO;
import it.cleverad.engine.web.dto.TransactionCPMDTO;
import it.cleverad.engine.web.dto.tracking.CpmDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ManageCPM {

    @Autowired
    CampaignBudgetBusiness campaignBudgetBusiness;
    @Autowired
    private CpmBusiness CpmBusiness;
    @Autowired
    private CpmRepository repository;
    @Autowired
    private TransactionCPMBusiness transactionCPMBusiness;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private CampaignBusiness campaignBusiness;
    @Autowired
    private AffiliateChannelCommissionCampaignBusiness affiliateChannelCommissionCampaignBusiness;
    @Autowired
    private RevenueFactorBusiness revenueFactorBusiness;
    @Autowired
    private ReferralService referralService;

    /**
     * ============================================================================================================
     **/

    @Scheduled(cron = "12 0/8 * * * ?")
    @Async
    public void gestisciTransazioni() {
        trasformaTrackingCPM(true);
        gestisciBlacklisted();
    }

    /**
     * ============================================================================================================
     **/

    public void trasformaTrackingCPM(boolean today) {
        try {
            // trovo tutti i tracking con read == false
            Map<String, Integer> mappa = new HashMap<>();
            List<Long> lstaID = new ArrayList<>();
            List<CpmDTO> last;
            if (today)
                last = CpmBusiness.getUnreadHourBefore().getContent();
            else
                last = CpmBusiness.getAllDaysBefore().getContent();

            log.trace("MANAGE CPM TOT {}", last.size());
            last = last.stream().limit(20000).collect(Collectors.toList());

            List<Triple> triples = new ArrayList<>();
            last.stream().filter(cpmDTO -> cpmDTO.getRefferal() != null).forEach(cpm -> {

                // gestisco calcolatore
                Integer num = mappa.get(cpm.getRefferal());
                if (num == null) num = 0;
                if (cpm.getRefferal().length() < 5) {
                    // cerco da cpc
                    List<CpmDTO> ips = CpmBusiness.findByIp24HoursBefore(cpm.getIp(), cpm.getDate()).stream().collect(Collectors.toList());
                    // prendo ultimo   isp
                    for (CpmDTO dto : ips)
                        if (StringUtils.isNotBlank(dto.getRefferal())) dto.setRefferal(dto.getRefferal());
                }
                mappa.put(cpm.getRefferal(), num + 1);

                Triple<LocalDate, String, Integer> triple = new ImmutableTriple<>(cpm.getDate().toLocalDate(), cpm.getRefferal(), num + 1);
                triples.add(triple);

                // TODO SE PESANTE EVENTUALEMNTE TOLGO
                // aggiorno dati CPM
                Cpm cpmm = repository.findById(cpm.getId()).orElseThrow(() -> new ElementCleveradException("CPM", cpm.getId()));
                if (cpm.getRefferal().equals("{{refferalId}}")) {
                    cpmm.setRefferal("");
                } else {
                    Refferal refferal = referralService.decodificaReferral(cpm.getRefferal());
                    if (refferal != null) {
                        if (refferal.getMediaId() != null) {
                            cpmm.setMediaId(refferal.getMediaId());
                        }
                        if (refferal.getCampaignId() != null) {
                            cpmm.setCampaignId(refferal.getCampaignId());
                        }
                        if (refferal.getAffiliateId() != null) {
                            cpmm.setAffiliateId(refferal.getAffiliateId());
                        }
                        if (refferal.getChannelId() != null) {
                            cpmm.setChannelId(refferal.getChannelId());
                        }
                        if (refferal.getTargetId() != null) {
                            cpmm.setTargetId(refferal.getTargetId());
                        }
                    }
                }
                lstaID.add(cpm.getId());

                repository.save(cpmm);
            });

            if (mappa != null)
                mappa.forEach((x, aLong) -> {
                    // prendo reffereal e lo leggo
                    Refferal refferal = referralService.decodificaReferral(x);
                    log.trace(">>>> T-CPM :: {} -> {} - {}", aLong, x, refferal);
                    if (refferal != null && refferal.getCampaignId() != null && !Objects.isNull(refferal.getAffiliateId())) {

                        CampaignDTO campaignDTO = campaignBusiness.findByIdAdminNull(refferal.getCampaignId());
                        if (campaignDTO != null) {

                            // setta transazione
                            TransactionCPMBusiness.BaseCreateRequest transaction = new TransactionCPMBusiness.BaseCreateRequest();
                            transaction.setAffiliateId(refferal.getAffiliateId());
                            transaction.setCampaignId(refferal.getCampaignId());
                            transaction.setChannelId(refferal.getChannelId());
                            transaction.setDateTime(LocalDateTime.now().withMinute(0).withSecond(0).withNano(0).minusMinutes(1));
                            transaction.setMediaId(refferal.getMediaId());
                            transaction.setApproved(true);
                            transaction.setPayoutPresent(false);
                            transaction.setImpressionNumber(Long.valueOf(aLong));

                            // controlla data scadneza camapgna
                            LocalDate endDate = campaignDTO.getEndDate();
                            Boolean scaduta = false;
                            if (endDate.isBefore(LocalDate.now())) {
                                // setto a campagna scaduta
                                transaction.setDictionaryId(42L);
                                scaduta = true;
                            } else {
                                transaction.setDictionaryId(49L);
                            }

                            if (!campaignDTO.getStatus()) {
                                // setto a campagna scaduta
                                transaction.setDictionaryId(49L);
                                scaduta = true;
                            }

                            // associo a wallet
                            Long affiliateID = refferal.getAffiliateId();

                            Long walletID;
                            if (affiliateID != null) {
                                try {
                                    walletID = walletRepository.findByAffiliateId(affiliateID).getId();
                                    transaction.setWalletId(walletID);
                                } catch (NullPointerException exception) {
                                    log.warn("Non trovo wallet per affiliate {}", affiliateID);
                                }
                            }

                            if (scaduta) {
                                log.trace("Campagna {} : {} scaduta", campaignDTO.getId(), campaignDTO.getName());
                                transaction.setRevenueId(1L);
                                transaction.setCommissionId(0L);
                                transaction.setStatusId(74L); // rigettato
                                transaction.setValue(0D);
                                transaction.setDictionaryId(49L);
                            } else {
                                // trovo revenue
                                RevenueFactor rf = revenueFactorBusiness.getbyIdCampaignAndDictionrayId(refferal.getCampaignId(), 50L);
                                if (rf != null && rf.getId() != null) {
                                    transaction.setRevenueId(rf.getId());
                                } else {
                                    transaction.setRevenueId(3L);
                                }

                                // gesione commisione
                                Double commVal = 0D;

                                AffiliateChannelCommissionCampaignBusiness.Filter req = new AffiliateChannelCommissionCampaignBusiness.Filter();
                                req.setAffiliateId(refferal.getAffiliateId());
                                req.setChannelId(refferal.getChannelId());
                                req.setCampaignId(refferal.getCampaignId());
                                req.setCommissionDicId(50L);
                                AffiliateChannelCommissionCampaignDTO acccFirst = affiliateChannelCommissionCampaignBusiness.search(req).stream().findFirst().orElse(null);

                                if (acccFirst != null) {
                                    log.trace(acccFirst.getCommissionId() + " " + acccFirst.getCommissionValue());
                                    commVal = acccFirst.getCommissionValue();
                                    transaction.setCommissionId(acccFirst.getCommissionId());
                                } else {
                                    transaction.setCommissionId(0L);
                                }

                                Double totale = commVal * aLong;
                                transaction.setValue(totale);

                                // Stato Budget Campagna
                                if (totale > 0) {
                                    CampaignBudgetDTO campBudget = campaignBudgetBusiness.searchByCampaignAndDate(refferal.getCampaignId(), transaction.getDateTime().toLocalDate()).stream().findFirst().orElse(null);
                                    if (campBudget != null && campBudget.getBudgetErogato() != null) {
                                        Double budgetCampagna = campBudget.getBudgetErogato() - totale;
                                        // setto stato transazione a ovebudget editore se totale < 0
                                        if (budgetCampagna < 0) {
                                            transaction.setDictionaryId(48L);
                                        }
                                    }
                                }

                                //setto APPROVATO - sempre per i CPM
                                transaction.setStatusId(73L);
                            }

                            // creo la transazione
                            TransactionCPMDTO tcpm = transactionCPMBusiness.createCpm(transaction);
                            log.trace(">>> CREATO TRANSAZIONE :::: CPM :::: {}", tcpm.getId());
                        }

                    }// refferal not null
                });

            // setto a gestito
            lstaID.forEach(aLong -> CpmBusiness.setRead(aLong));

        } catch (Exception e) {
            log.error("Eccezione Scheduler Cpm --  {}", e.getMessage(), e);
        }

    }//trasformaTrackingCpm

    /**
     * ============================================================================================================
     **/

    public void gestisciBlacklisted() {
        try {

            // trovo tutti i tracking con read == false
            Map<String, Integer> mappa = new HashMap<>();
            CpmBusiness.getUnreadBlacklisted().stream().filter(cmpDTO -> cmpDTO.getRefferal() != null).forEach(cpm -> {
                // gestisco calcolatore
                Integer num = mappa.get(cpm.getRefferal());
                if (num == null) num = 0;
                if (cpm.getRefferal().length() < 5) {
                    // cerco da cpc
                    List<CpmDTO> ips = CpmBusiness.findByIp24HoursBefore(cpm.getIp(), cpm.getDate()).stream().collect(Collectors.toList());
                    // prendo ultimo   isp
                    for (CpmDTO dto : ips)
                        if (StringUtils.isNotBlank(dto.getRefferal())) dto.setRefferal(dto.getRefferal());
                }
                mappa.put(cpm.getRefferal(), num + 1);
                // setto a gestito
                CpmBusiness.setRead(cpm.getId());
            });

            mappa.forEach((x, aLong) -> {
                // prendo reffereal e lo leggo
                Refferal refferal = referralService.decodificaReferral(x);
                if (refferal != null && refferal.getCampaignId() != null && !Objects.isNull(refferal.getAffiliateId())) {

                    CampaignDTO campaignDTO = campaignBusiness.findByIdAdminNull(refferal.getCampaignId());
                    if (campaignDTO != null) {

                        // setta transazione
                        TransactionCPMBusiness.BaseCreateRequest transaction = new TransactionCPMBusiness.BaseCreateRequest();
                        transaction.setAffiliateId(refferal.getAffiliateId());
                        transaction.setCampaignId(refferal.getCampaignId());
                        transaction.setChannelId(refferal.getChannelId());
                        transaction.setDateTime(LocalDateTime.now().withMinute(0).withSecond(0).withNano(0).minusMinutes(1));
                        transaction.setMediaId(refferal.getMediaId());
                        transaction.setApproved(false);
                        transaction.setPayoutPresent(false);
                        transaction.setImpressionNumber(Long.valueOf(aLong));

                        // associo a wallet
                        Long affiliateID = refferal.getAffiliateId();
                        if (affiliateID != null) {
                            transaction.setWalletId(walletRepository.findByAffiliateId(affiliateID).getId());
                        }


                        // trovo revenue
                        RevenueFactor rf = revenueFactorBusiness.getbyIdCampaignAndDictionrayId(refferal.getCampaignId(), 50L);
                        if (rf != null && rf.getId() != null) {
                            transaction.setRevenueId(rf.getId());
                        } else {
                            transaction.setRevenueId(3L);
                        }

                        // gesione commisione
                        Double commVal = 0D;
                        AffiliateChannelCommissionCampaignBusiness.Filter req = new AffiliateChannelCommissionCampaignBusiness.Filter();
                        req.setAffiliateId(refferal.getAffiliateId());
                        req.setChannelId(refferal.getChannelId());
                        req.setCampaignId(refferal.getCampaignId());
                        req.setCommissionDicId(50L);
                        req.setBlocked(false);
                        AffiliateChannelCommissionCampaignDTO acccFirst = affiliateChannelCommissionCampaignBusiness.search(req).stream().findFirst().orElse(null);
                        if (acccFirst != null) {
                            commVal = acccFirst.getCommissionValue();
                            transaction.setCommissionId(acccFirst.getCommissionId());
                        } else {
                            transaction.setCommissionId(0L);
                        }

                        transaction.setValue(commVal * aLong);

                        //setto rifiutato
                        transaction.setStatusId(74L);
                        // setto blacklisted
                        transaction.setDictionaryId(70L);


                        // creo la transazione
                        TransactionCPMDTO tcpm = transactionCPMBusiness.createCpm(transaction);
                        log.trace(">>> CREATO TRANSAZIONE :::: CPM :::: {}", tcpm.getId());
                    }

                }// refferal not null
            });

        } catch (Exception e) {
            log.error("Eccezione Scheduler Cpm --  {}", e.getMessage(), e);
        }

    }//gestisciBlacklisted

}