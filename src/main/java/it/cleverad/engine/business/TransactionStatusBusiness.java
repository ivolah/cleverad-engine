package it.cleverad.engine.business;

import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.persistence.model.service.QueryTransaction;
import it.cleverad.engine.persistence.repository.service.QueryRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@Transactional
public class TransactionStatusBusiness {

    @Autowired
    private QueryRepository queryRepository;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;

    /**
     * ============================================================================================================
     **/

    // GET BY ID
    public QueryTransaction findById(Long id) {
        QueryFilter request = new QueryFilter();
        if (jwtUserDetailsService.isAffiliate()) {
            request.setAffiliateId(jwtUserDetailsService.getAffiliateId());
        } else if (jwtUserDetailsService.isAdvertiser()) {
            request.setAdvertiserId(jwtUserDetailsService.getAdvertiserId());
        }
        // setto tipo
        QueryTransaction tq = queryRepository.listaTransazioni(
                        request.getCreationDateFrom(), request.getCreationDateTo().atTime(23, 59, 59, 99999), request.getStatusId(),
                        request.getDictionaryId(), request.getAffiliateId(), request.getChannelId(), request.getCampaignId(),
                        request.getMediaId(), request.getCommissionId(), request.getRevenueId(), request.getPayoutPresent(),
                        request.getPayoutId(), request.getAdvertiserId(), request.getValueNotZero(), request.getInDictionaryId(),
                        request.getNotInDictionaryId(), request.getInStausId(), request.getNotInStausId(), null)
                .stream().findFirst().orElse(null);
        return tq;
    }

    public Page<QueryTransaction> searchPrefiltratoN(TransactionStatusBusiness.QueryFilter request, Pageable pageableRequest) {

        List<Long> statusIds = new ArrayList<>();
        statusIds.add(72L);
        List<Long> dictIds = new ArrayList<>();
        dictIds.add(39L);
        dictIds.add(42L);
        dictIds.add(68L);

        if (jwtUserDetailsService.isAffiliate()) {
            request.setValueNotZero(true);
            request.setInStausId(statusIds);// nascodne delle transazioni
            request.setInDictionaryId(dictIds);// nascodne delle transazioni
            request.setAffiliateId(jwtUserDetailsService.getAffiliateId());
        } else if (jwtUserDetailsService.isAdvertiser()) {
            request.setValueNotZero(true);
            request.setInStausId(statusIds);// nascodne delle transazioni
            request.setInDictionaryId(dictIds);// nascodne delle transazioni
            request.setAdvertiserId(jwtUserDetailsService.getAdvertiserId());
        }

      List<String> orders = new ArrayList<>();
        if (request.getDataList() != null && request.getDataList().length() > 3) {
            log.info("Dentro :: ", request.getDataList());
            Arrays.stream(request.getDataList().split(",")).distinct().forEach(s -> {
                orders.add(s);
            });
        }

        List<QueryTransaction> listaTransazioni = new ArrayList<>();
        if (request != null) {
            if (request.getTipo() == null) {
                listaTransazioni = queryRepository.listaTransazioni(request.getCreationDateFrom(), request.getCreationDateTo() != null ? request.getCreationDateTo().atTime(23, 59, 59, 99999) : null, request.getStatusId(), request.getDictionaryId(), request.getAffiliateId(), request.getChannelId(), request.getCampaignId(), request.getMediaId(), request.getCommissionId(), request.getRevenueId(), request.getPayoutPresent(), request.getPayoutId(), request.getAdvertiserId(), request.getValueNotZero(), request.getInDictionaryId(), request.getNotInDictionaryId(), request.getInStausId(), request.getNotInStausId(), orders);
            } else if (request.getTipo().equals("CPC")) {
                listaTransazioni = queryRepository.listaTransazioniCPC(request.getCreationDateFrom(), request.getCreationDateTo() != null ? request.getCreationDateTo().atTime(23, 59, 59, 99999) : null, request.getStatusId(), request.getDictionaryId(), request.getAffiliateId(), request.getChannelId(), request.getCampaignId(), request.getMediaId(), request.getCommissionId(), request.getRevenueId(), request.getPayoutPresent(), request.getPayoutId(), request.getAdvertiserId(), request.getValueNotZero(), request.getInDictionaryId(), request.getNotInDictionaryId(), request.getInStausId(), request.getNotInStausId());
            } else if (request.getTipo().equals("CPL")) {
                listaTransazioni = queryRepository.listaTransazioniCPL(request.getCreationDateFrom(), request.getCreationDateTo() != null ? request.getCreationDateTo().atTime(23, 59, 59, 99999) : null, request.getStatusId(), request.getDictionaryId(), request.getAffiliateId(), request.getChannelId(), request.getCampaignId(), request.getMediaId(), request.getCommissionId(), request.getRevenueId(), request.getPayoutPresent(), request.getPayoutId(), request.getAdvertiserId(), request.getValueNotZero(), request.getInDictionaryId(), request.getNotInDictionaryId(), request.getInStausId(), request.getNotInStausId());
            } else if (request.getTipo().equals("CPM")) {
                listaTransazioni = queryRepository.listaTransazioniCPM(request.getCreationDateFrom(), request.getCreationDateTo() != null ? request.getCreationDateTo().atTime(23, 59, 59, 99999) : null, request.getStatusId(), request.getDictionaryId(), request.getAffiliateId(), request.getChannelId(), request.getCampaignId(), request.getMediaId(), request.getCommissionId(), request.getRevenueId(), request.getPayoutPresent(), request.getPayoutId(), request.getAdvertiserId(), request.getValueNotZero(), request.getInDictionaryId(), request.getNotInDictionaryId(), request.getInStausId(), request.getNotInStausId());
            } else if (request.getTipo().equals("CPS")) {
                listaTransazioni = queryRepository.listaTransazioniCPS(request.getCreationDateFrom(), request.getCreationDateTo() != null ? request.getCreationDateTo().atTime(23, 59, 59, 99999) : null, request.getStatusId(), request.getDictionaryId(), request.getAffiliateId(), request.getChannelId(), request.getCampaignId(), request.getMediaId(), request.getCommissionId(), request.getRevenueId(), request.getPayoutPresent(), request.getPayoutId(), request.getAdvertiserId(), request.getValueNotZero(), request.getInDictionaryId(), request.getNotInDictionaryId(), request.getInStausId(), request.getNotInStausId());
            }
        } else {
            listaTransazioni = queryRepository.listaTransazioni(
                    request.getCreationDateFrom(), request.getCreationDateTo() != null ? request.getCreationDateTo().atTime(23, 59, 59, 99999) : null, request.getStatusId(), request.getDictionaryId(), request.getAffiliateId(), request.getChannelId(), request.getCampaignId(), request.getMediaId(), request.getCommissionId(), request.getRevenueId(), request.getPayoutPresent(), request.getPayoutId(), request.getAdvertiserId(), request.getValueNotZero(), request.getInDictionaryId(), request.getNotInDictionaryId(), request.getInStausId(), request.getNotInStausId(), orders);
        }

        final int end = (int) Math.min((pageableRequest.getOffset() + pageableRequest.getPageSize()), listaTransazioni.size());
        return new PageImpl<>(listaTransazioni.stream().distinct().collect(Collectors.toList()).subList((int) pageableRequest.getOffset(), end), pageableRequest, listaTransazioni.size());
    }

    /**
     * ============================================================================================================
     **/

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public static class QueryFilter {
        public List<Long> inDictionaryId = new ArrayList<>();
        public List<Long> inStausId = new ArrayList<>();
        public List<Long> notInDictionaryId = new ArrayList<>();
        public List<Long> notInStausId = new ArrayList<>();
        private Long id;
        private String tipo;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate creationDateFrom;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate creationDateTo;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateTimeFrom;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private LocalDate dateTimeTo;
        private Long statusId;
        private String statusName;
        private Long dictionaryId;
        private String dictionaryName;
        private Long affiliateId;
        private String affiliateName;
        private Long channelId;
        private String channelName;
        private Long campaignId;
        private String campaignName;
        private Long mediaId;
        private String mediaName;
        private Long commissionId;
        private String commissionName;
        private Double commissionValue;
        private Double value;
        private Long revenueId;
        private Long revenue;
        private Long clickNumber;
        private Long impressionNumber;
        private Long leadNumber;
        private String data;
        private Long walletId;
        private Boolean payoutPresent;
        private Long payoutId;
        private String payoutReference;
        private Boolean valueNotZero;
        private String dataList;
        private String note;
        private Long advertiserId;
    }

}