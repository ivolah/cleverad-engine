package it.cleverad.engine.persistence.model.service;

public interface TopCampaings {


    Long getCampaignId();
    String getCampaignName();
    Long getDictionaryId();
    String getDictionaryName();


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

}
