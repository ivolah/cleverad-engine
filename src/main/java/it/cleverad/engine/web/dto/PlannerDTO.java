package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.Planner;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlannerDTO {

    private long id;
    private String name;
    private String surname;
    private String email;
    private String phonePrefix;
    private String phone;
    private String mobilePrefix;
    private String mobile;
    private String skype;

    private Boolean status;
    private LocalDateTime creationDate;

    public static PlannerDTO from(Planner planner) {
        return new PlannerDTO(planner.getId(),
                planner.getName(), planner.getSurname(), planner.getEmail(), planner.getPhonePrefix(), planner.getPhone(), planner.getMobilePrefix(), planner.getMobile(),
                planner.getSkype(), planner.getStatus(), planner.getCreationDate());
    }

}