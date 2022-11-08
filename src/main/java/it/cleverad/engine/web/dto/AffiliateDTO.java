package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.Affiliate;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class AffiliateDTO {

    private long id;
    private String name;
    private String vatNumber;
    private String street;
    private String streetNumber;
    private String city;
    private String zipCode;
    private String primaryMail;
    private String secondaryMail;

    private String iban;
    private String swift;
    private String paypal;

    private Boolean status;
    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    private List<BasicCampaignDTO> basicCampaignDTOS;

    public AffiliateDTO(long id, String name, String vatNumber, String street, String streetNumber, String city, String zipCode, String primaryMail, String secondaryMail, String iban, String swift, String paypal, Boolean status, LocalDateTime creationDate, LocalDateTime lastModificationDate, List<BasicCampaignDTO> basicCampaignDTOS) {
        this.id = id;
        this.name = name;
        this.vatNumber = vatNumber;
        this.street = street;
        this.streetNumber = streetNumber;
        this.city = city;
        this.zipCode = zipCode;
        this.primaryMail = primaryMail;
        this.secondaryMail = secondaryMail;
        this.iban = iban;
        this.swift = swift;
        this.paypal = paypal;
        this.status = status;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
        this.basicCampaignDTOS = basicCampaignDTOS;
    }

    public static AffiliateDTO from(Affiliate affiliate) {

        List<BasicCampaignDTO> collect = null;
        if (affiliate.getAffiliateCampaigns() != null) {
            collect = affiliate.getAffiliateCampaigns().stream().map(affiliateCampaign -> {
                BasicCampaignDTO campaignDTO = new BasicCampaignDTO();
                campaignDTO.setId(affiliateCampaign.getCampaign().getId());
                campaignDTO.setName(affiliateCampaign.getCampaign().getName());
                campaignDTO.setShortDescription(affiliateCampaign.getCampaign().getShortDescription());
                campaignDTO.setLongDescription(affiliateCampaign.getCampaign().getLongDescription());
                campaignDTO.setStatus(affiliateCampaign.getCampaign().getStatus());
                return campaignDTO;
            }).collect(Collectors.toList());
        }

        return new AffiliateDTO(affiliate.getId(), affiliate.getName(), affiliate.getVatNumber(), affiliate.getStreet(), affiliate.getStreetNumber(), affiliate.getCity(), affiliate.getZipCode(), affiliate.getPrimaryMail(), affiliate.getSecondaryMail(), affiliate.getIban(), affiliate.getSwift(), affiliate.getPaypal(),
                affiliate.getStatus(), affiliate.getCreationDate(), affiliate.getLastModificationDate(), collect);
    }

}
