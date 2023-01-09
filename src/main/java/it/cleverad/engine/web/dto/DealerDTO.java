package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.Dealer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DealerDTO {

    private long id;
    private String name;
    private String surname;
    private String email;
    private String phonePrefix;
    private String phone;
    private String mobilePrefix;
    private String mobile;

    private Boolean status;
    private LocalDateTime creationDate;

    public static DealerDTO from(Dealer dealer) {
        return new DealerDTO(dealer.getId(),
                dealer.getName(), dealer.getSurname(), dealer.getEmail(), dealer.getPhonePrefix(), dealer.getPhone(), dealer.getMobilePrefix(), dealer.getMobile(),
                dealer.getStatus(), dealer.getCreationDate());
    }

}
