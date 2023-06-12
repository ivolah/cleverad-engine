package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.Representative;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class RepresentativeDTO {

    private long id;
    private String name;
    private String surname;
    private String email;
    private String phonePrefix;
    private String phone;
    private String mobilePrefix;
    private String mobile;

    private Long affiliateId;
    private String affiliateName;

    private Long advertiserId;
    private String advertiserName;

    private Long roleId;
    private String role;

    private Boolean status;
    private LocalDateTime creationDate;

    public RepresentativeDTO(long id, String name, String surname, String email, String phonePrefix, String phone, String mobilePrefix, String mobile, Long affiliateId, String affiliateName, Long advertiserId, String advertiserName, Long roleId, String role, Boolean status, LocalDateTime creationDate) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phonePrefix = phonePrefix;
        this.phone = phone;
        this.mobilePrefix = mobilePrefix;
        this.mobile = mobile;
        this.affiliateId = affiliateId;
        this.affiliateName = affiliateName;
        this.advertiserId = advertiserId;
        this.advertiserName = advertiserName;
        this.roleId = roleId;
        this.role = role;
        this.status = status;
        this.creationDate = creationDate;
    }

    public static RepresentativeDTO from(Representative representative) {
        return new RepresentativeDTO(representative.getId(),
                representative.getName(), representative.getSurname(), representative.getEmail(), representative.getPhonePrefix(), representative.getPhone(), representative.getMobilePrefix(), representative.getMobile(),

                representative.getAffiliate() != null ? representative.getAffiliate().getId() : null, representative.getAffiliate() != null ? representative.getAffiliate().getName() : null,
                representative.getAdvertiser() != null ? representative.getAdvertiser().getId() : null, representative.getAdvertiser() != null ? representative.getAdvertiser().getName() : null,

                representative.getDictionary().getId(), representative.getDictionary().getName(),
                representative.getStatus(), representative.getCreationDate());
    }

}
