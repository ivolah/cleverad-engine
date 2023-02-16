package it.cleverad.engine.business;

import it.cleverad.engine.persistence.model.service.*;
import it.cleverad.engine.persistence.repository.service.*;
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

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@Transactional
public class StatsBusiness {

    @Autowired
    private StatCpcClickCampaignRepository statCpcClickCampaignRepository;
    @Autowired
    private StatCpcClickCampaignWeekRepository statCpcClickCampaignWeekRepository;
    @Autowired
    private StatCpcValueCampaignRepository statCpcValueCampaignRepository;
    @Autowired
    private StatCpcValueCampaignWeekRepository statCpcValueCampaignWeekRepository;
    @Autowired
    private StatCpcTransactionCampaignRepository statCpcTransactionCampaignRepository;
    @Autowired
    private StatCpcTransactionCampaignWeekRepository statCpcTransactionCampaignWeekRepository;
    @Autowired
    private StatCplValueCampaignRepository statCplValueCampaignRepository;
    @Autowired
    private StatCplValueCampaignWeekRepository statCplValueCampaignWeekRepository;

    /**
     * ============================================================================================================
     **/

    public Page<StatCpcClickCampaign> searchStatCpcClickCampaign(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<StatCpcClickCampaign> page = statCpcClickCampaignRepository.findAll(getSpecificationStatCpcClickCampaign(request), pageable);
        return page;
    }

    private Specification<StatCpcClickCampaign> getSpecificationStatCpcClickCampaign(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }

            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
            return completePredicate;
        };
    }

    public Page<StatCpcClickCampaignWeek> searchStatCpcClickCampaignWeek(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<StatCpcClickCampaignWeek> page = statCpcClickCampaignWeekRepository.findAll(getSpecificationStatCpcClickCampaignWeek(request), pageable);
        return page;
    }

    private Specification<StatCpcClickCampaignWeek> getSpecificationStatCpcClickCampaignWeek(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }

            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
            return completePredicate;
        };
    }

    /**
     * ============================================================================================================
     **/

    public Page<StatCpcValueCampaign> searchStatCpcValueCampaign(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<StatCpcValueCampaign> page = statCpcValueCampaignRepository.findAll(getSpecificationStatCpcValueCampaign(request), pageable);
        return page;
    }

    private Specification<StatCpcValueCampaign> getSpecificationStatCpcValueCampaign(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }

            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
            return completePredicate;
        };
    }

    public Page<StatCpcValueCampaignWeek> searchStatCpcValueCampaignWeek(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<StatCpcValueCampaignWeek> page = statCpcValueCampaignWeekRepository.findAll(getSpecificationStatCpcValueCampaignWeek(request), pageable);
        return page;
    }

    private Specification<StatCpcValueCampaignWeek> getSpecificationStatCpcValueCampaignWeek(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }

            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
            return completePredicate;
        };
    }

    /**
     * ============================================================================================================
     **/

    public Page<StatCpcTransactionCampaign> searchStatCpcTransactionCampaign(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<StatCpcTransactionCampaign> page = statCpcTransactionCampaignRepository.findAll(getSpecificationStatCpcTransactionCampaign(request), pageable);
        return page;
    }

    private Specification<StatCpcTransactionCampaign> getSpecificationStatCpcTransactionCampaign(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }

            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
            return completePredicate;
        };
    }

    public Page<StatCpcTransactionCampaignWeek> searchStatCpcTransactionCampaignWeek(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<StatCpcTransactionCampaignWeek> page = statCpcTransactionCampaignWeekRepository.findAll(getSpecificationStatCpcTransactionCampaignWeek(request), pageable);
        return page;
    }

    private Specification<StatCpcTransactionCampaignWeek> getSpecificationStatCpcTransactionCampaignWeek(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }

            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
            return completePredicate;
        };
    }

    /**
     * ============================================================================================================
     **/

    public Page<StatCplValueCampaign> searchStatCplValueCampaign(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<StatCplValueCampaign> page = statCplValueCampaignRepository.findAll(getSpecificationStatCplValueCampaign(request), pageable);
        return page;
    }

    private Specification<StatCplValueCampaign> getSpecificationStatCplValueCampaign(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }

            completePredicate = cb.and(predicates.toArray(new Predicate[0]));
            return completePredicate;
        };
    }

    public Page<StatCplValueCampaignWeek> searchStatCplValueCampaignWeek(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<StatCplValueCampaignWeek> page = statCplValueCampaignWeekRepository.findAll(getSpecificationStatCplValueCampaignWeek(request), pageable);
        return page;
    }

    private Specification<StatCplValueCampaignWeek> getSpecificationStatCplValueCampaignWeek(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
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
    public static class Filter {
        private Long id;
    }

}
