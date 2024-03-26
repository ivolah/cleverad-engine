package it.cleverad.engine.persistence.model.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class StatDayCpc {
    private Long id;
    private Double totale;
    private Double valore;
    private Long campaignId;
    private String campaign;
    private Long affiliateId;
    private Long year;
    private Long month;
    private Long day;
    private Long week;
    private Long doy;
}