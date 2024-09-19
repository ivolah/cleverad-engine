package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.persistence.model.service.Agent;
import it.cleverad.engine.persistence.model.service.WidgetAgent;
import it.cleverad.engine.persistence.repository.service.AgentRepository;
import it.cleverad.engine.web.dto.AgentDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import it.cleverad.engine.web.exception.PostgresDeleteCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
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
public class AgentBusiness {

    @Autowired
    private AgentRepository repository;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private Mapper mapper;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public AgentDTO create(BaseCreateRequest request) {
        Agent map = mapper.map(request, Agent.class);
        return AgentDTO.from(repository.save(map));
    }


    // GET BY ID
    public AgentDTO findById(Long id) {
        return AgentDTO.from(repository.findById(id).orElseThrow(() -> new ElementCleveradException(" Agent", id)));
    }

    // DELETE BY ID
    public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (ConstraintViolationException ex) {
            throw ex;
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }
    }

    // SEARCH PAGINATED
    public Page<AgentDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<Agent> page = repository.findAll(getSpecification(request), pageable);
        return page.map(AgentDTO::from);
    }

    // UPDATE
    public AgentDTO update(Long id, Filter filter) {
        Agent agent = repository.findById(id).orElseThrow(() -> new ElementCleveradException(" Agent", id));
        mapper.map(filter, agent);
        return AgentDTO.from(repository.save(agent));
    }

    public List<WidgetAgent> searchOS(Filter request) {
        if (Boolean.TRUE.equals(jwtUserDetailsService.isAdmin())) {
            return repository.geOs(request.getCampaignId(), request.getAffiliateId());
        } else {
            return repository.geOs(request.getCampaignId(), String.valueOf(jwtUserDetailsService.getAffiliateId()));
        }
    }

    public List<WidgetAgent> searchDevic(Filter request) {
        if (Boolean.TRUE.equals(jwtUserDetailsService.isAdmin())) {
            return repository.getDevice(request.getCampaignId(), request.getAffiliateId());
        } else {
            return repository.getDevice(request.getCampaignId(), String.valueOf(jwtUserDetailsService.getAffiliateId()));
        }
    }

    //  SEARCH AGENT DATA
    public List<WidgetAgent> searchAgentDetailed(Filter request) {
        if (Boolean.TRUE.equals(jwtUserDetailsService.isAdmin())) {
            return repository.searchAgentDetailed(request.getCampaignId(), request.getAffiliateId());
        } else {
            return repository.searchAgentDetailed(request.getCampaignId(), String.valueOf(jwtUserDetailsService.getAffiliateId()));
        }
    }

    public List<WidgetAgent> searchAgent(Filter request) {
        if (Boolean.TRUE.equals(jwtUserDetailsService.isAdmin())) {
            return repository.getAgent(request.getCampaignId(), request.getAffiliateId());
        } else {
            return repository.getAgent(request.getCampaignId(), String.valueOf(jwtUserDetailsService.getAffiliateId()));
        }
    }

    /**
     * ============================================================================================================
     **/
    private Specification<Agent> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getCampaignId() != null) {
                predicates.add(cb.equal(root.get("campaignId"), request.getCampaignId()));
            }
            if (request.getAffiliateId() != null) {
                predicates.add(cb.equal(root.get("affiliateId"), request.getAffiliateId()));
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

        private String tipo;
        private String campaignId;
        private String affiliateId;

        private String DeviceName;
        private String DeviceBrand;
        private String DeviceCpu;
        private String DeviceCpuBits;
        private String DeviceVersion;

        private String OperatingSystemClass;
        private String OperatingSystemName;
        private String OperatingSystemVersion;

        private String LayoutEngineClass;
        private String LayoutEngineName;
        private String LayoutEngineVersion;

        private String AgentClass;
        private String AgentName;
        private String AgentVersion;


    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;

        private String tipo;
        private String campaignId;
        private String affiliateId;

        private String DeviceName;
        private String DeviceBrand;
        private String DeviceCpu;
        private String DeviceCpuBits;
        private String DeviceVersion;

        private String OperatingSystemClass;
        private String OperatingSystemName;
        private String OperatingSystemVersion;

        private String LayoutEngineClass;
        private String LayoutEngineName;
        private String LayoutEngineVersion;

        private String AgentClass;
        private String AgentName;
        private String AgentVersion;


    }

}