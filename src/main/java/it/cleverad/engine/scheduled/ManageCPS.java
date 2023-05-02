package it.cleverad.engine.scheduled;

import it.cleverad.engine.business.*;
import it.cleverad.engine.config.model.Refferal;
import it.cleverad.engine.persistence.model.service.AffiliateChannelCommissionCampaign;
import it.cleverad.engine.persistence.model.service.Commission;
import it.cleverad.engine.persistence.model.service.RevenueFactor;
import it.cleverad.engine.persistence.repository.service.AffiliateChannelCommissionCampaignRepository;
import it.cleverad.engine.persistence.repository.service.WalletRepository;
import it.cleverad.engine.service.RefferalService;
import it.cleverad.engine.web.dto.AffiliateChannelCommissionCampaignDTO;
import it.cleverad.engine.web.dto.BudgetDTO;
import it.cleverad.engine.web.dto.CampaignDTO;
import it.cleverad.engine.web.dto.CommissionDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
public class ManageCPS {

    @Autowired
    private CpsBusiness cpsBusiness;
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
    private RefferalService refferalService;
    @Autowired
    private CommissionBusiness commissionBusiness;

    @Scheduled(cron = "0 0 4 * * ?")
    public void trasformaTrackingCPS() {
        try {
            // trovo uttti i tracking con read == false
            cpsBusiness.getUnread().stream().filter(cpsDTO -> StringUtils.isNotBlank(cpsDTO.getRefferal())).forEach(cpsDTO -> {

                // prendo reffereal e lo leggo
                Refferal refferal = refferalService.decodificaRefferal(cpsDTO.getRefferal());
                log.info("CPS :: {} - {}", cpsDTO, refferal);

                // setta transazione
                TransactionBusiness.BaseCreateRequest rr = new TransactionBusiness.BaseCreateRequest();
                rr.setAffiliateId(refferal.getAffiliateId());
                rr.setCampaignId(refferal.getCampaignId());
                rr.setChannelId(refferal.getChannelId());
                rr.setMediaId(refferal.getMediaId());
                rr.setDateTime(cpsDTO.getDate());
                rr.setApproved(true);
                rr.setAgent(cpsDTO.getAgent());
                rr.setIp(cpsDTO.getIp());
                rr.setData(cpsDTO.getData());
                rr.setMediaId(refferal.getMediaId());

                // controlla data scadneza camapgna
                CampaignDTO campaignDTO = campaignBusiness.findById(refferal.getCampaignId());
                LocalDate endDate = campaignDTO.getEndDate();
                if (endDate.isBefore(LocalDate.now())) {
                    // setto a campagna scaduta
                    rr.setDictionaryId(42L);
                } else {
                    rr.setDictionaryId(42L);
                }

                // associo a wallet
                Long affiliateID = refferal.getAffiliateId();
                log.info("AFFILIATE >>>> {}", affiliateID);
                Long walletID;
                if (affiliateID != null) {
                    walletID = walletRepository.findByAffiliateId(affiliateID).getId();
                    rr.setWalletId(walletID);
                } else {
                    walletID = null;
                }

                // trovo revenue
                RevenueFactor rf = revenueFactorBusiness.getbyIdCampaignAndDictionrayId(refferal.getCampaignId(), 51L);
                rr.setRevenueId(rf.getId());

                // gesione commisione
                Long commId = null;
                Double commVal = 0D;

                AffiliateChannelCommissionCampaignBusiness.Filter req = new AffiliateChannelCommissionCampaignBusiness.Filter();
                req.setAffiliateId(refferal.getAffiliateId());
                req.setChannelId(refferal.getChannelId());
                req.setCampaignId(refferal.getCampaignId());
                req.setCommissionDicId(51L);
                AffiliateChannelCommissionCampaignDTO acccFirst = affiliateChannelCommissionCampaignBusiness.search(req).stream().findFirst().get();

                if (acccFirst != null) {
                    commId = acccFirst.getCommissionId();
                    commVal = Double.valueOf(acccFirst.getCommissionValue().replace(",", "."));
                } else {
                    log.info("ACCCC VUOTO");
                    CommissionBusiness.Filter filt = new CommissionBusiness.Filter();
                    filt.setCampaignId(campaignDTO.getId());
                    filt.setDictionaryId(51L);
                    CommissionDTO commission = commissionBusiness.search(filt).stream().findFirst().orElse(null);
                    commId = commission != null ? commission.getId() : null;
                    commVal = commission != null ? Double.valueOf(commission.getValue()) : 0;
                }

                Double totale = commVal * 1;
                rr.setValue(totale);

                rr.setCommissionId(commId);
                rr.setClickNumber(Long.valueOf(1));

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

                    CampaignBusiness.Filter filter = new CampaignBusiness.Filter();
                    filter.setBudget(budgetCampagna);
                    campaignBusiness.update(bb.getId(), filter);

                    // setto stato transazione a ovebudget editore se totale < 0
                    if (budgetCampagna < 0) {
                        rr.setDictionaryId(48L);
                    }
                }

                // creo la transazione
                transactionBusiness.createCps(rr);

                // setto a gestito
                cpsBusiness.setRead(cpsDTO.getId());

            });
        } catch (Exception e) {
            log.error("Eccezione Scheduler CPS --  {}", e.getMessage(), e);
        }
    }//trasformaTrackingCPS

}
