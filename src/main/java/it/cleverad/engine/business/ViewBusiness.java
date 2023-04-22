package it.cleverad.engine.business;

import it.cleverad.engine.persistence.model.service.StatCampaignDayCpc;
import it.cleverad.engine.persistence.model.service.StatCampaignDayCpl;
import it.cleverad.engine.persistence.model.service.StatCampaignDayCpm;
import it.cleverad.engine.persistence.model.service.StatCampaignDayCps;
import it.cleverad.engine.persistence.repository.service.StatCampaignDayCpcRepository;
import it.cleverad.engine.persistence.repository.service.StatCampaignDayCplRepository;
import it.cleverad.engine.persistence.repository.service.StatCampaignDayCpmRepository;
import it.cleverad.engine.persistence.repository.service.StatCampaignDayCpsRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.threeten.extra.YearWeek;

import javax.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional
public class ViewBusiness {

    @Autowired
    private StatCampaignDayCpcRepository statCampaignDayCpcRepository;
    @Autowired
    private StatCampaignDayCpmRepository statCampaignDayCpmRepository;
    @Autowired
    private StatCampaignDayCplRepository statCampaignDayCplRepository;
    @Autowired
    private StatCampaignDayCpsRepository statCampaignDayCpsRepository;

    /**
     * ===========================================================================================================
     * >>> CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC CPC <<<
     * ===========================================================================================================
     **/

    /// CPC CAMPAIGN DAY
    public Page<StatCampaignDayCpc> getStatCampaignDayCpc(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        Page<StatCampaignDayCpc> page = statCampaignDayCpcRepository.findAll(getSpecificationCampaignDayCpc(request), pageable);
        return page;
    }

    public Page<StatCampaignDayCpc> getTopCampaignsDayCpc() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("totale")));

        LocalDate oggi = LocalDate.now();
        Integer week = YearWeek.from(LocalDate.of(oggi.getYear(), oggi.getMonth(), oggi.getDayOfMonth())).getWeek();

        Filter request = new Filter();
        log.info("week :: " + week);
        request.setWeek(week);
        request.setWeekMeno(week - 1);
        Page<StatCampaignDayCpc> page = statCampaignDayCpcRepository.findAll(getSpecificationCampaignDayCpc(request), pageable);
        return page;
    }


    private Specification<StatCampaignDayCpc> getSpecificationCampaignDayCpc(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getCampaignId() != null) {
                predicates.add(cb.equal(root.get("campaignId"), request.getCampaignId()));
            }

            if (request.getWeekMeno() != null) {
                predicates.add(cb.or(cb.equal(root.get("week"), request.getWeek()), cb.equal(root.get("week"), request.getWeekMeno())));
            }

            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
            return completePredicate;
        };
    }


    /**
     * ===========================================================================================================
     * >>> CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM CPM <<<
     * ===========================================================================================================
     **/

    public Page<StatCampaignDayCpm> getStatCampaignDayCpm(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        Page<StatCampaignDayCpm> page = statCampaignDayCpmRepository.findAll(getSpecificationCampaignDayCpm(request), pageable);
        return page;
    }

    public Page<StatCampaignDayCpm> getTopCampaignsDayCpm() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("totale")));

        LocalDate oggi = LocalDate.now();
        Integer week = YearWeek.from(LocalDate.of(oggi.getYear(), oggi.getMonth(), oggi.getDayOfMonth())).getWeek();

        Filter request = new Filter();
        request.setWeek(week);
        request.setWeekMeno(week - 1);
        Page<StatCampaignDayCpm> page = statCampaignDayCpmRepository.findAll(getSpecificationCampaignDayCpm(request), pageable);
        return page;
    }


    private Specification<StatCampaignDayCpm> getSpecificationCampaignDayCpm(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getCampaignId() != null) {
                predicates.add(cb.equal(root.get("campaignId"), request.getCampaignId()));
            }
            if (request.getWeekMeno() != null) {
                predicates.add(cb.or(cb.equal(root.get("week"), request.getWeek()), cb.equal(root.get("week"), request.getWeekMeno())));
            }

            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
            return completePredicate;
        };
    }


    /**
     * ===========================================================================================================
     * >>> CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL CPL <<<
     * ===========================================================================================================
     **/

    public Page<StatCampaignDayCpl> getStatCampaignDayCpl(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        Page<StatCampaignDayCpl> page = statCampaignDayCplRepository.findAll(getSpecificationCampaignDayCpl(request), pageable);
        return page;
    }

    public Page<StatCampaignDayCpl> getTopCampaignsDayCpl() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("valore")));

        LocalDate oggi = LocalDate.now();
        Integer week = YearWeek.from(LocalDate.of(oggi.getYear(), oggi.getMonth(), oggi.getDayOfMonth())).getWeek();

        Filter request = new Filter();
        request.setWeek(week);
        request.setWeekMeno(week - 1);
        Page<StatCampaignDayCpl> page = statCampaignDayCplRepository.findAll(getSpecificationCampaignDayCpl(request), pageable);
        return page;
    }

    private Specification<StatCampaignDayCpl> getSpecificationCampaignDayCpl(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getCampaignId() != null) {
                predicates.add(cb.equal(root.get("campaignId"), request.getCampaignId()));
            }
            if (request.getWeekMeno() != null) {
                predicates.add(cb.or(cb.equal(root.get("week"), request.getWeek()), cb.equal(root.get("week"), request.getWeekMeno())));
            }

            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
            return completePredicate;
        };
    }

    /**
     * ===========================================================================================================
     * >>> CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS CPS <<<
     * ===========================================================================================================
     **/

    public Page<StatCampaignDayCps> getStatCampaignDayCps(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id")));
        Page<StatCampaignDayCps> page = statCampaignDayCpsRepository.findAll(getSpecificationCampaignDayCps(request), pageable);
        return page;
    }

    public Page<StatCampaignDayCps> getTopCampaignsDayCps() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("valore")));

        LocalDate oggi = LocalDate.now();
        Integer week = YearWeek.from(LocalDate.of(oggi.getYear(), oggi.getMonth(), oggi.getDayOfMonth())).getWeek();

        Filter request = new Filter();
        request.setWeek(week);
        request.setWeekMeno(week - 1);
        Page<StatCampaignDayCps> page = statCampaignDayCpsRepository.findAll(getSpecificationCampaignDayCps(request), pageable);
        return page;
    }

    private Specification<StatCampaignDayCps> getSpecificationCampaignDayCps(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getCampaignId() != null) {
                predicates.add(cb.equal(root.get("campaignId"), request.getCampaignId()));
            }
            if (request.getWeekMeno() != null) {
                predicates.add(cb.or(cb.equal(root.get("week"), request.getWeek()), cb.equal(root.get("week"), request.getWeekMeno())));
            }

            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
            return completePredicate;
        };
    }

//    public Page<StatCPLClickCampaignMediaDay> searchStatCpcClickCampaignMediaDay(Filter request, Pageable pageableRequest) {
//        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
//        Page<StatCpcClickCampaignMediaDay> page = statCpcClickCampaignMediaDayRepository.findAll(getSpecificationCpcCampaignDay(request), pageable);
//        return page;
//    }
//
//    private Specification<StatCpcClickCampaignMediaDay> getSpecificationStatCpcClickCampaignWeek(Filter request) {
//        return (root, query, cb) -> {
//            Predicate completePredicate = null;
//            List<Predicate> predicates = new ArrayList<>();
//
//            if (request.getId() != null) {
//                predicates.add(cb.equal(root.get("id"), request.getId()));
//            }
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
//    public Page<StatCpcValueCampaign> searchStatCpcValueCampaign(Filter request, Pageable pageableRequest) {
//        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
//        Page<StatCpcValueCampaign> page = statCpcValueCampaignRepository.findAll(getSpecificationStatCpcValueCampaign(request), pageable);
//        return page;
//    }
//
//    private Specification<StatCpcValueCampaign> getSpecificationStatCpcValueCampaign(Filter request) {
//        return (root, query, cb) -> {
//            Predicate completePredicate = null;
//            List<Predicate> predicates = new ArrayList<>();
//
//            if (request.getId() != null) {
//                predicates.add(cb.equal(root.get("id"), request.getId()));
//            }
//
//            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
//            return completePredicate;
//        };
//    }
//
//    public Page<StatCpcValueCampaignWeek> searchStatCpcValueCampaignWeek(Filter request, Pageable pageableRequest) {
//        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
//        Page<StatCpcValueCampaignWeek> page = statCpcValueCampaignWeekRepository.findAll(getSpecificationStatCpcValueCampaignWeek(request), pageable);
//        return page;
//    }
//
//    private Specification<StatCpcValueCampaignWeek> getSpecificationStatCpcValueCampaignWeek(Filter request) {
//        return (root, query, cb) -> {
//            Predicate completePredicate = null;
//            List<Predicate> predicates = new ArrayList<>();
//
//            if (request.getId() != null) {
//                predicates.add(cb.equal(root.get("id"), request.getId()));
//            }
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
//    public Page<StatCpcTransactionCampaignMedia> searchStatCpcTransactionCampaign(Filter request, Pageable pageableRequest) {
//        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
//        Page<StatCpcTransactionCampaignMedia> page = statCpcTransactionCampaignRepository.findAll(getSpecificationStatCpcTransactionCampaign(request), pageable);
//        return page;
//    }
//
//    private Specification<StatCpcTransactionCampaignMedia> getSpecificationStatCpcTransactionCampaign(Filter request) {
//        return (root, query, cb) -> {
//            Predicate completePredicate = null;
//            List<Predicate> predicates = new ArrayList<>();
//
//            if (request.getId() != null) {
//                predicates.add(cb.equal(root.get("id"), request.getId()));
//            }
//
//            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
//            return completePredicate;
//        };
//    }
//
//        /**
//     * ============================================================================================================
//     **/
//
//    public Page<StatCplValueCampaign> searchStatCplValueCampaign(Filter request, Pageable pageableRequest) {
//        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
//        Page<StatCplValueCampaign> page = statCplValueCampaignRepository.findAll(getSpecificationStatCplValueCampaign(request), pageable);
//        return page;
//    }
//
//    private Specification<StatCplValueCampaign> getSpecificationStatCplValueCampaign(Filter request) {
//        return (root, query, cb) -> {
//            Predicate completePredicate = null;
//            List<Predicate> predicates = new ArrayList<>();
//
//            if (request.getId() != null) {
//                predicates.add(cb.equal(root.get("id"), request.getId()));
//            }
//
//            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
//            return completePredicate;
//        };
//    }
//
//    public Page<StatCplValueCampaignWeek> searchStatCplValueCampaignWeek(Filter request, Pageable pageableRequest) {
//        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
//        Page<StatCplValueCampaignWeek> page = statCplValueCampaignWeekRepository.findAll(getSpecificationStatCplValueCampaignWeek(request), pageable);
//        return page;
//    }
//
//    private Specification<StatCplValueCampaignWeek> getSpecificationStatCplValueCampaignWeek(Filter request) {
//        return (root, query, cb) -> {
//            Predicate completePredicate = null;
//            List<Predicate> predicates = new ArrayList<>();
//
//            if (request.getId() != null) {
//                predicates.add(cb.equal(root.get("id"), request.getId()));
//            }
//
//            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
//            return completePredicate;
//        };
//    }

    /**
     * ============================================================================================================
     **/

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;
        private String name;
        private String description;

        private Long campaignId;

        private Long year;
        private Long month;
        private Long day;
        private Integer week;
        private Integer weekMeno;
    }

}
