package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.CampaignCookie;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CampaignCookieDTO {

    private Long id;
    private Long cookieId;
    private String name;
    private String value;

    public CampaignCookieDTO(Long id, Long cookieId, String name, String value) {
        this.id = id;
        this.cookieId = cookieId;
        this.name = name;
        this.value = value;
    }

    public static CampaignCookieDTO from(CampaignCookie campaignCookie) {
        return new CampaignCookieDTO(campaignCookie.getId(), campaignCookie.getCookie().getId(), campaignCookie.getCookie().getName(), campaignCookie.getCookie().getValue());
    }

}
