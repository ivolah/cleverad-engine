package it.cleverad.engine.scheduled;

import it.cleverad.engine.business.*;
import it.cleverad.engine.config.model.Refferal;
import it.cleverad.engine.persistence.model.service.RevenueFactor;
import it.cleverad.engine.persistence.repository.service.WalletRepository;
import it.cleverad.engine.service.ReferralService;
import it.cleverad.engine.web.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class Consolida {

    @Autowired
    private TransactionBusiness transactionBusiness;
    @Autowired
    private CampaignBusiness campaignBusiness;
    @Autowired
    private it.cleverad.engine.business.CpmBusiness CpmBusiness;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private WalletBusiness walletBusiness;
    @Autowired
    private BudgetBusiness budgetBusiness;
    @Autowired
    private AffiliateChannelCommissionCampaignBusiness affiliateChannelCommissionCampaignBusiness;
    @Autowired
    private RevenueFactorBusiness revenueFactorBusiness;
    @Autowired
    private ReferralService referralService;


    @Scheduled(cron = "0 59 * * * ?")
    public void consolidaCPC() {
        log.info("\n\n\nCONSOLIDA CPC ");
        TransactionBusiness.Filter request = new TransactionBusiness.Filter();
        LocalDateTime oraSpaccata = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        request.setDateTimeFrom(oraSpaccata.toLocalDate().atStartOfDay());
        request.setDateTimeTo(oraSpaccata);
        Page<TransactionCPCDTO> cpcs = transactionBusiness.searchCpc(request, PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("id"))));

        List<Triple> triples = new ArrayList<>();
        for (TransactionCPCDTO tcpm : cpcs) {
            Triple<Long, Long, Long> triple = new ImmutableTriple<>(tcpm.getCampaignId(), tcpm.getAffiliateId(), tcpm.getChannelId());
            triples.add(triple);
        }
        List<Triple> listWithoutDuplicates = triples.stream().distinct().collect(Collectors.toList());

        for (Triple ttt : listWithoutDuplicates) {
            log.info("{} - {} - {}", ttt.getLeft(), ttt.getMiddle(), ttt.getRight());

            request = new TransactionBusiness.Filter();
            oraSpaccata = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
            request.setDateTimeFrom(oraSpaccata.toLocalDate().atStartOfDay());
            request.setDateTimeTo(oraSpaccata);
            request.setCampaignId((Long) ttt.getLeft());
            request.setAffiliateId((Long) ttt.getMiddle());
            request.setChannelId((Long) ttt.getRight());
            cpcs = transactionBusiness.searchCpc(request, PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("id"))));

            Long totaleClick = 0L;
            Double value = 0D;
            Long walletId = null;
            Long mediaId = null;
            Long dictionaryId = null;
            Long revenueId = null;
            Long commissionId = null;
            for (TransactionCPCDTO tcpc : cpcs) {
                totaleClick += tcpc.getClickNumber();
                value += tcpc.getValue();
                walletId = tcpc.getWalletId();
                mediaId = tcpc.getMediaId();
                dictionaryId = tcpc.getDictionaryId();
                revenueId = tcpc.getRevenueId();
                commissionId = tcpc.getCommissionId();
                log.info("TRANSAZIONE CPC ID :: {} : {} :: {}", tcpc.getId(), tcpc.getClickNumber(), tcpc.getDateTime());
                transactionBusiness.deleteInterno(tcpc.getId(), "CPC");
            }
            if (totaleClick > 0) {
                log.info("Click {}  x valore {}\n\n", totaleClick, value);
                TransactionBusiness.BaseCreateRequest bReq = new TransactionBusiness.BaseCreateRequest();
                bReq.setClickNumber(totaleClick);
                bReq.setValue(value);
                bReq.setCampaignId((Long) ttt.getLeft());
                bReq.setAffiliateId((Long) ttt.getMiddle());
                bReq.setChannelId((Long) ttt.getRight());
                bReq.setCommissionId(commissionId);
                bReq.setApproved(true);
                bReq.setDateTime(oraSpaccata.minusMinutes(1));
                bReq.setMediaId(mediaId);
                bReq.setDictionaryId(dictionaryId);
                bReq.setRevenueId(revenueId);
                bReq.setWalletId(walletId);
                bReq.setAgent("");
                TransactionCPCDTO cpc = transactionBusiness.createCpc(bReq);
            }

        }//tripletta

    }//trasformaTrackingCPC

    // @Scheduled(cron = "30 59 * * * ?")
    public void consolidaCPM() {
        log.info("\n\n\nCONSOLIDA CPM ");

        TransactionBusiness.Filter request = new TransactionBusiness.Filter();
        LocalDateTime oraSpaccata = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
        request.setDateTimeFrom(oraSpaccata.toLocalDate().atStartOfDay());
        request.setDateTimeTo(oraSpaccata);
        Page<TransactionCPMDTO> cpcM = transactionBusiness.searchCpm(request, PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("id"))));

        List<Triple> triples = new ArrayList<>();
        for (TransactionCPMDTO tcpm : cpcM) {
            Triple<Long, Long, Long> triple = new ImmutableTriple<>(tcpm.getCampaignId(), tcpm.getAffiliateId(), tcpm.getChannelId());
            triples.add(triple);
        }
        List<Triple> listWithoutDuplicates = triples.stream().distinct().collect(Collectors.toList());

        for (Triple ttt : listWithoutDuplicates) {
            log.info("{} - {} - {}", ttt.getLeft(), ttt.getMiddle(), ttt.getRight());

            request = new TransactionBusiness.Filter();
            oraSpaccata = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
            request.setDateTimeFrom(oraSpaccata.toLocalDate().atStartOfDay());
            request.setDateTimeTo(oraSpaccata);
            request.setCampaignId((Long) ttt.getLeft());
            request.setAffiliateId((Long) ttt.getMiddle());
            request.setChannelId((Long) ttt.getRight());
            cpcM = transactionBusiness.searchCpm(request, PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("id"))));

            Long impressionNumber = 0L;
            Double value = 0D;
            Long walletId = null;
            Long mediaId = null;
            Long dictionaryId = null;
            Long revenueId = null;
            Long commissionId = null;
            for (TransactionCPMDTO tcpm : cpcM) {
                impressionNumber += tcpm.getImpressionNumber();
                value += tcpm.getValue();
                walletId = tcpm.getWalletId();
                mediaId = tcpm.getMediaId();
                dictionaryId = tcpm.getDictionaryId();
                revenueId = tcpm.getRevenueId();
                commissionId = tcpm.getCommissionId();
                log.info("TRANSAZIONE CPM ID :: {} : {} :: {}", tcpm.getId(), tcpm.getImpressionNumber(), tcpm.getDateTime());
                transactionBusiness.deleteInterno(tcpm.getId(), "CPM");
            }
            if (impressionNumber > 0) {
                log.info("impression {}  x valore {}\n\n", impressionNumber, value);

                TransactionBusiness.BaseCreateRequest bReq = new TransactionBusiness.BaseCreateRequest();
                bReq.setImpressionNumber(impressionNumber);
                bReq.setValue(value);
                bReq.setCampaignId((Long) ttt.getLeft());
                bReq.setAffiliateId((Long) ttt.getMiddle());
                bReq.setChannelId((Long) ttt.getRight());
                bReq.setCommissionId(commissionId);
                bReq.setApproved(true);
                bReq.setDateTime(oraSpaccata.minusMinutes(1));
                bReq.setMediaId(mediaId);
                bReq.setDictionaryId(dictionaryId);
                bReq.setRevenueId(revenueId);
                bReq.setWalletId(walletId);
                bReq.setAgent("");
                transactionBusiness.createCpm(bReq);
            }


        }//tripletta
    }//trasformaTrackingCPC

    @Async
    // @Scheduled(cron = "0 30 1 * * ?")
    // @Scheduled(cron = "0 */2 * * * ?")
    public void trasformaTrackingCPM() {

        try {

            Map<String, Integer> mappa = new HashMap<>();
            Page<CpmDTO> tutti = CpmBusiness.getAllDayBefore();

            tutti.stream().filter(CpmDTO -> CpmDTO.getRefferal() != null).forEach(cpm -> {
                // gestisco calcolatore
                Integer num = mappa.get(cpm.getRefferal());
                if (num == null) num = 0;

                if (cpm.getRefferal().length() < 6) {
                    log.trace("Referral on solo Campaign Id :: {}", cpm.getRefferal());
                    // cerco da cpc
                    List<CpmDTO> ips = CpmBusiness.findByIp24HoursBefore(cpm.getIp(), cpm.getDate()).stream().collect(Collectors.toList());
                    // prendo ultimo   isp
                    for (CpmDTO dto : ips)
                        if (StringUtils.isNotBlank(dto.getRefferal())) dto.setRefferal(dto.getRefferal());
                    log.warn("Nuovo refferal :: {} ", cpm.getRefferal());
                }
                mappa.put(cpm.getRefferal(), num + 1);
            });

            mappa.forEach((x, aLong) -> {
                // prendo reffereal e lo leggo
                Refferal refferal = referralService.decodificaReferral(x);
                log.info(">>>> T-CPM :: {} -> {} - {}", aLong, x, refferal);
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
                            } else {
                                log.trace("Non trovato revenue factor di tipo 10 per campagna {}, setto default", refferal.getCampaignId());
                                transaction.setRevenueId(3L);
                            }
                        }

                        // gesione commisione
                        Double commVal = 0D;

                        AffiliateChannelCommissionCampaignBusiness.Filter req = new AffiliateChannelCommissionCampaignBusiness.Filter();
                        req.setAffiliateId(refferal.getAffiliateId());
                        req.setChannelId(refferal.getChannelId());
                        req.setCampaignId(refferal.getCampaignId());
                        req.setCommissionDicId(50L);
                        AffiliateChannelCommissionCampaignDTO acccFirst = affiliateChannelCommissionCampaignBusiness.search(req).stream().findFirst().orElse(null);

                        if (acccFirst != null) {
                            log.info(acccFirst.getCommissionId() + " " + acccFirst.getCommissionValue());
                            commVal = acccFirst.getCommissionValue();
                            transaction.setCommissionId(acccFirst.getCommissionId());
                        } else {
                            log.trace("Non trovato Commission di tipo 10 per campagna {}, setto default", refferal.getCampaignId());
                            transaction.setCommissionId(0L);
                        }

                        Double totale = commVal * aLong;
                        transaction.setValue(totale);
                        transaction.setImpressionNumber(Long.valueOf(aLong));

                        BudgetDTO bb = budgetBusiness.getByIdCampaignAndIdAffiliate(refferal.getCampaignId(), refferal.getAffiliateId()).stream().findFirst().orElse(null);
                        if (bb != null) {
                            Double totBudgetDecrementato = bb.getBudget() - totale;

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
                            // setto stato transazione a ovebudget editore se totale < 0
                            if (budgetCampagna < 0) {
                                transaction.setDictionaryId(48L);
                            }
                        }

                        // creo la transazione
//                        TransactionCPMDTO tcpm = transactionBusiness.createCpm(transaction);
//                        log.info("CREATO TRANSAZIONE PULITA :::: CPM :::: {} \n", tcpm.getId());
                    }

                }// refferal not null
            });


            // trovo tutte le transazioni CCPM di ieri e le cancello           }

            TransactionBusiness.Filter request = new TransactionBusiness.Filter();
            LocalDateTime oraSpaccata = LocalDateTime.now().withMinute(0).withSecond(0).withNano(0);
            request.setDateTimeFrom(LocalDate.now().minusDays(1).atStartOfDay());
            request.setDateTimeTo(LocalDate.now().atStartOfDay());
            Page<TransactionCPMDTO> cpcM = transactionBusiness.searchCpm(request, PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("id"))));
            log.info("TOTALE CCPM DA CANCCELLAT ::{} ", cpcM.stream().collect(Collectors.toList()).size());
         //   cpcM.stream().forEach(transactionCPMDTO -> transactionBusiness.deleteInterno(transactionCPMDTO.getId(), "CPM"));

        } catch (Exception e) {
            log.error("Eccezione Scheduler Cpm --  {}", e.getMessage(), e);
        }

    }//trasformaTrackingCpm


}
