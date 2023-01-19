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
    private String province;
    private String zipCode;
    private String primaryMail;
    private String secondaryMail;
    private String country;
    private String phonePrefix;
    private String phoneNumber;

    private String note;

    private String bank;
    private String iban;
    private String swift;
    private String paypal;

    private Boolean status;
    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    private List<BasicCampaignDTO> basicCampaignDTOS;
    private List<FileAffiliateDTO> fileAffiliates;

    public AffiliateDTO(long id, String name, String vatNumber, String street, String streetNumber, String city, String province, String zipCode, String primaryMail, String secondaryMail, String country, String phonePrefix, String phoneNumber, String note, String bank, String iban, String swift, String paypal, Boolean status, LocalDateTime creationDate, LocalDateTime lastModificationDate, List<BasicCampaignDTO> basicCampaignDTOS, List<FileAffiliateDTO> fileAffiliates) {
        this.id = id;
        this.name = name;
        this.vatNumber = vatNumber;
        this.street = street;
        this.streetNumber = streetNumber;
        this.city = city;
        this.province = province;
        this.zipCode = zipCode;
        this.primaryMail = primaryMail;
        this.secondaryMail = secondaryMail;
        this.country = country;
        this.phonePrefix = phonePrefix;
        this.phoneNumber = phoneNumber;
        this.note = note;
        this.bank = bank;
        this.iban = iban;
        this.swift = swift;
        this.paypal = paypal;
        this.status = status;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
        this.basicCampaignDTOS = basicCampaignDTOS;
        this.fileAffiliates = fileAffiliates;
    }

    public static AffiliateDTO from(Affiliate affiliate) {

        List<BasicCampaignDTO> listaCam = null;
        if (affiliate.getAffiliateChannelCommissionCampaigns() != null) {
            listaCam = affiliate.getAffiliateChannelCommissionCampaigns().stream().map(affiliateCampaign -> {
                BasicCampaignDTO campaignDTO = new BasicCampaignDTO();
                campaignDTO.setId(affiliateCampaign.getCampaign().getId());
                campaignDTO.setName(affiliateCampaign.getCampaign().getName());
                campaignDTO.setShortDescription(affiliateCampaign.getCampaign().getShortDescription());
                campaignDTO.setLongDescription(affiliateCampaign.getCampaign().getLongDescription());
                campaignDTO.setStatus(affiliateCampaign.getCampaign().getStatus());
                return campaignDTO;
            }).collect(Collectors.toList());
        }

        List<FileAffiliateDTO> listaFile = null;
        if (affiliate.getFileAffiliates() != null) {
            listaFile = affiliate.getFileAffiliates().stream().map(fileAffiliate -> {
                FileAffiliateDTO fileAffiliateDTO = new FileAffiliateDTO();
                fileAffiliateDTO.setAffiliateId(fileAffiliate.getAffiliate().getId());
                fileAffiliateDTO.setAffiliateName(fileAffiliate.getAffiliate().getName());
                fileAffiliateDTO.setDictionaryId(fileAffiliate.getDictionary().getId());
                fileAffiliateDTO.setDictionaryValue(fileAffiliate.getDictionary().getName());
                fileAffiliateDTO.setCreationDate(fileAffiliate.getCreationDate());
                fileAffiliateDTO.setNote(fileAffiliate.getNote());
                fileAffiliateDTO.setId(fileAffiliate.getId());
                fileAffiliateDTO.setName(fileAffiliate.getName());
                fileAffiliateDTO.setType(fileAffiliate.getType());
                return fileAffiliateDTO;
            }).collect(Collectors.toList());
        }

        return new AffiliateDTO(affiliate.getId(), affiliate.getName(), affiliate.getVatNumber(), affiliate.getStreet(), affiliate.getStreetNumber(),
                affiliate.getCity(), affiliate.getProvince(), affiliate.getZipCode(), affiliate.getPrimaryMail(), affiliate.getSecondaryMail(),
                affiliate.getCountry(), affiliate.getPhonePrefix(), affiliate.getPhoneNumber(),
                affiliate.getNote(), affiliate.getBank(), affiliate.getIban(), affiliate.getSwift(), affiliate.getPaypal(),
                affiliate.getStatus(), affiliate.getCreationDate(), affiliate.getLastModificationDate(), listaCam, listaFile);
    }

}
