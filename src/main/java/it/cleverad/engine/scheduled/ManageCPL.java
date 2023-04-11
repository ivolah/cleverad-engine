package it.cleverad.engine.scheduled;

import it.cleverad.engine.business.*;
import it.cleverad.engine.config.model.Refferal;
import it.cleverad.engine.persistence.model.service.AffiliateChannelCommissionCampaign;
import it.cleverad.engine.persistence.repository.service.AffiliateChannelCommissionCampaignRepository;
import it.cleverad.engine.persistence.repository.service.WalletRepository;
import it.cleverad.engine.service.RefferalService;
import it.cleverad.engine.web.dto.BudgetDTO;
import it.cleverad.engine.web.dto.CampaignDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
public class ManageCPL {

    @Autowired
    private CplBusiness cplBusiness;
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
    @Scheduled(cron = "0 0 1 * * ?")
    public void trasformaTrackingCPL() {
        try {
            // trovo uttti i tracking con read == false
            cplBusiness.getUnreadDayBefore().stream().filter(cplDTO -> StringUtils.isNotBlank(cplDTO.getRefferal())).forEach(cplDTO -> {

                // prendo reffereal e lo leggo
                Refferal refferal = refferalService.decodificaRefferal(cplDTO.getRefferal());
                log.info("CPL :: {} - {}", cplDTO, refferal);

                // setta transazione
                TransactionBusiness.BaseCreateRequest rr = new TransactionBusiness.BaseCreateRequest();
                rr.setAffiliateId(refferal.getAffiliateId());
                rr.setCampaignId(refferal.getCampaignId());
                rr.setChannelId(refferal.getChannelId());
                rr.setMediaId(refferal.getMediaId());
                rr.setDateTime(cplDTO.getDate());
                rr.setApproved(true);
                rr.setAgent(cplDTO.getAgent());
                rr.setIp(cplDTO.getIp());
                rr.setData(cplDTO.getData());
                rr.setMediaId(refferal.getMediaId());

                // controlla data scadneza camapgna
                CampaignDTO campaignDTO = campaignBusiness.findByIdAdmin(refferal.getCampaignId());
                LocalDate endDate = campaignDTO.getEndDate();
                if (endDate.isBefore(LocalDate.now())) {
                    // setto a campagna scaduta
                    rr.setDictionaryId(49L);
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

                // gesione commisione
                List<AffiliateChannelCommissionCampaign> accc = affiliateChannelCommissionCampaignRepository.findByAffiliateIdAndChannelIdAndCampaignId(refferal.getAffiliateId(), refferal.getChannelId(), refferal.getCampaignId());
                accc.stream().forEach(affiliateChannelCommissionCampaign -> {
                    if (affiliateChannelCommissionCampaign.getCommission().getDictionary().getName().equals("CPL")) {
                        rr.setCommissionId(affiliateChannelCommissionCampaign.getCommission().getId());

                        Double totale = Double.valueOf(affiliateChannelCommissionCampaign.getCommission().getValue()) * 1;
                        rr.setValue(totale);
                        rr.setLeadNumber(Long.valueOf(1));

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

                        // creo la transazione
                        transactionBusiness.createCpl(rr);
                    }
                });

                // setto a gestito
                cplBusiness.setRead(cplDTO.getId());

            });
        } catch (Exception e) {
            log.error("Eccezione Scheduler CPL --  {}", e.getMessage(), e);
        }
    }//trasformaTrackingCPL

}
