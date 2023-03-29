package it.cleverad.engine.persistence.model.service;

public interface TopCampaings {

    Long getClickNumber();
    Long getCampaignId();
    String getCampaignName();
    Long getCTR();
    Long getLT();
    Long getDictionaryId();
    Double getCommission();
    Double getRevenue();
    Double getMargine();
    Double getMarginePC();
    Double getEccpm();
    Double getEcpc();
    Double getEcpl();

}
