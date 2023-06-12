package it.cleverad.engine.scheduled;

import it.cleverad.engine.business.*;
import it.cleverad.engine.config.model.Refferal;
import it.cleverad.engine.persistence.model.service.RevenueFactor;
import it.cleverad.engine.persistence.repository.service.WalletRepository;
import it.cleverad.engine.service.ReferralService;
import it.cleverad.engine.web.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

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
    private RevenueFactorBusiness revenueFactorBusiness;
    @Autowired
    private AffiliateChannelCommissionCampaignBusiness affiliateChannelCommissionCampaignBusiness;
    @Autowired
    private ReferralService referralService;
    @Autowired
    private CommissionBusiness commissionBusiness;
    @Autowired
    private CpcBusiness cpcBusiness;

    @Async
    @Scheduled(cron = "0 5 0/1 * * ?")
    //@Scheduled(cron = "*/15 * * * * ?")
    public void trasformaTrackingCPL() {
        try {
            //   cplBusiness.getUnreadDayBefore().stream().filter(cplDTO -> StringUtils.isNotBlank(cplDTO.getRefferal())).forEach(cplDTO -> {
            cplBusiness.getUnreadOneHourBefore().stream().filter(cplDTO -> StringUtils.isNotBlank(cplDTO.getRefferal())).forEach(cplDTO -> {

                if (cplDTO.getRefferal().length() < 6) {
                    log.trace("Referral con solo Campaign Id :: {}", cplDTO.getRefferal());
                    // cerco da cpc
                    List<CpcDTO> ips = cpcBusiness.findByIp24HoursBefore(cplDTO.getIp(), cplDTO.getDate(), cplDTO.getRefferal()).stream().collect(Collectors.toList());
                    // prendo ultimo ip
                    for (CpcDTO dto : ips)
                        if (StringUtils.isNotBlank(dto.getRefferal())) cplDTO.setRefferal(dto.getRefferal());
                    log.info("Nuovo refferal :: {} ", cplDTO.getRefferal());
                }

                // prendo reffereal e lo leggo
                Refferal refferal = referralService.decodificaReferral(cplDTO.getRefferal());
                log.info(">>>> T-CPL :: {} - {}", cplDTO, refferal);

                if (refferal != null && refferal.getAffiliateId() != null) {
                    // setta transazione
                    TransactionBusiness.BaseCreateRequest transaction = new TransactionBusiness.BaseCreateRequest();
                    transaction.setAffiliateId(refferal.getAffiliateId());
                    transaction.setCampaignId(refferal.getCampaignId());
                    transaction.setChannelId(refferal.getChannelId());
                    transaction.setMediaId(refferal.getMediaId());
                    transaction.setDateTime(cplDTO.getDate());
                    transaction.setApproved(true);

                    if (StringUtils.isNotBlank(cplDTO.getAgent()))
                        transaction.setAgent(cplDTO.getAgent());
                    else
                        transaction.setAgent("");

                    transaction.setIp(cplDTO.getIp());
                    transaction.setData(cplDTO.getData());
                    transaction.setMediaId(refferal.getMediaId());

                    // controlla data scadneza camapgna
                  try {
                      CampaignDTO campaignDTO = campaignBusiness.findByIdAdmin(refferal.getCampaignId());
                      LocalDate endDate = campaignDTO.getEndDate();
                      if (endDate.isBefore(LocalDate.now())) {
                          // setto a campagna scaduta
                          transaction.setDictionaryId(49L);
                      } else {
                          //setto pending
                          transaction.setDictionaryId(42L);
                      }

                      // associo a wallet
                      Long affiliateID = refferal.getAffiliateId();

                      Long walletID = null;
                      if (affiliateID != null) {
                          walletID = walletRepository.findByAffiliateId(affiliateID).getId();
                          transaction.setWalletId(walletID);
                      }

                      // trovo revenue
                      RevenueFactor rf = revenueFactorBusiness.getbyIdCampaignAndDictionrayId(refferal.getCampaignId(), 11L);
                      if (rf != null) {
                          transaction.setRevenueId(rf.getId());
                      } else {
                          log.warn("Non trovato revenue factor di tipo 11 per campagna {} , setto default", refferal.getCampaignId());
                          transaction.setRevenueId(2L);
                      }

                      // gesione commisione
                      Double commVal = 0D;

                      AffiliateChannelCommissionCampaignBusiness.Filter req = new AffiliateChannelCommissionCampaignBusiness.Filter();
                      req.setAffiliateId(refferal.getAffiliateId());
                      req.setChannelId(refferal.getChannelId());
                      req.setCampaignId(refferal.getCampaignId());
                      req.setCommissionDicId(11L);
                      AffiliateChannelCommissionCampaignDTO acccFirst = affiliateChannelCommissionCampaignBusiness.search(req).stream().findFirst().orElse(null);

                      if (acccFirst != null) {
                          log.info(acccFirst.getCommissionId() + " " + acccFirst.getCommissionValue());
                          commVal = acccFirst.getCommissionValue();
                          transaction.setCommissionId(acccFirst.getCommissionId());
                      } else {
                          log.warn("Non trovato Commission di tipo 10 per campagna {}, setto default", refferal.getCampaignId());
                          transaction.setCommissionId(0L);
                      }

                      Double totale = commVal * 1;
                      transaction.setValue(totale);
                      transaction.setLeadNumber(Long.valueOf(1));

                      // incemento valore
                      if (walletID != null && totale > 0D) walletBusiness.incement(walletID, totale);

                      // decremento budget Affiliato
                      BudgetDTO bb = budgetBusiness.getByIdCampaignAndIdAffiliate(refferal.getCampaignId(), refferal.getAffiliateId()).stream().findFirst().orElse(null);
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

                      // creo la transazione
                      TransactionCPLDTO cpl = transactionBusiness.createCpl(transaction);
                      log.info(">>> CREATO TRANSAZIONE :::: CPL :::: {} \n", cpl.getId());

                      // setto a gestito
                      cplBusiness.setRead(cplDTO.getId());
                  }catch (Exception ecc){
                      log.error("ECCEZIONE CPL :> ", ecc);
                  }
                }// creo solo se ho affiliate
            });
        } catch (Exception e) {
            log.error("Eccezione Scheduler CPL --  {}", e.getMessage(), e);
        }
    }//trasformaTrackingCPL

}
