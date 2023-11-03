//package it.cleverad.engine.business.incoming;
//
//
//import it.cleverad.engine.persistence.model.incoming.Registrazione;
//import it.cleverad.engine.persistence.repository.incoming.RegistrazioneRepository;
//import it.cleverad.engine.web.dto.RegistrazioneDTO;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import lombok.ToString;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.jpa.domain.Specification;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import javax.persistence.criteria.Predicate;
//import java.util.ArrayList;
//import java.util.List;
//
//@Slf4j
//@Component
//@Transactional
//public class RegistrazioneBusiness {
//
//    @Autowired
//    private RegistrazioneRepository registrazioneRepository;
//
//    /**
//     * ============================================================================================================
//     **/
//
//    // GET BY ID CPL
//    public RegistrazioneDTO findById(Long id) throws Exception {
//        Registrazione registrazione = null;
//        Filter request = new Filter();
//        request.setId(id);
//        registrazione = registrazioneRepository.findById(id).orElseThrow(() -> new Exception("Registrazione " + id));
//        return RegistrazioneDTO.from(registrazione);
//    }
//
//    // SEARCH PAGINATED
//    public Page<RegistrazioneDTO> search(Filter request) {
//        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc("id")));
//        Page<Registrazione> page = registrazioneRepository.findAll(getSpecification(request), pageable);
//        return page.map(RegistrazioneDTO::from);
//    }
//
//    /**
//     * ============================================================================================================
//     **/
//
//
//    private Specification<Registrazione> getSpecification(Filter request) {
//        return (root, query, cb) -> {
//            Predicate completePredicate = null;
//            List<Predicate> predicates = new ArrayList<>();
//
//            if (request.getId() != null) {
//                predicates.add(cb.equal(root.get("id"), request.getId()));
//            }
//            if (request.getIp() != null) {
//                predicates.add(cb.equal(root.get("ip"), request.getIp()));
//            }
//            if (request.getUserAgent() != null) {
//                predicates.add(cb.equal(root.get("userAgent"), request.getUserAgent()));
//            }
//
//
//            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
//            return completePredicate;
//        };
//    }
//
//    /**
//     * ============================================================================================================
//     **/
//
//    @Data
//    @NoArgsConstructor
//    @AllArgsConstructor
//    @ToString
//    public static class Filter {
//        private Long id;
//        private String nome;
//        private String ragioneSociale;
//        private String piva;
//        private String telefono;
//        private String email;
//        private String citta;
//        private String indirizzo;
//        private String civico;
//        private String cap;
//        private String provincia;
//        private String privacy1;
//        private String privacy2;
//        private String privacy3;
//        private String ip;
//        private String userAgent;
//    }
//
//}