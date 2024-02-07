package it.cleverad.engine.scheduled.manage;

import it.cleverad.engine.business.*;
import it.cleverad.engine.config.model.Refferal;
import it.cleverad.engine.persistence.model.service.RevenueFactor;
import it.cleverad.engine.persistence.repository.service.WalletRepository;
import it.cleverad.engine.service.ReferralService;
import it.cleverad.engine.web.dto.AffiliateBudgetDTO;
import it.cleverad.engine.web.dto.AffiliateChannelCommissionCampaignDTO;
import it.cleverad.engine.web.dto.CampaignBudgetDTO;
import it.cleverad.engine.web.dto.CampaignDTO;
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
    CampaignBudgetBusiness campaignBudgetBusiness;
    @Autowired
    private CpsBusiness cpsBusiness;
    @Autowired
    private TransactionCPSBusiness transactionCPSBusiness;
    @Autowired
    private WalletRepository walletRepository;
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

    @Scheduled(cron = "0 0 4 * * ?")
    public void trasformaTrackingCPS() {
        try {
            // trovo uttti i tracking con read == false
            cpsBusiness.getUnread().stream().filter(cpsDTO -> StringUtils.isNotBlank(cpsDTO.getRefferal())).forEach(cpsDTO -> {

                // prendo reffereal e lo leggo
                Refferal refferal = referralService.decodificaReferral(cpsDTO.getRefferal());
                log.info("CPS :: {} - {}", cpsDTO, refferal);

                // setta transazione
                TransactionCPSBusiness.BaseCreateRequest transaction = new TransactionCPSBusiness.BaseCreateRequest();
                transaction.setAffiliateId(refferal.getAffiliateId());
                transaction.setCampaignId(refferal.getCampaignId());
                transaction.setChannelId(refferal.getChannelId());
                transaction.setMediaId(refferal.getMediaId());
                transaction.setDateTime(cpsDTO.getDate());
                transaction.setApproved(true);
                transaction.setAgent(cpsDTO.getAgent());
                transaction.setIp(cpsDTO.getIp());
                transaction.setData(cpsDTO.getData());
                transaction.setMediaId(refferal.getMediaId());

                // controlla data scadneza camapgna
                CampaignDTO campaignDTO = campaignBusiness.findById(refferal.getCampaignId());
                LocalDate endDate = campaignDTO.getEndDate();
                if (endDate.isBefore(LocalDate.now())) {
                    // setto a campagna scaduta
                    transaction.setDictionaryId(42L);
                } else {
                    transaction.setDictionaryId(49L);
                }

                // associo a wallet
                Long affiliateID = refferal.getAffiliateId();
                log.info("AFFILIATE >>>> {}", affiliateID);
                Long walletID;
                if (affiliateID != null) {
                    walletID = walletRepository.findByAffiliateId(affiliateID).getId();
                    transaction.setWalletId(walletID);
                } else {
                    walletID = null;
                }

                // trovo revenue
                RevenueFactor rf = revenueFactorBusiness.getbyIdCampaignAndDictionrayId(refferal.getCampaignId(), 51L);
                if (rf != null) {
                    transaction.setRevenueId(rf.getId());
                } else {
                    log.warn("Non trovato revenue factor di tipo 11 per campagna {} , setto default", refferal.getCampaignId());
                    transaction.setRevenueId(4L);
                }

                // gesione commisione
                Long commId = 3L;
                Double commVal = 0D;

                AffiliateChannelCommissionCampaignBusiness.Filter req = new AffiliateChannelCommissionCampaignBusiness.Filter();
                req.setAffiliateId(refferal.getAffiliateId());
                req.setChannelId(refferal.getChannelId());
                req.setCampaignId(refferal.getCampaignId());
                req.setCommissionDicId(51L);
                AffiliateChannelCommissionCampaignDTO acccFirst = affiliateChannelCommissionCampaignBusiness.search(req).stream().findFirst().get();

                if (acccFirst != null) {
                    commId = acccFirst.getCommissionId();
                    commVal = acccFirst.getCommissionValue();
                }

                Double totale = commVal * 1;
                transaction.setValue(totale);

                transaction.setCommissionId(commId);
                transaction.setClickNumber(Long.valueOf(1));

                // decremento budget Affiliato
                AffiliateBudgetDTO bb = affiliateBudgetBusiness.getByIdCampaignAndIdAffiliate(refferal.getCampaignId(), refferal.getAffiliateId()).stream().findFirst().orElse(null);
                if (bb != null && bb.getBudget() != null) {
                    if ((bb.getBudget() - totale) < 0) transaction.setDictionaryId(47L);
                }

                // Stato Budget Campagna
                CampaignBudgetDTO campBudget = campaignBudgetBusiness.searchByCampaignAndDate(refferal.getCampaignId(), transaction.getDateTime().toLocalDate()).stream().findFirst().orElse(null);
                Double budgetCampagna = campBudget.getBudgetErogato() - totale;
                // setto stato transazione a ovebudget editore se totale < 0
                if (budgetCampagna < 0) {
                    transaction.setDictionaryId(48L);
                }

                //setto pending
                transaction.setStatusId(72L);

                // creo la transazione
                transactionCPSBusiness.createCps(transaction);

                // setto a gestito
                cpsBusiness.setRead(cpsDTO.getId());

            });
        } catch (Exception e) {
            log.error("Eccezione Scheduler CPS --  {}", e.getMessage(), e);
        }
    }//trasformaTrackingCPS

}