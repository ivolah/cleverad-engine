package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.Affiliate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    private String firstName;
    private String lastName;

    private Long companytypeId;
    private String companytypeNome;

    private Boolean status;
    private Long statusId;
    private String statusNome;
    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    private List<BasicCampaignDTO> basicCampaignDTOS;
    private List<FileAffiliateDTO> fileAffiliates;

    private Boolean cb;
    private Boolean brandbuddies;

    private Long termId;
    private String termName;
    private Long vatId;
    private String vatName;

    private String globalPixel;
    private String globalPixelValue;

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
                fileAffiliateDTO.setId(fileAffiliate.getId());
                fileAffiliateDTO.setName(fileAffiliate.getName());
                fileAffiliateDTO.setType(fileAffiliate.getType());
                return fileAffiliateDTO;
            }).collect(Collectors.toList());
        }

        return new AffiliateDTO(affiliate.getId(), affiliate.getName(), affiliate.getVatNumber(), affiliate.getStreet(), affiliate.getStreetNumber(), affiliate.getCity(), affiliate.getProvince(), affiliate.getZipCode(), affiliate.getPrimaryMail(), affiliate.getSecondaryMail(), affiliate.getCountry(), affiliate.getPhonePrefix(), affiliate.getPhoneNumber(), affiliate.getNote(), affiliate.getBank(), affiliate.getIban(), affiliate.getSwift(), affiliate.getPaypal(), affiliate.getFirstName(), affiliate.getLastName(),
                affiliate.getDictionaryCompanyType() != null ? affiliate.getDictionaryCompanyType().getId() : null,
                affiliate.getDictionaryCompanyType() != null ? affiliate.getDictionaryCompanyType().getName() : null,
                affiliate.getStatus(),
                affiliate.getDictionaryStatusType() != null ? affiliate.getDictionaryStatusType().getId() : null,
                affiliate.getDictionaryStatusType() != null ? affiliate.getDictionaryStatusType().getName() : null,
                affiliate.getCreationDate(), affiliate.getLastModificationDate(), listaCam, listaFile, affiliate.getCb(), affiliate.getBrandbuddies(),
                affiliate.getDictionaryTermType() != null ? affiliate.getDictionaryTermType().getId() : null,
                affiliate.getDictionaryTermType() != null ? affiliate.getDictionaryTermType().getName() : null,
                affiliate.getDictionaryVatType() != null ? affiliate.getDictionaryVatType().getId() : null,
                affiliate.getDictionaryVatType() != null ? affiliate.getDictionaryVatType().getName() : null,
                affiliate.getGlobalPixel(), affiliate.getGlobalPixelValue());
    }

    public static AffiliateDTO fromList(Affiliate affiliate) {
        return new AffiliateDTO(affiliate.getId(), affiliate.getName(), affiliate.getVatNumber(), affiliate.getStreet(), affiliate.getStreetNumber(), affiliate.getCity(), affiliate.getProvince(), affiliate.getZipCode(), affiliate.getPrimaryMail(), affiliate.getSecondaryMail(), affiliate.getCountry(), affiliate.getPhonePrefix(), affiliate.getPhoneNumber(), affiliate.getNote(), affiliate.getBank(), affiliate.getIban(), affiliate.getSwift(), affiliate.getPaypal(), affiliate.getFirstName(), affiliate.getLastName(),
                affiliate.getDictionaryCompanyType() != null ? affiliate.getDictionaryCompanyType().getId() : null,
                affiliate.getDictionaryCompanyType() != null ? affiliate.getDictionaryCompanyType().getName() : null,
                affiliate.getStatus(),
                affiliate.getDictionaryStatusType() != null ? affiliate.getDictionaryStatusType().getId() : null,
                affiliate.getDictionaryStatusType() != null ? affiliate.getDictionaryStatusType().getName() : null,
                affiliate.getCreationDate(), affiliate.getLastModificationDate(), null, null, affiliate.getCb(), affiliate.getBrandbuddies(),
                affiliate.getDictionaryTermType() != null ? affiliate.getDictionaryTermType().getId() : null,
                affiliate.getDictionaryTermType() != null ? affiliate.getDictionaryTermType().getName() : null,
                affiliate.getDictionaryVatType() != null ? affiliate.getDictionaryVatType().getId() : null,
                affiliate.getDictionaryVatType() != null ? affiliate.getDictionaryVatType().getName() : null,
                affiliate.getGlobalPixel(), affiliate.getGlobalPixelValue()
                );
    }

}