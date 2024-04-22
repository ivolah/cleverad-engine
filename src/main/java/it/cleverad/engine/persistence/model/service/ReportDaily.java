package it.cleverad.engine.persistence.model.service;

public interface ReportDaily {

    String giorno = null;
    Double commission = null;
    Double commissionRigettato = null;
    Double revenue = null;
    Double revenueRigettato = null;
    Double margine = null;
    Double marginePC = null;
    String ecpm = null;
    String ecpc = null;
    String ecpl = null;
    Long impressionNumber = null;
    Long clickNumber = null;
    Long leadNumber = null;
    String ctr = null;
    String lr = null;
    Long impressionNumberRigettato = null;
    Long leadNumberRigettato = null;
    Long clickNumberRigettato = null;

    String getGiorno();

    Double getCommission();

    Double getCommissionRigettato();

    Double getRevenue();

    Double getRevenueRigettato();

    Double getMargine();

    Double getMarginePC();

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

    Long getClickNumberRigettato();

    String setGiorno(String giorno);

    String setCommission(String commission);

    String setCommissionRigettato(String commissionRigettato);

    String setRevenue(String revenue);

    String setRevenueRigettato(String revenueRigettato);

    String setMargine(String margine);

    String setMarginePC(String marginePC);

    String setEcpm(String ecpm);

    String setEcpc(String ecpc);

    String setEcpl(String ecpl);

    Long setImpressionNumber(Long impressionNumber);

    Long setClickNumber(Long clickNumber);

    Long setLeadNumber(Long leadNumber);

    String setCTR(String CTR);

    String setLR(String LR);

    String setImpressionNumberRigettato(Long impressionNumberRigettato);

    String setImpressionNumberrigettato(Long impressionNumberrigettato);

    String setLeadNumberRigettato(Long leadNumberRigettato);

    String setClickNumberRigettato(Long clickNumberRigettato);
}