package it.cleverad.engine.persistence.model.service;

public interface TopCampaings {

    String getAffiliateName();
    String getAffiliateId();
    Long getCampaignId();
    String getCampaignName();
    String getCampaignname();
    Long getDictionaryId();
    String getDictionaryName();
    Long getFileid();

    Long getImpressionNumber();
    Long getClickNumber();
    Long getLeadNumber();

    Double getCTR();
    Double getLR();

    Double getCommission();
    Double getRevenue();

    Double getMargine();
    Double getMarginePC();

    Double getEcpm();
    Double getEcpc();
    Double getEcpl();

    Double getBudget();
    Double getBudgetPC();
    Double getBudgetGivenPC();
    Double getInitialBudget();

    String getChannelId();
    String getChannelName();

}
