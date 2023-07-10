package it.cleverad.engine.persistence.model.service;

public interface ReportDaily {

    String getGiorno();

    String getCommission();

    String getRevenue();

    String getMargine();

    String getMarginePC();

    String getEcpm();

    String getEcpc();

    String getEcpl();

    Long getImpressionNumber();

    Long getClickNumber();

    Long getLeadNumber();

    String getCTR();

    String getLR();


    Long getImpressionNumberrigettato();

    Long getClickNumberrigettato();

    Long getLeadNumberrigettato();

}
