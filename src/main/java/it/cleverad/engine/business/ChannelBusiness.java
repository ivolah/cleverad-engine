package it.cleverad.engine.business;

import com.github.dozermapper.core.Mapper;
import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.persistence.model.service.Channel;
import it.cleverad.engine.persistence.model.service.ChannelCategory;
import it.cleverad.engine.persistence.repository.service.AffiliateRepository;
import it.cleverad.engine.persistence.repository.service.ChannelRepository;
import it.cleverad.engine.persistence.repository.service.DictionaryRepository;
import it.cleverad.engine.service.MailService;
import it.cleverad.engine.web.dto.AffiliateChannelCommissionCampaignDTO;
import it.cleverad.engine.web.dto.ChannelDTO;
import it.cleverad.engine.web.dto.DictionaryDTO;
import it.cleverad.engine.web.exception.ElementCleveradException;
import it.cleverad.engine.web.exception.PostgresDeleteCleveradException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional
public class ChannelBusiness {

    @Autowired
    private ChannelRepository repository;
    @Autowired
    private Mapper mapper;
    @Autowired
    private DictionaryRepository dictionaryRepository;
    @Autowired
    private AffiliateRepository affiliateRepository;
    @Autowired
    private DictionaryBusiness dictionaryBusiness;
    @Autowired
    private UserBusiness userBusiness;
    @Autowired
    private ChannelCategoryBusiness channelCategoryBusiness;
    @Autowired
    private AffiliateChannelCommissionCampaignBusiness accc;
    @Autowired
    private MailService mailService;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    /**
     * ============================================================================================================
     **/

    // CREATE
    public ChannelDTO create(BaseCreateRequest request) {

        if (request.getRegistrazione() == null) {
            request.setRegistrazione(false);
        }

        request.setStatus(true);

        Channel map = mapper.map(request, Channel.class);
        map.setDictionary(dictionaryRepository.findById(request.dictionaryId).orElseThrow(() -> new ElementCleveradException("Dictionary", request.dictionaryId)));
        if (request.typeId != null)
            map.setDictionaryType(dictionaryRepository.findById(request.typeId).orElseThrow(() -> new ElementCleveradException("Type", request.typeId)));
        if (request.businessTypeId != null)
            map.setDictionaryBusinessType(dictionaryRepository.findById(request.businessTypeId).orElseThrow(() -> new ElementCleveradException("Business Type", request.businessTypeId)));
        if (request.ownerId != null)
            map.setDictionaryOwner(dictionaryRepository.findById(request.ownerId).orElseThrow(() -> new ElementCleveradException("Owner", request.ownerId)));
        if (request.affiliateId != null) {
            map.setAffiliate(affiliateRepository.findById(request.affiliateId).orElseThrow(() -> new ElementCleveradException("Affiliate", request.affiliateId)));
        } else {
            map.setAffiliate(affiliateRepository.findById(jwtUserDetailsService.getAffiliateId()).orElseThrow(() -> new ElementCleveradException("Affiliate", jwtUserDetailsService.getAffiliateId())));
        }

        ChannelDTO channelDTO = ChannelDTO.from(repository.save(map));

        if (!request.getRegistrazione()) {
            MailService.BaseCreateRequest mailRequest = new MailService.BaseCreateRequest();
            if (request.dictionaryId == 12) {
                // mail pendig
                mailRequest.setTemplateId(17L);
            } else if (request.dictionaryId == 13) {
                // approvato
                mailRequest.setTemplateId(9L);
            } else if (request.dictionaryId == 14) {
                // rigettato
                mailRequest.setTemplateId(10L);
            }
            mailRequest.setChannelId(channelDTO.getId());
            mailRequest.setAffiliateId(request.affiliateId);
            mailService.invio(mailRequest);
        }

        // setto categories
        if (StringUtils.isNotBlank(request.getCategories()) && !request.getCategories().equals("null")) {
            Arrays.stream(request.getCategories().split(",")).map(s -> channelCategoryBusiness.create(new ChannelCategoryBusiness.BaseCreateRequest(channelDTO.getId(), Long.valueOf(s)))).collect(Collectors.toList());
        }

        return channelDTO;
    }

    // GET BY ID
    public ChannelDTO findById(Long id) {
        Channel channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Channel", id));
        return ChannelDTO.from(channel);
    }

    // DELETE BY ID
    public void delete(Long id) {
        Channel channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Channel", id));
        try {
            // canello rif categorie canale
            channel.getChannelCategories().forEach(channelCategory -> channelCategoryBusiness.delete(channelCategory.getId()));

            // cancello riferimento
            AffiliateChannelCommissionCampaignBusiness.Filter rr = new AffiliateChannelCommissionCampaignBusiness.Filter();
            rr.setChannelId(id);
            accc.search(rr, PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc("id")))).forEach(affiliateChannelCommissionCampaignDTO -> accc.delete(affiliateChannelCommissionCampaignDTO.getId()));

            // cancello Canale
            repository.deleteById(id);
        } catch (ConstraintViolationException ex) {
            throw ex;
        } catch (Exception ee) {
            throw new PostgresDeleteCleveradException(ee);
        }

    }

    public void deleteByIdAffiliate(Long idAffilaite) {
        if (jwtUserDetailsService.isAdmin()) {
            Page<Channel> page = repository.findByAffiliateId(idAffilaite, PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc("id"))));
            page.stream().forEach(channel -> this.delete(channel.getId()));
        }
    }

    // SEARCH PAGINATED
    public Page<ChannelDTO> search(Filter request, Pageable pageableRequest) {
        Page<AffiliateChannelCommissionCampaignDTO> searchACCC = null;
        if (!jwtUserDetailsService.isAdmin()) {
            request.setAffiliateId(jwtUserDetailsService.getAffiliateId());
            request.setDictionaryId(13L);

            AffiliateChannelCommissionCampaignBusiness.Filter rr = new AffiliateChannelCommissionCampaignBusiness.Filter();
            rr.setAffiliateId(jwtUserDetailsService.getAffiliateId());
            searchACCC = accc.search(rr, pageableRequest);
        }

        Page<Channel> page = repository.findAll(getSpecification(request), PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id"))));

        if (searchACCC != null) {
            List<Long> channelsACCC = searchACCC.stream().map(dtos -> repository.findById(dtos.getChannelId()).get().getId()).collect(Collectors.toList());
            List<Channel> channels = new ArrayList<>();
            page.stream().forEach(channel -> {
                Long id = channel.getId();
                if (channelsACCC.contains(id)) {
                    channels.add(channel);
                }
            });
            page = new PageImpl<>(channels.stream().distinct().collect(Collectors.toList()));
        }

        return page.map(ChannelDTO::from);
    }

    // UPDATE
    public ChannelDTO update(Long id, Filter filter) {
        Channel channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Channel", id));
        filter.setStatus(true);
        filter.setId(id);
        mapper.map(filter, channel);
        channel.setDictionary(dictionaryRepository.findById(filter.dictionaryId).orElseThrow(() -> new ElementCleveradException("Dictionary", filter.dictionaryId)));
        channel.setDictionaryType(dictionaryRepository.findById(filter.typeId).orElseThrow(() -> new ElementCleveradException("Type", filter.typeId)));
        channel.setDictionaryOwner(dictionaryRepository.findById(filter.ownerId).orElseThrow(() -> new ElementCleveradException("Owner", filter.ownerId)));
        channel.setDictionaryBusinessType(dictionaryRepository.findById(filter.businessTypeId).orElseThrow(() -> new ElementCleveradException("Business Type", filter.businessTypeId)));
        channel.setLastModificationDate(LocalDateTime.now());

        MailService.BaseCreateRequest mailRequest = new MailService.BaseCreateRequest();
        mailRequest.setChannelId(filter.getId());
        mailRequest.setAffiliateId(channel.getAffiliate().getId());
        mailRequest.setChannelId(id);
        if (filter.dictionaryId == 12) {
            // mail pendig
        } else if (filter.dictionaryId == 13) {
            // approvato
            mailRequest.setTemplateId(9L);
            mailService.invio(mailRequest);
        } else if (filter.dictionaryId == 14) {
            // rigettato
            mailRequest.setTemplateId(10L);
            mailService.invio(mailRequest);
        }

        // SET Category - cancello precedenti
        channelCategoryBusiness.deleteByChannelID(id);

        // setto nuvoi
        if (filter.getCategoryList() != null && !filter.getCategoryList().isEmpty()) {
            Set<ChannelCategory> collect = filter.getCategoryList().stream().map(ss -> channelCategoryBusiness.createEntity(new ChannelCategoryBusiness.BaseCreateRequest(id, ss))).collect(Collectors.toSet());
            channel.setChannelCategories(collect);
        }

        return ChannelDTO.from(repository.save(channel));
    }

    public Page<ChannelDTO> getbyIdAffiliateChannelCommissionTemplate(Long id, Pageable pageableRequest) {
        AffiliateChannelCommissionCampaignBusiness.Filter rr = new AffiliateChannelCommissionCampaignBusiness.Filter();
        rr.setAffiliateId(id);
        Page<AffiliateChannelCommissionCampaignDTO> search = accc.search(rr, pageableRequest);
        List<Channel> channelList = search.stream().map(affiliateChannelCommissionCampaignDTO -> repository.findById(affiliateChannelCommissionCampaignDTO.getChannelId()).get()).collect(Collectors.toList());
        Page<Channel> page = new PageImpl<>(channelList.stream().distinct().collect(Collectors.toList()));
        return page.map(ChannelDTO::from);
    }

    public Page<ChannelDTO> getbyIdAffiliateAll(Pageable pageableRequest) {
        if (jwtUserDetailsService.isAdmin()) {
            Filter request = new Filter();
            Page<Channel> page = repository.findAll(getSpecification(request), PageRequest.of(pageableRequest.getPageNumber(), pageableRequest.getPageSize(), Sort.by(Sort.Order.desc("id"))));
            return page.map(ChannelDTO::from);
        } else {
            Page<Channel> page = repository.findByAffiliateId(jwtUserDetailsService.getAffiliateId(), pageableRequest);
            return page.map(ChannelDTO::from);
        }
    }

    public List<Long> getbyIdAffiliateAllActive(Long affiliateId) {
        Page<Channel> page = repository.findByAffiliateIdAndStatus(affiliateId, true, PageRequest.of(0, Integer.MAX_VALUE));
        return page.map(ChannelDTO::from).stream().map(ChannelDTO::getId).collect(Collectors.toList());
    }

    public Page<ChannelDTO> getbyIdUser(Long id, Pageable pageableRequest) {
        AffiliateChannelCommissionCampaignBusiness.Filter rr = new AffiliateChannelCommissionCampaignBusiness.Filter();
        rr.setAffiliateId(userBusiness.findById(id).getAffiliateId());
        Page<AffiliateChannelCommissionCampaignDTO> search = accc.search(rr, pageableRequest);

        List<Channel> channelList = search.stream().map(dtos -> repository.findById(dtos.getChannelId()).get()).collect(Collectors.toList());

        Page<Channel> page = new PageImpl<>(channelList.stream().distinct().collect(Collectors.toList()));
        return page.map(ChannelDTO::from);
    }

    public List<Long> getBrandBuddies(Long affiliateId) {
        Page<Channel> page = repository.findByAffiliateIdAndStatus(affiliateId, true, PageRequest.of(0, Integer.MAX_VALUE));
        return page.map(ChannelDTO::from).stream().map(ChannelDTO::getId).collect(Collectors.toList());
    }

    public Page<ChannelDTO> getbyIdCampaignPrefiltrato(Long campaignId, Pageable pageableRequest) {
        AffiliateChannelCommissionCampaignBusiness.Filter rr = new AffiliateChannelCommissionCampaignBusiness.Filter();
        rr.setAffiliateId(jwtUserDetailsService.getAffiliateId());
        rr.setCampaignId(campaignId);
        Page<AffiliateChannelCommissionCampaignDTO> search = accc.search(rr, pageableRequest);

        List<Channel> channelList = search.stream().map(dtos -> repository.findById(dtos.getChannelId()).get()).collect(Collectors.toList());

        Page<Channel> page = new PageImpl<>(channelList.stream().distinct().collect(Collectors.toList()));
        return page.map(ChannelDTO::from);
    }

    //  GET TIPI
    public Page<DictionaryDTO> getTypes() {
        return dictionaryBusiness.getTypeChannel();
    }

    public Page<DictionaryDTO> getBusinessTypes() {
        return dictionaryBusiness.getBusinessTypeChannel();
    }

    public ChannelDTO disable(Long id) {
        Channel channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Channel", id));
        channel.setStatus(false);
        return ChannelDTO.from(repository.save(channel));
    }

    public ChannelDTO enable(Long id) {
        Channel channel = repository.findById(id).orElseThrow(() -> new ElementCleveradException("Channel", id));
        channel.setStatus(true);
        return ChannelDTO.from(repository.save(channel));
    }

    /**
     * ============================================================================================================
     **/
    private Specification<Channel> getSpecification(Filter request) {
        return (root, query, cb) -> {
            Predicate completePredicate;
            List<Predicate> predicates = new ArrayList<>();

            if (request.getId() != null) {
                predicates.add(cb.equal(root.get("id"), request.getId()));
            }
            if (request.getName() != null) {
                predicates.add(cb.like(cb.upper(root.get("name")), "%" + request.getName().toUpperCase() + "%"));
            }
            if (request.getShortDescription() != null) {
                predicates.add(cb.like(cb.upper(root.get("shortDescription")), "%" + request.getShortDescription().toUpperCase() + "%"));
            }
            if (request.getTypeId() != null) {
                predicates.add(cb.equal(root.get("dictionaryType").get("id"), request.getTypeId()));
            }
            if (request.getDictionaryId() != null) {
                predicates.add(cb.equal(root.get("dictionary").get("id"), request.getDictionaryId()));
            }
            if (request.getBusinessTypeId() != null) {
                predicates.add(cb.equal(root.get("dictionaryBusinessType").get("id"), request.getBusinessTypeId()));
            }
            if (request.getOwnerId() != null) {
                predicates.add(cb.equal(root.get("dictionaryOwner").get("id"), request.getOwnerId()));
            }
            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }
            if (request.getAffiliateId() != null) {
                predicates.add(cb.equal(root.get("affiliate").get("id"), request.getAffiliateId()));
            }
            if (request.getCountry() != null) {
                predicates.add(cb.equal(root.get("country"), request.getCountry()));
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

            if (request.getIdListIn() != null) {
                CriteriaBuilder.In<Long> in = cb.in(root.get("id"));
                for (Long id : request.getIdListIn())
                    in.value(id);
                predicates.add(in);
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
        private String approvazione;
        private String url;
        private String dimension;
        private String country;
        private Long ownerId;
        private String categories;
        private Long affiliateId;
        private Long dictionaryId;
        private Long typeId;
        private Boolean status;
        private Long businessTypeId;
        private Boolean registrazione;

    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class Filter {
        private Long id;

        private String name;
        private String shortDescription;
        private String approvazione;
        private String url;
        private String dimension;
        private String country;
        private List<Long> categoryList;
        private Long ownerId;
        private Long affiliateId;
        private Long dictionaryId;
        private Long typeId;
        private Boolean status;
        private Long businessTypeId;

        private Instant creationDateFrom;
        private Instant creationDateTo;
        private Instant lastModificationDateFrom;
        private Instant lastModificationDateTo;

        private List<Long> idListIn;
    }

}