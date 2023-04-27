package it.cleverad.engine.persistence.model.service;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "v_widget_cpm")
@Getter
@Setter
@NoArgsConstructor
public class WidgetCPM {
    @Id
    private Long campaignId;
    private String campaignName;
    private Long affiliateId;
    private String affiliateName;
    private Long channelId;
    private String channelName;
    private Long dictionaryId;
    private LocalDateTime date;
    private Double commssion;
    private Long impression;
    private Double ecpm;
    private Double revenue;
}
