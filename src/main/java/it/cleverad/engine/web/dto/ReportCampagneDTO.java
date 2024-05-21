package it.cleverad.engine.web.dto;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ReportCampagneDTO {
    private Double commission;
    private Double commissionRigettato;
    private Double revenue;
    private Double revenueRigettato;
    private Double margine;
    private Double marginePC;
    private String ecpm;
    private String ecpc;
    private String ecpl;
    private Long impressionNumber;
    private Long clickNumber;
    private Long leadNumber;
    private String ctr;
    private String lr;
    private Long leadNumberRigettato;
    private Long clickNumberRigettato;
    private Double initialBudget;
    private Double budget;
    private Double budgetGivenPC;
    private Double budgetPC;
    private Long campaignId;
    private String campaignName;
}