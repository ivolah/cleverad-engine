package it.cleverad.engine.scheduled;

import it.cleverad.engine.business.*;
import it.cleverad.engine.config.model.Refferal;
import it.cleverad.engine.persistence.model.service.Campaign;
import it.cleverad.engine.persistence.model.service.ClickMultipli;
import it.cleverad.engine.persistence.model.service.RevenueFactor;
import it.cleverad.engine.persistence.model.tracking.Cpc;
import it.cleverad.engine.persistence.repository.service.CampaignRepository;
import it.cleverad.engine.persistence.repository.service.WalletRepository;
import it.cleverad.engine.persistence.repository.tracking.CpcRepository;
import it.cleverad.engine.service.ReferralService;
import it.cleverad.engine.web.dto.AffiliateChannelCommissionCampaignDTO;
import it.cleverad.engine.web.dto.BudgetDTO;
import it.cleverad.engine.web.dto.TransactionCPCDTO;
import it.cleverad.engine.web.dto.tracking.CpcDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.decimal4j.util.DoubleRounder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ManageCPC {

    @Autowired
    CampaignBudgetBusiness campaignBudgetBusiness;
    @Autowired
    private CpcBusiness cpcBusiness;
    @Autowired
    private TransactionCPCBusiness transactionCPCBusiness;
    @Autowired
    private WalletRepository walletRepository;
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
    private CpcRepository repository;
    @Autowired
    private CampaignRepository campaignRepository;

    /**
     * ============================================================================================================
     **/

    @Scheduled(cron = "5 */5 * * * ?")
    @Async
    public void gestiusciTransazioni() {

        // Setto a  Blacklisted i click mulipli
        List<ClickMultipli> listaDaDisabilitare = cpcBusiness.getListaClickMultipliDaDisabilitare(LocalDate.now(), LocalDate.now());
        // giro settaggio click multipli
        listaDaDisabilitare.stream().forEach(clickMultipli -> {
            // log.info("Disabilito {} :: {}", clickMultipli.getId(), clickMultipli.getTotale());
            Cpc cccp = repository.findById(clickMultipli.getId()).orElseThrow(() -> new ElementCleveradException("Cpc", clickMultipli.getId()));
            cccp.setRead(false);
            cccp.setBlacklisted(true);
            repository.save(cccp);
        });

        // GESTICO I CLICK E CREO TRANSAZIONE
        trasformaTrackingCPC();

        // GESTICO I CLICK BLACKLISTED
        gestisciBlacklisted();

    }

    /**
     * ============================================================================================================
     **/

    public void trasformaTrackingCPC() {

        try {
            // trovo tutti i tracking cpc con read == false
            Map<String, Integer> mappa = new HashMap<>();

            Page<CpcDTO> day = cpcBusiness.getUnreadDayNotBlackilset();
            log.trace("CPC TOT NOT BLACKLISTED {}", day.getTotalElements());
            // RECUPERO REFFERAL + NUMERO TOTALE
            day.stream().filter(dto -> dto.getRefferal() != null).forEach(dto -> {

                // gestisco calcolatore
                Integer num = mappa.get(dto.getRefferal());
                if (num == null) num = 0;

                if (dto.getRefferal().length() < 5) {
                    log.trace("Referral on solo Campaign Id :: {}", dto.getRefferal());
                    // cerco da cpc
                    List<CpcDTO> ips = cpcBusiness.findByIp24HoursBefore(dto.getIp(), dto.getDate(), dto.getRefferal()).stream().collect(Collectors.toList());

                    // prendo ultimo ipp
                    for (CpcDTO cpcDTO : ips)
                        if (StringUtils.isNotBlank(cpcDTO.getRefferal())) dto.setRefferal(cpcDTO.getRefferal());
                    log.trace("Nuovo refferal :: {} ", dto.getRefferal());
                }

                mappa.put(dto.getRefferal(), num + 1);

                // setto a read
                cpcBusiness.setRead(dto.getId());

                // aggiorno dati CPC
                Cpc cccp = repository.findById(dto.getId()).orElseThrow(() -> new ElementCleveradException("Cpc", dto.getId()));
                if (dto.getRefferal().equals("{{refferalId}}")) {
                    log.info(" <<<< VUOTO >>>>>> " + dto.getRefferal());
                    cccp.setRefferal("");
                } else {
                    Refferal refferal = referralService.decodificaReferral(dto.getRefferal());
                    if (refferal != null && refferal.getMediaId() != null) {
                        cccp.setMediaId(refferal.getMediaId());
                    }
                    if (refferal != null && refferal.getCampaignId() != null) {
                        cccp.setCampaignId(refferal.getCampaignId());
                    }
                    if (refferal != null && refferal.getAffiliateId() != null) {
                        cccp.setAffiliateId(refferal.getAffiliateId());
                    }
                    if (refferal != null && refferal.getChannelId() != null) {
                        cccp.setChannelId(refferal.getChannelId());
                    }
                    if (refferal != null && refferal.getTargetId() != null) {
                        cccp.setTargetId(refferal.getTargetId());
                    }
                }
                repository.save(cccp);

            });

            mappa.forEach((ref, numer) -> {

                Refferal refferal = referralService.decodificaReferral(ref);
                if (refferal != null && refferal.getCampaignId() != null && !Objects.isNull(refferal.getAffiliateId())) {
                    Long campaignId = refferal.getCampaignId();

                    // controlla data scadneza camapgna
                    Campaign campaign = campaignRepository.findById(campaignId).orElse(null);
                    if (campaign != null) {
                        // setta transazione
                        TransactionCPCBusiness.BaseCreateRequest transaction = new TransactionCPCBusiness.BaseCreateRequest();
                        transaction.setCampaignId(campaignId);
                        transaction.setPayoutPresent(false);

                        Long affiliateId = refferal.getAffiliateId();
                        if (!Objects.isNull(affiliateId)) transaction.setAffiliateId(affiliateId);
                        Long channelId = refferal.getChannelId();
                        if (channelId != null) transaction.setChannelId(channelId);
                        Long mediaId = refferal.getMediaId();
                        if (mediaId != null) transaction.setMediaId(mediaId);

                        transaction.setDateTime(LocalDateTime.now().withMinute(0).withSecond(0).withNano(0).minusMinutes(1));
                        transaction.setApproved(true);

                        if (campaign.getEndDate().isBefore(LocalDate.now())) {
                            // setto a campagna scaduta
                            transaction.setDictionaryId(49L);
                        } else {
                            transaction.setDictionaryId(42L);
                        }

                        // associo a wallet
                        Long walletID = null;
                        if (affiliateId != null) {
                            walletID = walletRepository.findByAffiliateId(affiliateId).getId();
                            transaction.setWalletId(walletID);
                        }

                        // trovo revenue
                        RevenueFactor rf = revenueFactorBusiness.getbyIdCampaignAndDictionrayId(campaignId, 10L);
                        if (rf != null && rf.getId() != null) {
                            transaction.setRevenueId(rf.getId());
                        } else {
                            log.trace("Non trovato revenue factor di tipo 10 per campagna {}, setto default", campaignId);
                            transaction.setRevenueId(1L);
                        }

                        // gesione commisione
                        Double commVal = 0D;

                        AffiliateChannelCommissionCampaignBusiness.Filter req = new AffiliateChannelCommissionCampaignBusiness.Filter();
                        req.setAffiliateId(affiliateId);
                        req.setChannelId(channelId);
                        req.setCampaignId(campaignId);
                        req.setCommissionDicId(10L);

                        AffiliateChannelCommissionCampaignDTO accc = affiliateChannelCommissionCampaignBusiness.search(req).stream().findFirst().orElse(null);
                        if (accc != null) {
                            // log.info(accc.getCommissionId() + " " + accc.getCommissionValue());
                            commVal = accc.getCommissionValue();
                            transaction.setCommissionId(accc.getCommissionId());
                        } else {
                            log.trace("Non trovato Commission di tipo 10 per campagna {}, setto default", campaignId);
                            transaction.setCommissionId(0L);
                        }

                        // calcolo valore
                        Double totale = commVal * numer;
                        transaction.setValue(totale);
                        transaction.setClickNumber(Long.valueOf(numer));

                        // incemento valore
                        if (walletID != null && totale > 0D) walletBusiness.incement(walletID, totale);

                        // decremento budget Affiliato
                        BudgetDTO bb = budgetBusiness.getByIdCampaignAndIdAffiliate(campaignId, affiliateId).stream().findFirst().orElse(null);
                        if (bb != null && bb.getBudget() != null) {
                            Double totBudgetDecrementato = bb.getBudget() - totale;
                            budgetBusiness.updateBudget(bb.getId(), totBudgetDecrementato);

                            // decremento cap affiliato
                            Integer cap = bb.getCap() - numer;
                            budgetBusiness.updateCap(bb.getId(), cap);

                            // setto stato transazione a ovebudget editore se totale < 0
                            if (totBudgetDecrementato < 0) {
                                transaction.setDictionaryId(47L);
                            }
                        }

                        // decremento budget Campagna
                        Double budgetCampagna = campaign.getBudget() - totale;
                        campaignBusiness.updateBudget(campaign.getId(), budgetCampagna);

                        // setto stato transazione a ovebudget editore se totale < 0
                        if (budgetCampagna < 0) {
                            transaction.setDictionaryId(48L);
                        }

                        if (accc != null && accc.getCommissionDueDate() != null) {
                            // commissione scaduta
                            if (accc.getCommissionDueDate().isBefore(LocalDate.now())) {
                                transaction.setDictionaryId(49L);
                            }
                        }

//                        if (totale > 0) {
//                            // trovo CampaignBudget
//                            CampaignBudget cb = campaignBudgetBusiness.findByCampaignIdAndDate(campaignId, LocalDateTime.now());
//                            if (cb != null) {
//                                //incremento budget erogato
//                                campaignBudgetBusiness.incrementoBudgetErogato(cb.getId(), totale);
//                                // incremento cap
//                                campaignBudgetBusiness.incrementoCapErogato(cb.getId(), numer);
//                            }
//                        }

                        transaction.setAgent("");
                        //setto pending
                        transaction.setStatusId(72L);

                        // creo la transazione
                        TransactionCPCDTO tcpc = transactionCPCBusiness.createCpc(transaction);
                        log.trace(">>> CREATO TRANSAZIONE :::: CPC :::: {} -- {} -- {}", tcpc.getId(), ref, refferal);
                    } else {
                        log.info(">>> CAMPAGNA NULLO :: {}", campaignId);
                    }
                }

            });
        } catch (Exception e) {
            log.error("Eccezione Scheduler CPC --  {}", e.getMessage(), e);
        }

    }//trasformaTrackingCPC

    /**
     * ============================================================================================================
     **/

    public void gestisciBlacklisted() {

        try {
            Map<String, Integer> mappa = new HashMap<>();
            cpcBusiness.getUnreadBlacklisted().stream().filter(dto -> dto.getRefferal() != null).forEach(dto -> {

                // gestisco calcolatore
                Integer num = mappa.get(dto.getRefferal());
                if (num == null) num = 0;

                if (dto.getRefferal().length() < 5) {
                    // cerco da cpc
                    List<CpcDTO> ips = cpcBusiness.findByIp24HoursBefore(dto.getIp(), dto.getDate(), dto.getRefferal()).stream().collect(Collectors.toList());
                    // prendo ultimo ipp
                    for (CpcDTO cpcDTO : ips)
                        if (StringUtils.isNotBlank(cpcDTO.getRefferal())) dto.setRefferal(cpcDTO.getRefferal());
                }

                mappa.put(dto.getRefferal(), num + 1);

                // setto a read
                cpcBusiness.setRead(dto.getId());

                // aggiorno dati CPC
                Cpc cccp = repository.findById(dto.getId()).orElseThrow(() -> new ElementCleveradException("Cpc", dto.getId()));
                Refferal refferal = referralService.decodificaReferral(dto.getRefferal());
                if (refferal != null && refferal.getMediaId() != null) cccp.setMediaId(refferal.getMediaId());
                if (refferal != null && refferal.getCampaignId() != null) cccp.setCampaignId(refferal.getCampaignId());
                if (refferal != null && refferal.getAffiliateId() != null)
                    cccp.setAffiliateId(refferal.getAffiliateId());
                if (refferal != null && refferal.getChannelId() != null) cccp.setChannelId(refferal.getChannelId());
                if (refferal != null && refferal.getTargetId() != null) cccp.setTargetId(refferal.getTargetId());
                repository.save(cccp);

            });

            mappa.forEach((ref, numer) -> {

                Refferal refferal = referralService.decodificaReferral(ref);
                if (refferal != null && refferal.getCampaignId() != null && !Objects.isNull(refferal.getAffiliateId())) {
                    Long campaignId = refferal.getCampaignId();

                    Campaign campaign = campaignRepository.findById(campaignId).orElse(null);
                    if (campaign != null) {

                        // setta transazione
                        TransactionCPCBusiness.BaseCreateRequest transaction = new TransactionCPCBusiness.BaseCreateRequest();
                        transaction.setCampaignId(campaignId);
                        transaction.setPayoutPresent(false);

                        Long affiliateId = refferal.getAffiliateId();
                        if (!Objects.isNull(affiliateId)) transaction.setAffiliateId(affiliateId);
                        Long channelId = refferal.getChannelId();
                        if (channelId != null) transaction.setChannelId(channelId);
                        Long mediaId = refferal.getMediaId();
                        if (mediaId != null) transaction.setMediaId(mediaId);

                        transaction.setDateTime(LocalDateTime.now().withMinute(0).withSecond(0).minusMinutes(1));
                        transaction.setApproved(Boolean.FALSE);

                        // associo a wallet
                        if (affiliateId != null) {
                            transaction.setWalletId(walletRepository.findByAffiliateId(affiliateId).getId());
                        }

                        // trovo revenue
                        RevenueFactor rf = revenueFactorBusiness.getbyIdCampaignAndDictionrayId(campaignId, 10L);
                        if (rf != null && rf.getId() != null) {
                            transaction.setRevenueId(rf.getId());
                        } else {
                            transaction.setRevenueId(1L);
                        }

                        // gesione commisione
                        Double commVal = 0D;

                        AffiliateChannelCommissionCampaignBusiness.Filter req = new AffiliateChannelCommissionCampaignBusiness.Filter();
                        req.setAffiliateId(affiliateId);
                        req.setChannelId(channelId);
                        req.setCampaignId(campaignId);
                        req.setCommissionDicId(10L);
                        AffiliateChannelCommissionCampaignDTO accc = affiliateChannelCommissionCampaignBusiness.search(req).stream().findFirst().orElse(null);
                        if (accc != null) {
                            commVal = accc.getCommissionValue();
                            transaction.setCommissionId(accc.getCommissionId());
                        } else {
                            transaction.setCommissionId(0L);
                        }

                        // calcolo valore
                        transaction.setValue(DoubleRounder.round(commVal * numer, 2));
                        transaction.setClickNumber(Long.valueOf(numer));

                        transaction.setAgent("");

                        //setto rifiutato
                        transaction.setStatusId(74L);
                        // setto blacklisted
                        transaction.setDictionaryId(70L);

                        // creo la transazione
                        TransactionCPCDTO tcpc = transactionCPCBusiness.createCpc(transaction);
                        log.trace(">>>BLACKLISTED CPC :::: {} -- {} -- {}", tcpc.getId(), ref, refferal);
                    } else {
                        log.info(">>> BLACKLISTED CAMPAGNA NULLO :: {}", campaignId);
                    }
                }
            });
        } catch (Exception e) {
            log.error("BLACKLISTED Eccezione Scheduler CPC --  {}", e.getMessage(), e);
        }

    }//trasformaTrackingCPC

}