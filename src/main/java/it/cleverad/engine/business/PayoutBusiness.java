package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.service.Dictionary;
import it.cleverad.engine.persistence.model.service.*;
import it.cleverad.engine.persistence.repository.service.*;
import it.cleverad.engine.service.MailService;
import it.cleverad.engine.web.dto.DictionaryDTO;
import it.cleverad.engine.web.dto.PayoutDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import it.cleverad.engine.web.exception.PostgresDeleteCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.decimal4j.util.DoubleRounder;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional
public class PayoutBusiness {

    @Autowired
    private PayoutRepository repository;
    @Autowired
    private AffiliateRepository affiliateRepository;
    @Autowired
    private TransactionCPLRepository cplRepository;
    @Autowired
    private TransactionCPCRepository cpcRepository;
    @Autowired
    private TransactionCPSRepository cpsRepository;
    @Autowired
    private DictionaryBusiness dictionaryBusiness;
    @Autowired
    private DictionaryRepository dictionaryRepository;
    @Autowired
    private WalletBusiness walletBusiness;
    @Autowired
    private Mapper mapper;
    @Autowired
    private MailService mailService;

    /**
     * ============================================================================================================
     **/

    public List<Payout> create(BaseCreateRequest request) {

        String note = request.getNote();

        //Prendo tutti gli affiliati
        List<Long> affiliatesList = new ArrayList<>();
        request.getTransazioniCpc().stream().forEach(id -> {
            TransactionCPC transaction = cpcRepository.findById(id).orElseThrow(() -> new ElementCleveradException("Transaction CPC", id));
            affiliatesList.add(transaction.getAffiliate().getId());
        });
        request.getTransazioniCpl().stream().forEach(id -> {
            TransactionCPL transaction = cplRepository.findById(id).orElseThrow(() -> new ElementCleveradException("Transaction CPL", id));
            affiliatesList.add(transaction.getAffiliate().getId());
        });

        // faccio distinct e creo un pqyout vuoto per ognuno
        HashMap<Long, Long> affiliatoPayout = new HashMap<>();
        String finalNote = note;
        affiliatesList.stream().distinct().forEach(idAffiliate -> {
            Payout map = new Payout();
            Affiliate affiliate = affiliateRepository.findById(idAffiliate).orElseThrow(() -> new ElementCleveradException("Affiliate", idAffiliate));
            map.setAffiliate(affiliate);
            map.setData(LocalDate.now());
            map.setValuta("EUR");
            map.setCreationDate(LocalDateTime.now());
            map.setLastModificationDate(LocalDateTime.now());
            map.setTotale(0.0);
            map.setIva(0.0);
            map.setNote(finalNote);
            Dictionary dictionary = dictionaryRepository.findById(18L).orElseThrow(() -> new ElementCleveradException("Dictionary", 18L));
            map.setDictionary(dictionary);
            map.setDataScadenza(LocalDate.now().plusDays(60));
            map = repository.save(map);
            affiliatoPayout.put(idAffiliate, map.getId());
        });

        Set<Payout> listaPayout = new HashSet<>();

        // per ogni singola transazione
        request.getTransazioniCpc().forEach(id -> {
            TransactionCPC transaction = cpcRepository.findById(id).orElseThrow(() -> new ElementCleveradException("Transaction CPC", id));
            Long payoutId = affiliatoPayout.get(transaction.getAffiliate().getId());
            Payout payout = repository.findById(payoutId).orElseThrow(() -> new ElementCleveradException("PAYOUT CPC", payoutId));

            //aumento il valore
            Double totale = DoubleRounder.round(payout.getTotale() + transaction.getValue(), 2);

            //aggiorno payout
            payout.setTotale(totale);
            Payout pp = repository.save(payout);
            listaPayout.add(pp);

            // decermeto valore wallet
            walletBusiness.decrement(transaction.getWallet().getId(), totale);

            //aggiorno transazione e setto riferimento a payout
            transaction.setPayout(payout);
            transaction.setPayoutReference("Payout " + payoutId);
            transaction.setPayoutPresent(true);
            cpcRepository.save(transaction);
        });

        // per ogni singola transazione
        request.getTransazioniCpl().stream().forEach(id -> {
            TransactionCPL transaction = cplRepository.findById(id).orElseThrow(() -> new ElementCleveradException("Transaction CPL", id));
            Long payoutId = affiliatoPayout.get(transaction.getAffiliate().getId());
            Payout payout = repository.findById(payoutId).orElseThrow(() -> new ElementCleveradException("PAYOUT CPL", payoutId));

            //aumento il valore
            Double totale = DoubleRounder.round(payout.getTotale() + transaction.getValue(), 2);

            //aggiorno payout
            payout.setTotale(totale);
            Payout pp = repository.save(payout);
            listaPayout.add(pp);

            // decermeto valore wallet
            walletBusiness.decrement(transaction.getWallet().getId(), totale);

            //aggiorno transazione e setto riferimento a payout
            transaction.setPayout(payout);
            transaction.setPayoutReference("Payout " + payoutId);
            transaction.setPayoutPresent(true);
            cplRepository.save(transaction);
        });

        List<Payout> pys = new ArrayList<>(listaPayout);
        pys.forEach(payout -> payout.setIva(payout.getTotale() * 0.22));

        return pys;
    }

    public Page<PayoutDTO> createCps(List<Long> listaTransactions) {

        //prndo tutti gli affigliati
        List<Long> affiliatesList = new ArrayList<>();
        listaTransactions.stream().forEach(id -> {
            TransactionCPS transaction = cpsRepository.findById(id).orElseThrow(() -> new ElementCleveradException("Transaction CPS", id));
            affiliatesList.add(transaction.getAffiliate().getId());
        });

        // faccio distinct e creo un pqyout vuoto per ognuno
        HashMap<Long, Long> affiliatoPayout = new HashMap<>();
        affiliatesList.stream().distinct().forEach(idAffiliate -> {
            Payout map = new Payout();
            Affiliate affiliate = affiliateRepository.findById(idAffiliate).orElseThrow(() -> new ElementCleveradException("Affiliate", idAffiliate));
            map.setAffiliate(affiliate);
            map.setData(LocalDate.now());
            map.setValuta("EUR");
            map.setCreationDate(LocalDateTime.now());
            map.setLastModificationDate(LocalDateTime.now());
            map.setTotale(0.0);
            Dictionary dictionary = dictionaryRepository.findById(18L).orElseThrow(() -> new ElementCleveradException("Dictionary", 18L));
            map.setDictionary(dictionary);
            map = repository.save(map);
            affiliatoPayout.put(idAffiliate, map.getId());
        });

        Set<Payout> list = new HashSet<>();
        // per ogni singola transazione
        listaTransactions.stream().forEach(id -> {
            TransactionCPS transaction = cpsRepository.findById(id).orElseThrow(() -> new ElementCleveradException("Transaction CPs", id));
            Long payoutId = affiliatoPayout.get(transaction.getAffiliate().getId());
            Payout payout = repository.findById(payoutId).orElseThrow(() -> new ElementCleveradException("PAYOUT CPs", payoutId));

            //aumento il valore
            Double totale = DoubleRounder.round(payout.getTotale() + transaction.getValue(), 2);

            //aggiorno payout
            payout.setTotale(totale);
            Payout pp = repository.save(payout);
            list.add(pp);

            // decermeto valore wallet
            walletBusiness.decrement(transaction.getWallet().getId(), totale);

            //aggiorno transazione e setto riferimento a payout
            transaction.setPayout(payout);
            transaction.setPayoutReference("Payout " + payoutId);
            transaction.setPayoutPresent(true);
            cpsRepository.save(transaction);
        });

        Set<Payout> list2 = new HashSet<>();
        list.forEach(payout -> {
            list2.add(repository.findById(payout.getId()).orElseThrow(() -> new ElementCleveradException("PAYOUT CPL", payout.getId())));
        });

        Page<Payout> page = new PageImpl<>(list2.stream().distinct().collect(Collectors.toList()));
        return page.map(PayoutDTO::from);
    }

    // GET BY ID
    public PayoutDTO findById(Long id) {
        Payout payout = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Payout", id));
        return PayoutDTO.from(payout);
    }

    // DELETE BY ID
    public void delete(Long id) {
        try {
            Payout payout = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Payout", id));
            payout.getTransactionCPCS().forEach(transactionCPC -> {
                // rimuovo relazione
                TransactionCPC cpc = cpcRepository.findById(transactionCPC.getId()).orElseThrow(() -> new ElementCleveradException("Transaction CPC", transactionCPC.getId()));
                cpc.setPayout(null);
                cpc.setPayoutReference(null);
                cpcRepository.save(cpc);

                // incemento valore wallet
                if (cpc.getValue() > 0D) walletBusiness.incement(cpc.getWallet().getId(), cpc.getValue());

            });
            payout.getTransactionCPLS().forEach(transactionCPL -> {
                // rimuovo relazione
                TransactionCPL cpl = cplRepository.findById(transactionCPL.getId()).orElseThrow(() -> new ElementCleveradException("Transaction CPL", transactionCPL.getId()));
                cpl.setPayout(null);
                cpl.setPayoutReference(null);
                cplRepository.save(cpl);

                // incemento valore wallet
                if (cpl.getValue() > 0D) walletBusiness.incement(cpl.getWallet().getId(), cpl.getValue());

            });
            payout.getTransactionCPSS().forEach(transactionCPS -> {
                // rimuovo relazione
                TransactionCPS cps = cpsRepository.findById(transactionCPS.getId()).orElseThrow(() -> new ElementCleveradException("Transaction CPS", transactionCPS.getId()));
                cps.setPayout(null);
                cps.setPayoutReference(null);
                cpsRepository.save(cps);

                // incemento valore wallet
                if (cps.getValue() > 0D) walletBusiness.incement(cps.getWallet().getId(), cps.getValue());
            });


            repository.deleteById(id);
        } catch (ConstraintViolationException ex) {
            throw ex;
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    // SEARCH PAGINATED
    public Page<PayoutDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        if (request.getDictionaryId() == null) {
            request.setDictionaryIdNotConcluso(true);
        }
        Page<Payout> page = repository.findAll(getSpecification(request), pageable);
        return page.map(PayoutDTO::from);
    }

    public Page<PayoutDTO> findByIdAffilaite(Long id, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        Filter request = new Filter();
        if (id != null) request.setAffiliateId(id);
        Page<Payout> page = repository.findAll(getSpecification(request), pageable);
        return page.map(PayoutDTO::from);
    }

    // UPDATE
    public PayoutDTO update(Long id, Filter filter) {

        Payout payout = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Payout", id));
        mapper.map(filter, payout);

        payout.setAffiliate(affiliateRepository.findById(filter.affiliateId).orElseThrow(() -> new ElementCleveradException("Affiliate", filter.affiliateId)));
        payout.setDictionary(dictionaryRepository.findById(filter.dictionaryId).orElseThrow(() -> new ElementCleveradException("Dictionary", filter.dictionaryId)));

        return PayoutDTO.from(repository.save(payout));
    }

    public PayoutDTO updateStatus(Long id, Long dictionaryId) {
        Payout payout = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Payout", id));
        if (payout.getDictionary().getId() == 18L && dictionaryId == 19L) {
            // INVIA MAIL PAYOUT
            log.info("MAIL PAYOUT ::: " + payout.getId());
            MailService.BaseCreateRequest mailRequest = new MailService.BaseCreateRequest();
            mailRequest.setAffiliateId(payout.getAffiliate().getId());
            mailRequest.setPayoutId(payout.getId());
            mailService.inviaMailPayout(mailRequest);
        }
        payout.setDictionary(dictionaryRepository.findById(dictionaryId).orElseThrow(() -> new ElementCleveradException("Dictionary", dictionaryId)));
        return PayoutDTO.from(repository.save(payout));
    }

    public PayoutDTO removeCpc(Long payoutId, Long transactionId) {

        TransactionCPC cpc = cpcRepository.findById(transactionId).orElseThrow(() -> new ElementCleveradException("Transaction CPC", transactionId));
        Double value = cpc.getValue();
        cpc.setPayout(null);
        cpc.setPayoutReference(null);
        cpcRepository.save(cpc);

        Payout payout = repository.findById(payoutId).orElseThrow(() -> new ElementCleveradException("Payout", payoutId));
        Set<TransactionCPC> transactionCPCS = payout.getTransactionCPCS();
        transactionCPCS.remove(cpc);
        payout.setTransactionCPCS(transactionCPCS);
        Double totale = payout.getTotale();
        payout.setTotale(totale - value);
        repository.save(payout);

        return PayoutDTO.from(payout);
    }

    public PayoutDTO removeCpl(Long payoutId, Long transactionId) {
        TransactionCPL cpl = cplRepository.findById(transactionId).orElseThrow(() -> new ElementCleveradException("Transaction CPL", transactionId));
        Double value = cpl.getValue();
        cpl.setPayout(null);
        cpl.setPayoutReference(null);
        cplRepository.save(cpl);

        Payout payout = repository.findById(payoutId).orElseThrow(() -> new ElementCleveradException("Payout", payoutId));
        Set<TransactionCPL> transactionCPCl = payout.getTransactionCPLS();
        transactionCPCl.remove(cpl);
        payout.setTransactionCPLS(transactionCPCl);
        Double totale = payout.getTotale();
        payout.setTotale(totale - value);
        repository.save(payout);
        return PayoutDTO.from(payout);
    }

    //  GET TIPI
    public Page<DictionaryDTO> getTypes() {
        return dictionaryBusiness.getTypePayout();
    }

    /**
     * ============================================================================================================
     **/

    private Specification<Payout> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getAffiliateId() != null) {
                predicates.add(cb.equal(root.get("affiliate").get("id"), request.getAffiliateId()));
            }
            if (request.getDictionaryId() != null) {
                predicates.add(cb.equal(root.get("dictionary").get("id"), request.getDictionaryId()));
            }
            if (request.getDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("data"), request.getDateFrom()));
            }
            if (request.getDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("data"), request.getDateTo().plus(1, ChronoUnit.DAYS)));
            }
            if (request.getNote() != null) {
                predicates.add(cb.like(cb.upper(root.get("note")), "%" + request.getNote().toUpperCase() + "%"));
            }
            if (request.getDictionaryIdNotConcluso() != null && request.getDictionaryIdNotConcluso()) {
                predicates.add(cb.notEqual(root.get("dictionary").get("id"), 22L));
            }

            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
            return completePredicate;
        };
    }

    /**
     * ============================================================================================================
     **/

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BaseCreateRequest {
        private Double totale;
        private String valuta;
        private String note;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate data;
        private Long affiliateId;
        private List<Long> transazioniCpl;
        private List<Long> transazioniCpc;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private Long affiliateId;
        private Boolean stato;
        private Double totale;
        private String valuta;
        private String note;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateFrom;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateTo;
        private Long dictionaryId;
        private Boolean dictionaryIdNotConcluso;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Lista {
        HashMap<String, String> lista;
    }

}