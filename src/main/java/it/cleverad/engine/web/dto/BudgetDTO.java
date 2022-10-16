package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.Budget;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
public class BudgetDTO {

    private Long id;
    private Long idAffiliate;
    private Long budget;
    private Date dueDate;

    private String affiliateName;

    private Boolean status;
    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    public BudgetDTO(Long id, Long idAffiliate, Long budget, Date dueDate, Boolean status, LocalDateTime creationDate, LocalDateTime lastModificationDate) {
        this.id = id;
        this.idAffiliate = idAffiliate;
        this.budget = budget;
        this.dueDate = dueDate;
        this.status = status;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
    }

    public static BudgetDTO from(Budget budget) {
        return new BudgetDTO(budget.getId(), budget.getIdAffiliate(), budget.getBudget(), budget.getDueDate(), budget.getStatus(), budget.getCreationDate(), budget.getLastModificationDate());
    }

}
