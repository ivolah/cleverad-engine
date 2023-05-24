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
    @Scheduled(cron = "2 2 0/1 * * ?")
    //@Scheduled(cron = "*/8 * * * * ?")
    public void trasformaTrackingCPM() {
        try {

            // trovo tutti i tracking con read == false
            Map<String, Integer> mappa = new HashMap<>();
            Page<CpmDTO> last = CpmBusiness.getUnreadHourBefore();
            last.stream().filter(CpmDTO -> CpmDTO.getRefferal() != null).forEach(cpm -> {

                // gestisco calcolatore
                Integer num = mappa.get(cpm.getRefferal());
                if (num == null) num = 0;

                if (cpm.getRefferal().length() < 5) {
                    log.trace("Referral on solo Campaign Id :: {}", cpm.getRefferal());
                    // cerco da cpc
                    List<CpmDTO> ips = CpmBusiness.findByIp24HoursBefore(cpm.getIp(), cpm.getDate()).stream().collect(Collectors.toList());
                    // prendo ultimo   isp
                    for (CpmDTO dto : ips)
                        if (StringUtils.isNotBlank(dto.getRefferal())) dto.setRefferal(dto.getRefferal());
                    log.warn("Nuovo refferal :: {} ", cpm.getRefferal());
                }
                mappa.put(cpm.getRefferal(), num + 1);

                // setto a gestito
                CpmBusiness.setRead(cpm.getId());
            });

            mappa.forEach((x, aLong) -> {
                // prendo reffereal e lo leggo
                Refferal refferal = referralService.decodificaReferral(x);
                log.info(">>>> T-CPC :: {} -> {} - {}", aLong, x, refferal);
                if (refferal != null && refferal.getCampaignId() != null && !Objects.isNull(refferal.getAffiliateId())) {

                    // setta transazione
                    TransactionBusiness.BaseCreateRequest transaction = new TransactionBusiness.BaseCreateRequest();
                    transaction.setAffiliateId(refferal.getAffiliateId());
                    transaction.setCampaignId(refferal.getCampaignId());
                    transaction.setChannelId(refferal.getChannelId());
                    transaction.setDateTime(LocalDateTime.now().withMinute(0).withSecond(0).withNano(0));
                    transaction.setMediaId(refferal.getMediaId());
                    transaction.setApproved(true);

                    // controlla data scadneza camapgna
                    CampaignDTO campaignDTO = campaignBusiness.findByIdAdminNull(refferal.getCampaignId());
                    if (campaignDTO != null) {
                        LocalDate endDate = campaignDTO.getEndDate();
                        if (endDate.isBefore(LocalDate.now())) {
                            // setto a campagna scaduta
                            transaction.setDictionaryId(42L);
                        } else {
                            transaction.setDictionaryId(49L);
                        }

                        // associo a wallet
                        Long affiliateID = refferal.getAffiliateId();

                        Long walletID = null;
                        if (affiliateID != null) {
                            walletID = walletRepository.findByAffiliateId(affiliateID).getId();
                            transaction.setWalletId(walletID);
                        }

                        // trovo revenue
                        if (refferal.getCampaignId() != null) {
                            RevenueFactor rf = revenueFactorBusiness.getbyIdCampaignAndDictionrayId(refferal.getCampaignId(), 50L);
                            if (rf != null && rf.getId() != null) {
                                transaction.setRevenueId(rf.getId());
                            }
                            else
                            {
                                log.warn("Non trovato revenue factor di tipo 10 per campagna {}, setto default", refferal.getCampaignId());
                                transaction.setRevenueId(3L);
                            }
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
                            CommissionBusiness.Filter filt = new CommissionBusiness.Filter();
                            filt.setCampaignId(campaignDTO.getId());
                            filt.setDictionaryId(50L);
                            CommissionDTO commission = commissionBusiness.search(filt).stream().findFirst().orElse(null);
                            commId = commission != null ? commission.getId() : null;
                            commVal = commission != null ? Double.valueOf(commission.getValue()) : 0;
                        }

                        if (commId != null)
                            transaction.setCommissionId(commId);

                        Double totale = commVal * aLong;
                        transaction.setValue(totale);
                        transaction.setImpressionNumber(Long.valueOf(aLong));

                        // incemento valore
                        if (walletID != null) walletBusiness.incement(walletID, totale);

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
                        RevenueFactor rff = revenueFactorBusiness.getbyIdCampaignAndDictionrayId(refferal.getCampaignId(), 50L);
                        if (rff != null && rff.getRevenue() != null) {
                            Double totaleDaDecurtare = revenueFactorBusiness.getbyIdCampaignAndDictionrayId(refferal.getCampaignId(), 50L).getRevenue() * aLong;
                            Double budgetCampagna = campaignDTO.getBudget() - totaleDaDecurtare;
                            campaignBusiness.updateBudget(campaignDTO.getId(), budgetCampagna);

                            // setto stato transazione a ovebudget editore se totale < 0
                            if (budgetCampagna < 0) {
                                transaction.setDictionaryId(48L);
                            }
                        }

                        // creo la transazione
                        TransactionCPMDTO tcpm = transactionBusiness.createCpm(transaction);
                        log.info("CREATO TRANSAZIONE :::: CPM :::: {} \n", tcpm.getId());
                    }

                }// refferal not null
            });

        } catch (Exception e) {
            log.error("Eccezione Scheduler Cpm --  {}", e.getMessage(), e);
        }

    }//trasformaTrackingCpm

}
