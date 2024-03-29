package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.Advertiser;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class AdvertiserDTO {

    private long id;
    private String name;
    private String vatNumber;
    private String street;
    private String streetNumber;
    private String city;
    private String zipCode;
    private String primaryMail;
    private String secondaryMail;
    private Boolean status;
    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    private List<BasicCampaignDTO> basicCampaignDTOS;

    private String country;

    private Long termId;
    private String termName;
    private Long vatId;
    private String vatName;

    public AdvertiserDTO(long id, String name, String vatNumber, String street, String streetNumber, String city, String zipCode, String primaryMail, String secondaryMail, Boolean status, LocalDateTime creationDate, LocalDateTime lastModificationDate, List<BasicCampaignDTO> basicCampaignDTOS, String country,
                         Long termId,
                         String termName,
                         Long vatId,
                         String vatName
    ) {
        this.id = id;
        this.name = name;
        this.vatNumber = vatNumber;
        this.street = street;
        this.streetNumber = streetNumber;
        this.city = city;
        this.zipCode = zipCode;
        this.primaryMail = primaryMail;
        this.secondaryMail = secondaryMail;
        this.status = status;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
        this.basicCampaignDTOS = basicCampaignDTOS;
        this.country = country;
        this.termId = termId;
        this.termName = termName;
        this.vatId = vatId;
        this.vatName = vatName;

    }

    public static AdvertiserDTO from(Advertiser advertiser) {

        List<BasicCampaignDTO> collect = null;
        if (advertiser.getCampaigns() != null) {
            collect = advertiser.getCampaigns().stream().map(campaign -> {
                BasicCampaignDTO campaignDTO = new BasicCampaignDTO();
                campaignDTO.setId(campaign.getId());
                campaignDTO.setName(campaign.getName());
                campaignDTO.setShortDescription(campaign.getShortDescription());
                campaignDTO.setLongDescription(campaign.getLongDescription());
                campaignDTO.setStatus(campaign.getStatus());
                return campaignDTO;
            }).collect(Collectors.toList());
        }

        return new AdvertiserDTO(advertiser.getId(), advertiser.getName(), advertiser.getVatNumber(), advertiser.getStreet(), advertiser.getStreetNumber(), advertiser.getCity(), advertiser.getZipCode(), advertiser.getPrimaryMail(), advertiser.getSecondaryMail(), advertiser.getStatus(), advertiser.getCreationDate(), advertiser.getLastModificationDate(), collect, advertiser.getCountry(),

                advertiser.getDictionaryTermType() != null ? advertiser.getDictionaryTermType().getId() : null,
                advertiser.getDictionaryTermType() != null ? advertiser.getDictionaryTermType().getName() : null,

                advertiser.getDictionaryVatType() != null ? advertiser.getDictionaryVatType().getId() : null,
                advertiser.getDictionaryVatType() != null ? advertiser.getDictionaryVatType().getName() : null
                );
    }

}