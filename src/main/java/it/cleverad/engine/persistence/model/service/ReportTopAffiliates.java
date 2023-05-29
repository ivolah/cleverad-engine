package it.cleverad.engine.persistence.model.service;

public interface ReportTopAffiliates {

    String getAffiliateName();
    String getAffiliateId();
    Long getChannelId();
    String getChannelName();

    Long getDictionaryId();
    String getDictionaryName();


    Double getCommission();
    Double getRevenue();

    Double getMargine();
    Double getMarginePC();

    Double getEcpm();
    Double getEcpc();
    Double getEcpl();

    Long getImpressionNumber();
    Long getClickNumber();
    Long getLeadNumber();

    Double getCTR();
    Double getLR();

    Double getBudget();
    Double getBudgetPC();

}
