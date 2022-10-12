package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.persistence.model.Channel;
import it.cleverad.engine.persistence.repository.ChannelRepository;
import it.cleverad.engine.web.dto.AffiliateChannelCommissionCampaignDTO;
import it.cleverad.engine.web.dto.ChannelDTO;
import it.cleverad.engine.web.dto.DictionaryDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional
public class ChannelBusiness {

    @Autowired
    AffiliateBusiness affiliateBusiness;
    @Autowired
    UserBusiness userBusiness;
    @Autowired
    private ChannelRepository repository;
    @Autowired
    private DictionaryBusiness dictionaryBusiness;
    @Autowired
    private Mapper mapper;
    @Autowired
    private AffiliateChannelCommissionCampaignBusiness accc;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public ChannelDTO create(BaseCreateRequest request) {
        Channel map = mapper.map(request, Channel.class);
        map.setCreationDate(LocalDateTime.now());
        map.setLastModificationDate(LocalDateTime.now());
        return ChannelDTO.from(repository.save(map));
    }

    // GET BY ID
    public ChannelDTO findById(Long id) {
        try {
            Channel channel = repository.findById(id).orElseThrow(Exception::new);
            return ChannelDTO.from(channel);
        } catch (Exception e) {
            log.error("Errore in findById", e);
            return null;
        }
    }

    // DELETE BY ID
    public void delete(Long id) {
        repository.deleteById(id);
    }

    // SEARCH PAGINATED
    public Page<ChannelDTO> search(Filter request, Pageable pageableRequest) {
        Pageable pageable = PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.asc("id")));
        Page<Channel> page = repository.findAll(getSpecification(request), pageable);

        return page.map(ChannelDTO::from);
    }

    // UPDATE
    public ChannelDTO update(Long id, Filter filter) {
        try {
            Channel channel = repository.findById(id).orElseThrow(Exception::new);
            ChannelDTO campaignDTOfrom = ChannelDTO.from(channel);

            mapper.map(filter, campaignDTOfrom);

            Channel mappedEntity = mapper.map(channel, Channel.class);
            mappedEntity.setLastModificationDate(LocalDateTime.now());
            mapper.map(campaignDTOfrom, mappedEntity);

            return ChannelDTO.from(repository.save(mappedEntity));
        } catch (Exception e) {
            log.error("Errore in update", e);
            return null;
        }
    }

    public Page<ChannelDTO> getbyIdAffiliate(Long id, Pageable pageableRequest) {

        AffiliateChannelCommissionCampaignBusiness.Filter rr = new AffiliateChannelCommissionCampaignBusiness.Filter();
        rr.setAffiliateId(id);
        Page<AffiliateChannelCommissionCampaignDTO> search = accc.search(rr, pageableRequest);

        List<Channel> channelList = search.stream().map(affiliateChannelCommissionCampaignDTO -> {
            return repository.findById(affiliateChannelCommissionCampaignDTO.getChannelId()).get();
        }).collect(Collectors.toList());

        Page<Channel> page = new PageImpl<>(channelList.stream().distinct().collect(Collectors.toList()));
        return page.map(ChannelDTO::from);
    }

    public Page<ChannelDTO> getbyIdUser(Long id, Pageable pageableRequest) {
        AffiliateChannelCommissionCampaignBusiness.Filter rr = new AffiliateChannelCommissionCampaignBusiness.Filter();
        rr.setAffiliateId(userBusiness.findById(id).getAffiliateId());
        Page<AffiliateChannelCommissionCampaignDTO> search = accc.search(rr, pageableRequest);

        List<Channel> channelList = search.stream().map(dtos -> {
            return repository.findById(dtos.getChannelId()).get();
        }).collect(Collectors.toList());

        Page<Channel> page = new PageImpl<>(channelList.stream().distinct().collect(Collectors.toList()));
        return page.map(ChannelDTO::from);
    }

    //  GET TIPI
    public Page<DictionaryDTO> getTypes() {
        return dictionaryBusiness.getTypeChannel();
    }

    /**
     * ============================================================================================================
     **/
    private Specification<Channel> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate = null;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getName() != null) {
                predicates.add(cb.equal(root.get("name"), request.getName()));
            }
            if (request.getShortDescription() != null) {
                predicates.add(cb.equal(root.get("shortDescription"), request.getShortDescription()));
            }
            if (request.getType() != null) {
                predicates.add(cb.equal(root.get("type"), request.getType()));
            }
            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }

            if (request.getCreationDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("creationDate"), LocalDateTime.ofInstant(request.getCreationDateFrom(), ZoneOffset.UTC)));
            }
            if (request.getCreationDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("creationDate"), LocalDateTime.ofInstant(request.getCreationDateTo().plus(1, ChronoUnit.DAYS), ZoneOffset.UTC)));
            }

            if (request.getLastModificationDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("lastModificationDate"), LocalDateTime.ofInstant(request.getLastModificationDateFrom(), ZoneOffset.UTC)));
            }
            if (request.getLastModificationDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("lastModificationDate"), LocalDateTime.ofInstant(request.getLastModificationDateTo().plus(1, ChronoUnit.DAYS), ZoneOffset.UTC)));
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
        private String name;
        private String shortDescription;
        private String type;
        private String approvazione;
        private String url;

        private Boolean status;
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDate startDate;
        @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDate endDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private Long id;

        private String name;
        private String shortDescription;
        private String type;
        private String approvazione;
        private String url;

        private Boolean status;
        private Instant creationDateFrom;
        private Instant creationDateTo;
        private Instant lastModificationDateFrom;
        private Instant lastModificationDateTo;
    }

}

