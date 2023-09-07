package it.cleverad.engine.persistence.model.service;

public interface ReportDaily {

    String getGiorno();

    String getCommission();

    String getCommissionRigettato();

    String getRevenue();

    String getRevenueRigettato();

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

    Long getImpressionNumberRigettato();

    Long getImpressionNumberrigettato();


    Long getLeadNumberRigettato();

    //Long getLeadNumberrigettato();


    Long getClickNumberRigettato();

    //Long getClickNumberrigettato();



}