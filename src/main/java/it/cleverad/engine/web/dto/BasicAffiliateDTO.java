package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.Affiliate;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BasicAffiliateDTO {

    private long id;
    private String name;
    private String primaryMail;
    private String idCommission;

    public BasicAffiliateDTO(long id, String name, String primaryMail) {
        this.id = id;
        this.name = name;
        this.primaryMail = primaryMail;
    }

    public static BasicAffiliateDTO from(Affiliate affiliate) {
        return new BasicAffiliateDTO(affiliate.getId(), affiliate.getName(), affiliate.getPrimaryMail());
    }

}
