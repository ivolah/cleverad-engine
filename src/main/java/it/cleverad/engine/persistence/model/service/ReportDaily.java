package it.cleverad.engine.persistence.model.service;

public interface ReportDaily {

    String getGiorno();
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

}
