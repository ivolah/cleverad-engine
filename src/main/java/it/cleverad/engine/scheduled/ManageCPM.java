package it.cleverad.engine.scheduled;

import it.cleverad.engine.business.*;
import it.cleverad.engine.config.model.Refferal;
import it.cleverad.engine.persistence.model.service.AffiliateChannelCommissionCampaign;
import it.cleverad.engine.persistence.repository.service.AffiliateChannelCommissionCampaignRepository;
import it.cleverad.engine.persistence.repository.service.WalletRepository;
import it.cleverad.engine.service.RefferalService;
import it.cleverad.engine.web.dto.BudgetDTO;
import it.cleverad.engine.web.dto.CampaignDTO;
import it.cleverad.engine.web.dto.CpmDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
    private AffiliateChannelCommissionCampaignRepository affiliateChannelCommissionCampaignRepository;

    @Autowired
    private RefferalService refferalService;

    @Async
    @Scheduled(cron = "1 1 1 * * ?")
    public void trasformaTrackingCPM() {
        try {
            // trovo tutti i tracking con read == false
            Map<String, Integer> mappa = new HashMap<>();
            Page<CpmDTO> last = CpmBusiness.getUnreadDayBefore();
            last.stream().filter(CpmDTO -> CpmDTO.getRefferal() != null).forEach(CpmDTO -> {
                // gestisco calcolatore
                Integer num = mappa.get(CpmDTO.getRefferal());
                if (num == null) num = 0;
                mappa.put(CpmDTO.getRefferal(), num + 1);
                // setto a gestito
                CpmBusiness.setRead(CpmDTO.getId());
            });

            mappa.forEach((s, aLong) -> {
                log.info("Gestisco trasformaTrackingCpm ID {}", aLong);
                // prendo reffereal e lo leggo
                Refferal refferal = refferalService.decodificaRefferal(s);
                log.info("Cpm :: {} - {}", s, refferal);

                // setta transazione
                TransactionBusiness.BaseCreateRequest rr = new TransactionBusiness.BaseCreateRequest();
                rr.setAffiliateId(refferal.getAffiliateId());
                rr.setCampaignId(refferal.getCampaignId());
                rr.setChannelId(refferal.getChannelId());
                rr.setDateTime(LocalDate.now().minusDays(1).atStartOfDay());
                rr.setMediaId(refferal.getMediaId());
                rr.setApproved(true);
                rr.setMediaId(refferal.getMediaId());

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
                Long walletID;
                if (affiliateID != null) {
                    walletID = walletRepository.findByAffiliateId(affiliateID).getId();
                    rr.setWalletId(walletID);
                } else {
                    walletID = null;
                }

                // gesione commisione
                List<AffiliateChannelCommissionCampaign> accc = affiliateChannelCommissionCampaignRepository.findByAffiliateIdAndChannelIdAndCampaignId(refferal.getAffiliateId(), refferal.getChannelId(), refferal.getCampaignId());
                accc.stream().forEach(affiliateChannelCommissionCampaign -> {
                    if (affiliateChannelCommissionCampaign.getCommission().getDictionary().getName().toUpperCase(Locale.ROOT).equals("CPM")) {
                        Long cid = affiliateChannelCommissionCampaign.getCommission().getId();
                        log.info("CHECK >>>" + cid + "<<<" + refferal.getAffiliateId() + "-" + refferal.getChannelId() + "-" + refferal.getCampaignId());
                        rr.setCommissionId(cid);
                        Double totale = Double.valueOf(affiliateChannelCommissionCampaign.getCommission().getValue()) * aLong;
                        rr.setValue(totale);
                        rr.setImpressionNumber(Long.valueOf(aLong));
                        log.info("TOT " + totale + " - " + aLong);

                        // incemento valore
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
                            Double budgetCampagna = campaignDTO.getBudget() - totale;
                            campaignBusiness.updateBudget(campaignDTO.getId(), budgetCampagna);

                            // setto stato transazione a ovebudget editore se totale < 0
                            if (budgetCampagna < 0) {
                                rr.setDictionaryId(48L);
                            }
                        }

                        log.info("Creo Trans CPM");
                        // creo la transazione
                        transactionBusiness.createCpm(rr);
                    }
                });
            });

        } catch (Exception e) {
            log.error("Eccezione Scheduler Cpm --  {}", e.getMessage(), e);
        }

    }//trasformaTrackingCpm

}
