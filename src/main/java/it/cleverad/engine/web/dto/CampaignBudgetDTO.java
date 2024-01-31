package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.CampaignBudget;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CampaignBudgetDTO {

    private Long id;
    private LocalDateTime creationDate;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long campaignId;
    private String campaignName;
    private Boolean campaignStatus;
    private Long advertiserId;
    private String advertiserName;
    private Long plannerId;
    private String plannerName;
    private Long canaleId;
    private String canaleName;
    private Boolean prenotato;
    private Long tipologiaId;
    private String tipologiaNome;
    private Integer capIniziale;
    private Double payout;
    private Double budgetIniziale;
    private Integer capErogato;
    private Integer capVolume;
    private Double capPc;
    private Double budgetErogato;
    private Double commissioniErogate;
    private Double revenuePC;
    private Double revenue;
    private Double scarto;
    private Double budgetErogatops;
    private Double commissioniErogateps;
    private Double revenuePCPS;
    private Double revenuePS;
    private Double revenueDay;
    private String materiali;
    private String note;
    private Integer capFatturabile;
    private Double fatturato;
    private Boolean status;
    private Boolean statoFatturato;
    private Boolean statoPagato;
    private LocalDate invoiceDueDate;

    private Integer volume;
    private LocalDate volumeDate;
    private Integer volumeDelta;

    private List<FileCampaignBudgetInvoiceDTO> fileCampaignBudgetInvoices;
    private List<FileCampaignBudgetOrderDTO> fileCampaignBudgetOrders;

    public static CampaignBudgetDTO from(CampaignBudget campaignBudget) {

        List<FileCampaignBudgetInvoiceDTO> invoices = null;
        if (campaignBudget.getFileCampaignBudgetInvoices() != null) {
            invoices = campaignBudget.getFileCampaignBudgetInvoices().stream().map(invoice -> {
                FileCampaignBudgetInvoiceDTO dto = new FileCampaignBudgetInvoiceDTO();
                dto.setId(invoice.getId());
                dto.setName(invoice.getName());
                dto.setPath(invoice.getPath());
                dto.setType(invoice.getType());
                return dto;
            }).collect(Collectors.toList());
        }

        List<FileCampaignBudgetOrderDTO> orders = null;
        if (campaignBudget.getFileCampaignBudgetOrders() != null) {
            orders = campaignBudget.getFileCampaignBudgetOrders().stream().map(invoice -> {
                FileCampaignBudgetOrderDTO dto = new FileCampaignBudgetOrderDTO();
                dto.setId(invoice.getId());
                dto.setName(invoice.getName());
                dto.setPath(invoice.getPath());
                dto.setType(invoice.getType());
                return dto;
            }).collect(Collectors.toList());
        }

        return new CampaignBudgetDTO(campaignBudget.getId(), campaignBudget.getCreationDate(), campaignBudget.getStartDate(), campaignBudget.getEndDate(),
                campaignBudget.getCampaign() != null ? campaignBudget.getCampaign().getId() : null,
                campaignBudget.getCampaign() != null ? campaignBudget.getCampaign().getName() : null,
                campaignBudget.getCampaign() != null ? campaignBudget.getCampaign().getStatus() : null,
                campaignBudget.getAdvertiser() != null ? campaignBudget.getAdvertiser().getId() : null,
                campaignBudget.getAdvertiser() != null ? campaignBudget.getAdvertiser().getName() : null,
                campaignBudget.getPlanner() != null ? campaignBudget.getPlanner().getId() : null,
                campaignBudget.getPlanner() != null ? campaignBudget.getPlanner().getName() : null,
                campaignBudget.getCanali() != null ? campaignBudget.getCanali().getId() : null,
                campaignBudget.getCanali() != null ? campaignBudget.getCanali().getName() : null,
                campaignBudget.getPrenotato(),
                campaignBudget.getDictionary() != null ? campaignBudget.getDictionary().getId() : null,
                campaignBudget.getDictionary() != null ? campaignBudget.getDictionary().getName() : null,
                campaignBudget.getCapIniziale(),
                campaignBudget.getPayout(),
                campaignBudget.getBudgetIniziale(),
                campaignBudget.getCapErogato(),
                campaignBudget.getCapVolume(),
                campaignBudget.getCapPc(),
                campaignBudget.getBudgetErogato(),
                campaignBudget.getCommissioniErogate(),
                campaignBudget.getRevenuePC(),
                campaignBudget.getRevenue(),
                campaignBudget.getScarto(), campaignBudget.getBudgetErogatops(), campaignBudget.getCommissioniErogateps(),
                campaignBudget.getRevenuePCPS(), campaignBudget.getRevenuePS(), campaignBudget.getRevenueDay(), campaignBudget.getMateriali(),
                campaignBudget.getNote(), campaignBudget.getCapFatturabile(), campaignBudget.getFatturato(), campaignBudget.getStatus(),
                campaignBudget.getStatoFatturato(), campaignBudget.getStatoPagato(), campaignBudget.getInvoiceDueDate(),
                campaignBudget.getVolume(), campaignBudget.getVolumeDate(), campaignBudget.getVolumeDelta(),
                invoices, orders);
    }

}