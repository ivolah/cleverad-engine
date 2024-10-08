package it.cleverad.engine.scheduled.manage;

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
import it.cleverad.engine.web.dto.AffiliateBudgetDTO;
import it.cleverad.engine.web.dto.AffiliateChannelCommissionCampaignDTO;
import it.cleverad.engine.web.dto.CampaignBudgetDTO;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ManageCPC {

    @Autowired
    CampaignBudgetBusiness campaignBudgetBusiness;

    /*
     * ============================================================================================================
     **/

    @Autowired
    private CpcBusiness cpcBusiness;
    @Autowired
    private TransactionCPCBusiness transactionCPCBusiness;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private AffiliateBudgetBusiness affiliateBudgetBusiness;
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

    /*
     * ============================================================================================================
     **/

    @Scheduled(cron = "3 */3 * * * ?")
    @Async
    public void gestiusciTransazioni() {

        // Setto a  Blacklisted + Multiple i click mulipli

        //TODO logica non va bene perchè dovrei lavorqre sempre per affiliato e campagna

        List<ClickMultipli> listaDaDisabilitare = cpcBusiness.getListaClickMultipliDaDisabilitare(LocalDate.now(), LocalDate.now(), null, null);
        // giro settaggio click multipli
        listaDaDisabilitare.stream().forEach(clickMultipli -> {
            log.trace("Disabilito {} :: {}", clickMultipli.getId(), clickMultipli.getTotale());
            Cpc cccp = repository.findById(clickMultipli.getId()).orElseThrow(() -> new ElementCleveradException("Cpc", clickMultipli.getId()));
            cccp.setRead(false);
            cccp.setBlacklisted(true);
            cccp.setMultiple(true);
            repository.save(cccp);
        });

        // cerco gli ip vicini

        // GESTICO I CLICK E CREO TRANSAZIONE
        trasformaTrackingCPC();

        // GESTICO I CLICK BLACKLISTED
        gestisciBlacklisted();

    }

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
                        transaction.setClickNumber(Long.valueOf(numer));
                        transaction.setAgent("");

                        Long affiliateId = refferal.getAffiliateId();
                        if (!Objects.isNull(affiliateId)) transaction.setAffiliateId(affiliateId);
                        Long channelId = refferal.getChannelId();
                        if (channelId != null) transaction.setChannelId(channelId);
                        Long mediaId = refferal.getMediaId();
                        if (mediaId != null) transaction.setMediaId(mediaId);

                        transaction.setDateTime(LocalDateTime.now().withMinute(0).withSecond(0).withNano(0).minusMinutes(1));
                        transaction.setApproved(true);

                        Boolean scaduta = false;
                        if (campaign.getEndDate().isBefore(LocalDate.now())) {
                            // setto a campagna scaduta
                            transaction.setDictionaryId(49L);
                            scaduta = true;
                        } else {
                            transaction.setDictionaryId(42L);
                        }

                        if (!campaign.getStatus()) {
                            // setto a campagna scaduta
                            transaction.setDictionaryId(49L);
                            scaduta = true;
                        }

                        // associo a wallet
                        Long walletID;
                        if (affiliateId != null) {
                            walletID = walletRepository.findByAffiliateId(affiliateId).getId();
                            transaction.setWalletId(walletID);
                        }

                        // gesione commisione
                        Double commVal = 0D;

                        if (scaduta) {
                            transaction.setRevenueId(1L);
                            transaction.setCommissionId(0L);
                            transaction.setStatusId(74L); // rigettato
                            transaction.setValue(0D);
                            transaction.setDictionaryId(49L);
                        } else {

                            // trovo revenue
                            RevenueFactor rf = revenueFactorBusiness.getbyIdCampaignAndDictionrayId(campaignId, 10L);
                            if (rf != null && rf.getId() != null) {
                                transaction.setRevenueId(rf.getId());
                            } else {
                                log.trace("Non trovato revenue factor di tipo 10 per campagna {}, setto default", campaignId);
                                transaction.setRevenueId(1L);
                            }

                            //trovo commissione
                            AffiliateChannelCommissionCampaignBusiness.Filter req = new AffiliateChannelCommissionCampaignBusiness.Filter();
                            req.setAffiliateId(affiliateId);
                            req.setChannelId(channelId);
                            req.setCampaignId(campaignId);
                            req.setCommissionDicId(10L);
                            req.setBlocked(false);

                            AffiliateChannelCommissionCampaignDTO accc = affiliateChannelCommissionCampaignBusiness.search(req).stream().findFirst().orElse(null);
                            if (accc != null) {
                                commVal = accc.getCommissionValue();
                                transaction.setCommissionId(accc.getCommissionId());
                            } else {
                                log.trace("Non trovato Commission di tipo 10 per campagna {}, setto default", campaignId);
                                transaction.setCommissionId(0L);
                            }

                            // calcolo valore
                            Double totale = commVal * numer;
                            transaction.setValue(totale);

                            // incemento valore scheduled

                            // decremento budget Affiliato
                            AffiliateBudgetDTO bb = affiliateBudgetBusiness.getByIdCampaignAndIdAffiliate(campaignId, affiliateId).stream().findFirst().orElse(null);
                            // setto stato transazione a ovebudget editore se totale < 0
                            if (bb != null && bb.getBudget() != null && ((bb.getBudget() - totale) < 0)) {
                                transaction.setDictionaryId(47L);
                            }

                            // Stato Budget Campagna
                            CampaignBudgetDTO campBudget = campaignBudgetBusiness.searchByCampaignAndDate(refferal.getCampaignId(), transaction.getDateTime().toLocalDate()).stream().findFirst().orElse(null);
                            if (campBudget != null && campBudget.getBudgetErogato() != null) {
                                Double budgetCampagna = campBudget.getBudgetErogato() - totale;
                                // setto stato transazione a ovebudget editore se totale < 0
                                if (budgetCampagna < 0) {
                                    transaction.setDictionaryId(48L);
                                }
                            }

                            // commissione scaduta
                            if (accc != null && accc.getCommissionDueDate() != null && (accc.getCommissionDueDate().isBefore(LocalDate.now()))) {
                                transaction.setDictionaryId(49L);
                            }

                            transaction.setStatusId(72L);//setto pending
                        }

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

    /*
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