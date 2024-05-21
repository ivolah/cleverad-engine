package it.cleverad.engine.persistence.model.service;

public interface QueryTransaction {

    Long getid();

    String getTipo();

    String getCreationdate();

    String getDateTime();

    Long getStatusId();

    String getStatusName();

    Long getDictionaryId();

    String getDictionaryName();

    String getAffiliateid();

    String getAffiliateName();

    Long getChannelid();

    String getChannelName();

    Long getCampaignId();

    String getCampaignName();

    Long getMediaid();

    String getMediaName();

    Long getCommissionId();

    String getCommissionName();

    String getCommissionValue();

    String getCommissionValueRigettato();

    String getValue();

    String getValueRigettato();

    Long getRevenueid();

    String getRevenue();

    String getRevenuerigettato();

    Long getClickNumber();

    Long getClickNumberRigettato();

    Long getImpressionNumber();

    Long getLeadNumber();

    String getData();

    Long getWalletId();

    String getPayoutPresent();

    Long getPayoutId();

    String getPayoutReference();

}