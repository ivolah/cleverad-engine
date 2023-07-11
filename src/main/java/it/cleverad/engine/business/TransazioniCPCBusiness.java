package it.cleverad.engine.business;

import it.cleverad.engine.config.model.Refferal;
import it.cleverad.engine.persistence.model.service.Campaign;
import it.cleverad.engine.persistence.model.service.RevenueFactor;
import it.cleverad.engine.persistence.model.tracking.Cpc;
import it.cleverad.engine.persistence.repository.service.CampaignRepository;
import it.cleverad.engine.persistence.repository.service.WalletRepository;
import it.cleverad.engine.persistence.repository.tracking.CpcRepository;
import it.cleverad.engine.service.ReferralService;
import it.cleverad.engine.web.dto.AffiliateChannelCommissionCampaignDTO;
import it.cleverad.engine.web.dto.BudgetDTO;
import it.cleverad.engine.web.dto.CpcDTO;
import it.cleverad.engine.web.dto.TransactionCPCDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional
public class TransazioniCPCBusiness {

    @Autowired
    private CpcBusiness cpcBusiness;
    @Autowired
    private TransactionBusiness transactionBusiness;
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

    public void rigeneraCPC(Integer anno, Integer mese, Integer giorno) {

        LocalDate dataDaGestire = LocalDate.of(anno, mese, giorno);
        LocalDateTime inizio = dataDaGestire.atStartOfDay();
        LocalDateTime fine = LocalDateTime.now().withMinute(0).withSecond(0).withDayOfYear(dataDaGestire.getDayOfYear());

        // cancello le transazioni
        TransactionBusiness.Filter request = new TransactionBusiness.Filter();
        request.setDateTimeFrom(inizio);
        request.setDateTimeTo(fine);
        Page<TransactionCPCDTO> cpcs = transactionBusiness.searchCpc(request, PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("id"))));

        for (TransactionCPCDTO tcpc : cpcs) {
            log.trace("CANCELLO PER RIGENERA TRANSAZIONE CPC ID :: {} : {} :: {}", tcpc.getId(), tcpc.getClickNumber(), tcpc.getDateTime());
            transactionBusiness.delete(tcpc.getId(), "CPC");
        }


        //RIGENERO
        try {
            Map<String, Integer> mappa = new HashMap<>();
            Page<CpcDTO> day = cpcBusiness.getAllByDay(dataDaGestire);
            day.stream().filter(dto -> dto.getRefferal() != null).forEach(dto -> {

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

            log.info(" >> MAPPA >> " + mappa.size());
            mappa.forEach((s, integer) -> {
                log.info("R  ::  " + s + "  ==  " + integer);
            });

            mappa.forEach((ref, numer) -> {

                Refferal refferal = referralService.decodificaReferral(ref);
                log.info(">>>> T-CPC :: {} -> {} - {}", numer, ref, refferal);
                if (refferal != null && refferal.getCampaignId() != null && !Objects.isNull(refferal.getAffiliateId())) {

                    Long campaignId = refferal.getCampaignId();
                    // setta transazione
                    TransactionBusiness.BaseCreateRequest transaction = new TransactionBusiness.BaseCreateRequest();
                    transaction.setCampaignId(campaignId);
                    transaction.setPayoutPresent(false);

                    if (!Objects.isNull(refferal.getAffiliateId()))
                        transaction.setAffiliateId(refferal.getAffiliateId());
                    if (refferal.getChannelId() != null) transaction.setChannelId(refferal.getChannelId());
                    if (refferal.getMediaId() != null) transaction.setMediaId(refferal.getMediaId());

                    transaction.setDateTime(dataDaGestire.atTime(1, 1, 1));
                    transaction.setApproved(true);

                    // controlla data scadneza camapgna
                    Campaign campaign = campaignRepository.findById(campaignId).orElse(null);
                    if (campaign != null) {

                        if (campaign.getEndDate().isBefore(dataDaGestire)) {
                            // setto a campagna scaduta
                            transaction.setDictionaryId(49L);
                        } else {
                            transaction.setDictionaryId(42L);
                        }

                        // associo a wallet
                        Long walletID = null;
                        if (refferal.getAffiliateId() != null) {
                            walletID = walletRepository.findByAffiliateId(refferal.getAffiliateId()).getId();
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
                        req.setAffiliateId(refferal.getAffiliateId());
                        req.setChannelId(refferal.getChannelId());
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
                        BudgetDTO bb = budgetBusiness.getByIdCampaignAndIdAffiliate(campaignId, refferal.getAffiliateId()).stream().findFirst().orElse(null);
                        if (bb != null) {
                            Double totBudgetDecrementato = bb.getBudget() - totale;
                            budgetBusiness.updateBudget(bb.getId(), totBudgetDecrementato);

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
                            if (accc.getCommissionDueDate().isBefore(dataDaGestire)) {
                                transaction.setDictionaryId(49L);
                            }
                        }

                        transaction.setAgent(" ");

                        // creo la transazione
                        TransactionCPCDTO tcpc = transactionBusiness.createCpc(transaction);
                        log.info(">>> RIGENERO CPC :::: {} -- {} -- {}", tcpc.getId(), ref, refferal);
                    } else {
                        log.info(">>> RIGENERO Camp nulla :: {}", campaignId);
                    }
                }
            });
        } catch (Exception e) {
            log.error("Rigenero Eccezione Scheduler CPC --  {}", e.getMessage(), e);
        }

    }


}
