package it.cleverad.engine.scheduled;

import it.cleverad.engine.business.*;
import it.cleverad.engine.config.model.Refferal;
import it.cleverad.engine.persistence.model.service.RevenueFactor;
import it.cleverad.engine.persistence.repository.service.WalletRepository;
import it.cleverad.engine.service.ReferralService;
import it.cleverad.engine.web.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class ManageCPM {

    @Autowired
    private CpmBusiness CpmBusiness;
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
    private AffiliateChannelCommissionCampaignBusiness affiliateChannelCommissionCampaignBusiness;
    @Autowired
    private RevenueFactorBusiness revenueFactorBusiness;
    @Autowired
    private ReferralService referralService;
    @Autowired
    private CommissionBusiness commissionBusiness;

    @Async
    @Scheduled(cron = "0 30 0 * * ?")
    //     @Scheduled(cron = "*/18 * * * * ?")
    public void trasformaTrackingCPM() {
        try {

            // trovo tutti i tracking con read == false
            Map<String, Integer> mappa = new HashMap<>();
            Page<CpmDTO> last = CpmBusiness.getUnreadDayBefore();
            last.stream().filter(CpmDTO -> CpmDTO.getRefferal() != null).forEach(cpm -> {
                // gestisco calcolatore
                Integer num = mappa.get(cpm.getRefferal());
                if (num == null) num = 0;
                mappa.put(cpm.getRefferal(), num + 1);
                // setto a gestito
                CpmBusiness.setRead(cpm.getId());
            });

            mappa.forEach((x, aLong) -> {
                log.info("Gestisco trasformaTrackingCpm ID {}", aLong);
                // prendo reffereal e lo leggo
                Refferal refferal = referralService.decodificaReferral(x);
                log.info("Cpm :: {} - {}", x, refferal);
                if (refferal != null) {

                    // setta transazione
                    TransactionBusiness.BaseCreateRequest rr = new TransactionBusiness.BaseCreateRequest();
                    rr.setAffiliateId(refferal.getAffiliateId());
                    rr.setCampaignId(refferal.getCampaignId());
                    rr.setChannelId(refferal.getChannelId());
                    rr.setDateTime(LocalDate.now().minusDays(1).atStartOfDay());
                    rr.setMediaId(refferal.getMediaId());
                    rr.setApproved(true);

                    // controlla data scadneza camapgna
                    CampaignDTO campaignDTO = campaignBusiness.findByIdAdmin(refferal.getCampaignId());
                    LocalDate endDate = campaignDTO.getEndDate();
                    if (endDate.isBefore(LocalDate.now())) {
                        // setto a campagna scaduta
                        rr.setDictionaryId(42L);
                    } else {
                        rr.setDictionaryId(49L);
                    }

                    // associo a wallet
                    Long affiliateID = refferal.getAffiliateId();

                    Long walletID = null;
                    if (affiliateID != null) {
                        walletID = walletRepository.findByAffiliateId(affiliateID).getId();
                        rr.setWalletId(walletID);
                    }

                    // trovo revenue
                    if (refferal.getCampaignId() != null) {
                        RevenueFactor rf = revenueFactorBusiness.getbyIdCampaignAndDictionrayId(refferal.getCampaignId(), 50L);
                        if (rf != null && rf.getId() != null)
                            rr.setRevenueId(rf.getId());
                    }

                    // gesione commisione
                    Long commId = null;
                    Double commVal = 0D;

                    AffiliateChannelCommissionCampaignBusiness.Filter req = new AffiliateChannelCommissionCampaignBusiness.Filter();
                    req.setAffiliateId(refferal.getAffiliateId());
                    req.setChannelId(refferal.getChannelId());
                    req.setCampaignId(refferal.getCampaignId());
                    req.setCommissionDicId(50L);
                    AffiliateChannelCommissionCampaignDTO acccFirst = affiliateChannelCommissionCampaignBusiness.search(req).stream().findFirst().orElse(null);

                    if (acccFirst != null) {
                        commId = acccFirst.getCommissionId();
                        commVal = acccFirst.getCommissionValue();
                    } else {
                        log.info("ACCCC VUOTO");
                        CommissionBusiness.Filter filt = new CommissionBusiness.Filter();
                        filt.setCampaignId(campaignDTO.getId());
                        filt.setDictionaryId(50L);
                        CommissionDTO commission = commissionBusiness.search(filt).stream().findFirst().orElse(null);
                        commId = commission != null ? commission.getId() : null;
                        commVal = commission != null ? Double.valueOf(commission.getValue()) : 0;
                    }

                    if (commId != null) {
                        rr.setCommissionId(commId);
                        log.info("setto commissione :: " + commId);
                    }

                    Double totale = commVal * aLong;
                    rr.setValue(totale);
                    rr.setImpressionNumber(Long.valueOf(aLong));
                    log.info("TOT " + totale + " - " + aLong);

                    // incemento valore
                    if (walletID != null)
                        walletBusiness.incement(walletID, totale);

                    // decremento budget Affiliato
                    BudgetDTO bb = budgetBusiness.getByIdCampaignAndIdAffiliate(refferal.getCampaignId(), refferal.getAffiliateId()).stream().findFirst().orElse(null);
                    if (bb != null) {
                        Double totBudgetDecrementato = bb.getBudget() - totale;
                        budgetBusiness.updateBudget(bb.getId(), totBudgetDecrementato);

                        // setto stato transazione a ovebudget editore se totale < 0
                        if (totBudgetDecrementato < 0) {
                            rr.setDictionaryId(47L);
                        }
                    }

                    // decremento budget Campagna
                    if (campaignDTO != null) {
                        RevenueFactor rff = revenueFactorBusiness.getbyIdCampaignAndDictionrayId(refferal.getCampaignId(), 50L);
                        if (rff != null && rff.getRevenue() != null) {
                            Double totaleDaDecurtare = revenueFactorBusiness.getbyIdCampaignAndDictionrayId(refferal.getCampaignId(), 50L).getRevenue() * aLong;
                            Double budgetCampagna = campaignDTO.getBudget() - totaleDaDecurtare;
                            campaignBusiness.updateBudget(campaignDTO.getId(), budgetCampagna);

                            // setto stato transazione a ovebudget editore se totale < 0
                            if (budgetCampagna < 0) {
                                rr.setDictionaryId(48L);
                            }
                        }
                    }

                    log.info("Creo Trans CPM");
                    // creo la transazione
                    transactionBusiness.createCpm(rr);

                }// refferal not null
            });

        } catch (Exception e) {
            log.error("Eccezione Scheduler Cpm --  {}", e.getMessage(), e);
        }

    }//trasformaTrackingCpm

}
