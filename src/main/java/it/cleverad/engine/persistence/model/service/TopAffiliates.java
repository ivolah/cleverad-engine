package it.cleverad.engine.persistence.model.service;

public interface TopAffiliates {

    Long getClickNumber();
    Long getAffiliateName();
    String getAffiliateId();
    Long getChannelId();
    String getChannelName();
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
