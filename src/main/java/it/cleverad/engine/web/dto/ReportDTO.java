package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.Report;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ReportDTO {

    private Long id;
    private String nome;
    private String descrizione;

    public static ReportDTO from(Report report) {
        return new ReportDTO(
                report.getId(),
                report.getName(),
                report.getDescription()
        );
    }

}
