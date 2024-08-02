package it.cleverad.engine.business;

import it.cleverad.engine.persistence.model.service.Dictionary;
import it.cleverad.engine.persistence.model.service.*;
import it.cleverad.engine.persistence.repository.service.*;
import it.cleverad.engine.service.MailService;
import it.cleverad.engine.service.WalletService;
import it.cleverad.engine.web.dto.DictionaryDTO;
import it.cleverad.engine.web.dto.PayoutDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.decimal4j.util.DoubleRounder;
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
import java.time.temporal.TemporalAdjusters;
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
    private WalletService walletService;
    @Autowired
    private MailService mailService;
    @Autowired
    private TransactionStatusBusiness transactionStatusBusiness;
    @Autowired
    private TransactionCPCBusiness transactionCPCBusiness;
    @Autowired
    private TransactionCPLBusiness transactionCPLBusiness;
    @Autowired
    private TransactionCPSBusiness transactionCPSBusiness;
    @Autowired
    private TransactionCPMBusiness transactionCPMBusiness;

    /**
     * ============================================================================================================
     **/

    public List<Payout> createAll(TransactionStatusBusiness.QueryFilter request) {
        Page<QueryTransaction> results = transactionStatusBusiness.searchPrefiltratoN(request, Pageable.ofSize(Integer.MAX_VALUE));
        log.info("TOT elementi: " + results.getTotalElements());
        List<Long> cpcs = results.stream().filter(x -> x.getTipo().equals("CPC")).map(x -> x.getid()).collect(Collectors.toList());
        List<Long> cpls = results.stream().filter(x -> x.getTipo().equals("CPL")).map(x -> x.getid()).collect(Collectors.toList());
        List<Long> cpss = results.stream().filter(x -> x.getTipo().equals("CPS")).map(x -> x.getid()).collect(Collectors.toList());
        log.info("CPC: {} ::: CPL: {} ::: CPA: {}", cpcs.size(), cpls.size(), cpss.size());

        BaseCreateRequest payoutRequest = new BaseCreateRequest();
        payoutRequest.setTransazioniCpc(cpcs);
        payoutRequest.setTransazioniCpl(cpls);
        payoutRequest.setTransazioniCps(cpss);
        payoutRequest.setNote(request.getNote());
        return this.create(payoutRequest);
    }

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

//        request.getTransazioniCps().stream().forEach(id -> {
//            TransactionCPS transaction = cpsRepository.findById(id).orElseThrow(() -> new ElementCleveradException("Transaction CPS", id));
//            affiliatesList.add(transaction.getAffiliate().getId());
//        });

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
            map.setImponibile(0.0);
            map.setTotale(0.0);
            map.setIva(0.0);
            map.setNote(finalNote);
            Dictionary dictionary = dictionaryRepository.findById(18L).orElseThrow(() -> new ElementCleveradException("Dictionary", 18L));
            map.setDictionary(dictionary);
            Long dataDaSommare = Long.valueOf(affiliate.getDictionaryTermType().getDescription());
            LocalDate ultimoDelMese = LocalDate.now().plusDays(dataDaSommare).with(TemporalAdjusters.lastDayOfMonth());;
            map.setDataScadenza(ultimoDelMese);
            map = repository.save(map);
            affiliatoPayout.put(idAffiliate, map.getId());
        });

        Set<Payout> listaPayout = new HashSet<>();

        // per ogni singola transazione CPC
        request.getTransazioniCpc().forEach(id -> {
            TransactionCPC transaction = cpcRepository.findById(id).orElseThrow(() -> new ElementCleveradException("Transaction CPC", id));
            Long payoutId = affiliatoPayout.get(transaction.getAffiliate().getId());
            Payout payout = repository.findById(payoutId).orElseThrow(() -> new ElementCleveradException("PAYOUT CPC", payoutId));

            //aumento il valore
            Double imponibile = DoubleRounder.round(payout.getImponibile() + transaction.getValue(), 2);

            //aggiorno payout
            payout.setImponibile(imponibile);
            Payout pp = repository.save(payout);
            listaPayout.add(pp);

            //aggiorno transazione e setto riferimento a payout
            transaction.setPayout(payout);
            transaction.setPayoutReference("Payout " + payoutId);
            transaction.setPayoutPresent(true);
            cpcRepository.save(transaction);
        });

        // per ogni singola transazione CPL
        request.getTransazioniCpl().stream().forEach(id -> {
            TransactionCPL transaction = cplRepository.findById(id).orElseThrow(() -> new ElementCleveradException("Transaction CPL", id));
            Long payoutId = affiliatoPayout.get(transaction.getAffiliate().getId());
            Payout payout = repository.findById(payoutId).orElseThrow(() -> new ElementCleveradException("PAYOUT CPL", payoutId));

            //aumento il valore
            Double imponibile = DoubleRounder.round(payout.getImponibile() + transaction.getValue(), 2);

            //aggiorno payout
            payout.setImponibile(imponibile);
            Payout pp = repository.save(payout);
            listaPayout.add(pp);

            //aggiorno transazione e setto riferimento a payout
            transaction.setPayout(payout);
            transaction.setPayoutReference("Payout " + payoutId);
            transaction.setPayoutPresent(true);
            cplRepository.save(transaction);
        });

        // per ogni singola transazione
//        request.getTransazioniCps().stream().forEach(id -> {
//            TransactionCPS transaction = cpsRepository.findById(id).orElseThrow(() -> new ElementCleveradException("Transaction CPL", id));
//            Long payoutId = affiliatoPayout.get(transaction.getAffiliate().getId());
//            Payout payout = repository.findById(payoutId).orElseThrow(() -> new ElementCleveradException("PAYOUT CPL", payoutId));
//
//            //aumento il valore
//            Double imponibile = DoubleRounder.round(payout.getImponibile() + transaction.getValue(), 2);
//
//            //aggiorno payout
//            payout.setImponibile(imponibile);
//            Payout pp = repository.save(payout);
//            listaPayout.add(pp);
//
//            //aggiorno transazione e setto riferimento a payout
//            transaction.setPayout(payout);
//            transaction.setPayoutReference("Payout " + payoutId);
//            transaction.setPayoutPresent(true);
//            cpsRepository.save(transaction);
//        });

        // rigenero tutti i wallet
        affiliatoPayout.forEach((idAffiliate, longTwo) -> walletService.rigenera(idAffiliate));

        // setto iva e imponibile
        List<Payout> pys = new ArrayList<>(listaPayout);
        pys.forEach(payout -> {
            Double ivaDaMoltiplicare = Double.valueOf(payout.getAffiliate().getDictionaryVatType().getDescription());
            payout.setIva(DoubleRounder.round(payout.getImponibile() * ivaDaMoltiplicare, 2));
            payout.setTotale(DoubleRounder.round(payout.getImponibile() + payout.getIva(), 2));
        });

        log.info("Numero payout generati :  " + pys.size());
        return pys;
    }

    // GET BY ID
    public PayoutDTO findById(Long id) {
        Payout payout = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Payout", id));
        return PayoutDTO.from(payout);
    }

    // SEARCH PAGINATED
    public Page<PayoutDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        Page<Payout> page = repository.findAll(getSpecification(request), pageable);
        List<PayoutDTO> listaPayout = page.map(PayoutDTO::fromNoTransazioni).toList();

        PayoutDTO dto = new PayoutDTO();
        dto.setImponibile(listaPayout.stream().mapToDouble(PayoutDTO::getImponibile).sum());
        dto.setTotale(listaPayout.stream().mapToDouble(PayoutDTO::getTotale).sum());
        dto.setIva(listaPayout.stream().mapToDouble(PayoutDTO::getIva).sum());

        List<PayoutDTO> modifiableList = new ArrayList<>(listaPayout);
        modifiableList.add(dto);

        return new PageImpl<>(modifiableList, pageableRequest, page.getTotalElements());
    }

    public Page<PayoutDTO> findByIdAffilaite(Long id, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        Filter request = new Filter();
        request.setDictionaryIdNotApprovato(true);
        request.setDictionaryIdNotScaduto(true);
        if (id != null) request.setAffiliateId(id);
        Page<Payout> page = repository.findAll(getSpecification(request), pageable);
        return page.map(PayoutDTO::fromNoTransazioni);
    }



    // UPDATE
    public PayoutDTO update(Long id, Filter filter) {
        Payout payout = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Payout", id));
        payout.setDataScadenza(filter.getDataScadenza());
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
            mailRequest.setNote(payout.getNote());
            mailService.inviaMailPayout(mailRequest);
        }
        payout.setDictionary(dictionaryRepository.findById(dictionaryId).orElseThrow(() -> new ElementCleveradException("Dictionary", dictionaryId)));
        return PayoutDTO.from(repository.save(payout));
    }

    //  GET TIPI
    public Page<DictionaryDTO> getTypes() {
        return dictionaryBusiness.getTypePayout();
    }

    /**
     * ============================================================================================================
     **/

    public List<PayoutDTO> getPayoutToDisable() {
        Filter request = new Filter();
        request.setStato(true);
        request.setDateTo(LocalDate.now().minusMonths(6));
        request.setDictionaryIdNotConcluso(true);
        return repository.findAll(getSpecification(request), PageRequest.of(0, Integer.MAX_VALUE)).map(PayoutDTO::fromNoTransazioni).toList();
    }

    public PayoutDTO settaScaduti(Long id) {
        Payout payout = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Payout", id));
        payout.setDictionary(dictionaryRepository.findById(126L).get());

        // rigetto tutte le transazioni del payout
        payout.getTransactionCPCS().stream().forEach(transaction -> transactionCPCBusiness.settaScaduto(transaction.getId()));
        payout.getTransactionCPLS().stream().forEach(transaction -> transactionCPLBusiness.settaScaduto(transaction.getId()));
        payout.getTransactionCPMS().stream().forEach(transaction -> transactionCPMBusiness.settaScaduto(transaction.getId()));
        payout.getTransactionCPSS().stream().forEach(transaction -> transactionCPSBusiness.settaScaduto(transaction.getId()));

        return PayoutDTO.from(repository.save(payout));
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

            if (request.getDataScadenzaFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dataScadenza"), request.getDataScadenzaFrom()));
            }
            if (request.getDataScadenzaTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dataScadenza"), request.getDataScadenzaTo().plus(1, ChronoUnit.DAYS)));
            }

            if (request.getNote() != null) {
                predicates.add(cb.like(cb.upper(root.get("note")), "%" + request.getNote().toUpperCase() + "%"));
            }
            if (request.getDictionaryIdNotConcluso() != null && request.getDictionaryIdNotConcluso()) {
                predicates.add(cb.notEqual(root.get("dictionary").get("id"), 22L));
            }

            if (request.getDictionaryIdNotApprovato() != null && request.getDictionaryIdNotApprovato()) {
                predicates.add(cb.notEqual(root.get("dictionary").get("id"), 18));
            }

            if (request.getDictionaryIdNotScaduto() != null && request.getDictionaryIdNotScaduto()) {
                predicates.add(cb.notEqual(root.get("dictionary").get("id"), 126));
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
        private List<Long> transazioniCps;
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
        private Boolean dictionaryIdNotApprovato;
        private Boolean dictionaryIdNotScaduto;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate data;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dataScadenza;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dataScadenzaFrom;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dataScadenzaTo;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Lista {
        HashMap<String, String> lista;
    }

}