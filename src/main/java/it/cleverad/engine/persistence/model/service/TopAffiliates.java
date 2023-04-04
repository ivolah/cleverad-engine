package it.cleverad.engine.persistence.model.service;

public interface TopAffiliates {


    Long getAffiliateName();
    String getAffiliateId();
    Long getChannelId();
    String getChannelName();

    Long getDictionaryId();
    String getDictionaryName();

    Long getImpressionNumber();
    Long getClickNumber();
    Long getLeadNumber();

    Long getCTR();
    Long getLR();

    Double getCommission();
    Double getRevenue();

    Double getMargine();
    Double getMarginePC();

    Double getEcpm();
    Double getEcpc();
    Double getEcpl();


}
