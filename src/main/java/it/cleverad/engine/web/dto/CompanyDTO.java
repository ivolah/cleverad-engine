package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.Company;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class CompanyDTO {

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

    public CompanyDTO(long id, String name, String vatNumber, String street, String streetNumber, String city, String zipCode, String primaryMail, String secondaryMail, Boolean status, LocalDateTime creationDate, LocalDateTime lastModificationDate, List<BasicCampaignDTO> basicCampaignDTOS) {
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
    }

    public static CompanyDTO from(Company company) {

        List<BasicCampaignDTO> collect = null;
        if (company.getCampaigns() != null) {
            collect = company.getCampaigns().stream().map(campaign -> {
                BasicCampaignDTO campaignDTO = new BasicCampaignDTO();
                campaignDTO.setId(campaign.getId());
                campaignDTO.setName(campaign.getName());
                campaignDTO.setShortDescription(campaign.getShortDescription());
                campaignDTO.setLongDescription(campaign.getLongDescription());
                campaignDTO.setStatus(campaign.getStatus());
                return campaignDTO;
            }).collect(Collectors.toList());
        }

        return new CompanyDTO(company.getId(), company.getName(), company.getVatNumber(), company.getStreet(), company.getStreetNumber(), company.getCity(), company.getZipCode(), company.getPrimaryMail(), company.getSecondaryMail(), company.getStatus(), company.getCreationDate(), company.getLastModificationDate(), collect);
    }

}
