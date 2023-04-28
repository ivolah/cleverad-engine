package it.cleverad.engine.persistence.model.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "v_widget_campaign_day_cpl")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class WidgetCampaignDayCpl {

    @Id
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "campaign_id")
    private Long campaignId;
    private String campaign;
    @Column(name = "affiliate_id")
    private Long affiliateId;
    private Double valore;
    private Long year;
    private Long month;
    private Long day;
    private Long week;
    private Long doy;
}
