package it.cleverad.engine.scheduled;

import it.cleverad.engine.business.*;
import it.cleverad.engine.config.model.Refferal;
import it.cleverad.engine.persistence.model.service.RevenueFactor;
import it.cleverad.engine.persistence.repository.service.WalletRepository;
import it.cleverad.engine.service.ReferralService;
import it.cleverad.engine.web.dto.*;
import lombok.extern.slf4j.Slf4j;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
public class ManageCPC {

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
    private CommissionBusiness commissionBusiness;

    //    @Scheduled(cron = "*/15 * * * * ?")
    @Scheduled(cron = "0 5 0 * * ?")
    @Async
    public void trasformaTrackingCPC() {
        //   log.info("trasformaTrackingCPC");
        try {

            // trovo tutti i tracking con read == false
            Map<String, Integer> mappa = new HashMap<>();
            Page<CpcDTO> last = cpcBusiness.getUnreadDayBefore();
            last.stream().filter(dto -> dto.getRefferal() != null).forEach(dto -> {
                // gestisco calcolatore
                Integer num = mappa.get(dto.getRefferal());
                if (num == null) num = 0;
                mappa.put(dto.getRefferal(), num + 1);
                // setto a gestito
                cpcBusiness.setRead(dto.getId());
            });

            mappa.forEach((s, aLong) -> {
                log.info("Gestisco trasformaTrackingCPC :: {}", aLong);

                // prendo reffereal e lo leggo
                Refferal refferal = referralService.decodificaReferral(s);
                log.info("CPC :: {} - {}", s, refferal);
                Long campaignId = refferal.getCampaignId();
                if (campaignId != null && !Objects.isNull(refferal.getAffiliateId())) {
                    // setta transazione
                    TransactionBusiness.BaseCreateRequest transaction = new TransactionBusiness.BaseCreateRequest();
                    transaction.setCampaignId(campaignId);

                    Long affiliateId = refferal.getAffiliateId();
                    if (!Objects.isNull(affiliateId))
                        transaction.setAffiliateId(affiliateId);
                    Long channelId = refferal.getChannelId();
                    if (channelId != null)
                        transaction.setChannelId(channelId);
                    Long mediaId = refferal.getMediaId();
                    if (mediaId != null)
                        transaction.setMediaId(mediaId);

                    transaction.setDateTime(LocalDate.now().minusDays(1).atStartOfDay());
                    transaction.setApproved(true);

                    // controlla data scadneza camapgna
                    CampaignDTO campaignDTO = campaignBusiness.findByIdAdmin(campaignId);
                    if (campaignDTO.getEndDate().isBefore(LocalDate.now())) {
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
                    if (rf != null && rf.getId() != null) transaction.setRevenueId(rf.getId());

                    // gesione commisione
                    Long commId = null;
                    Double commVal = 0D;

                    AffiliateChannelCommissionCampaignBusiness.Filter req = new AffiliateChannelCommissionCampaignBusiness.Filter();
                    req.setAffiliateId(affiliateId);
                    req.setChannelId(channelId);
                    req.setCampaignId(campaignId);
                    req.setCommissionDicId(50L);
                    AffiliateChannelCommissionCampaignDTO acccFirst = affiliateChannelCommissionCampaignBusiness.search(req).stream().findFirst().orElse(null);

                    if (acccFirst != null) {
                        commId = acccFirst.getCommissionId();
                        commVal = acccFirst.getCommissionValue();
                    } else {
                        log.info("ACCC VUOTO");
                        CommissionBusiness.Filter filt = new CommissionBusiness.Filter();
                        filt.setCampaignId(campaignDTO.getId());
                        filt.setDictionaryId(10L);
                        CommissionDTO commission = commissionBusiness.search(filt).stream().findFirst().orElse(null);
                        commId = commission != null ? commission.getId() : null;
                        commVal = commission != null ? Double.valueOf(commission.getValue()) : 0;
                    }

                    if (commId != null) {
                        transaction.setCommissionId(commId);
                        log.info("setto commissione :: " + commId);
                    }

                    // calcolo valore
                    Double totale = commVal * aLong;
                    transaction.setValue(totale);
                    transaction.setClickNumber(Long.valueOf(aLong));

                    // incemento valore
                    if (walletID != null) walletBusiness.incement(walletID, totale);

                    // decremento budget Affiliato
                    BudgetDTO bb = budgetBusiness.getByIdCampaignAndIdAffiliate(campaignId, affiliateId).stream().findFirst().orElse(null);
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

                    //if (affiliateChannelCommissionCampaign.getCommission().getDueDate().isBefore(LocalDate.now())) {
                    // commissione scaduta
                    //rr.setDictionaryId(49L);
                    //}

                    // creo la transazione
                    TransactionCPCDTO tcpc = transactionBusiness.createCpc(transaction);
                    log.info("CREATO TRANSAZIONE CPC " + tcpc.getId());
                }
            });

        } catch (Exception e) {
            log.error("Eccezione Scheduler CPC --  {}", e.getMessage(), e);
        }

    }//trasformaTrackingCPC

}
