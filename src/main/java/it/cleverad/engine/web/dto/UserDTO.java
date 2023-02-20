package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UserDTO {

    private long id;
    private String username;
    private String password;
    private String name;
    private String surname;
    private String email;
    private Long affiliateId;
    private Long roleId;
    private String role;
    private String affiliateName;
    private Boolean status;
    private LocalDateTime creationDate;
    private LocalDateTime lastLogin;

    public UserDTO(long id, String username, String password, String name, String surname, String email, Long affiliateId, String affiliateName, Long roleId, String role, Boolean status, LocalDateTime creationDate, LocalDateTime lastLogin) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.affiliateId = affiliateId;
        this.affiliateName = affiliateName;
        this.roleId = roleId;
        this.role = role;
        this.status = status;
        this.creationDate = creationDate;
        this.lastLogin = lastLogin;
    }

    public static UserDTO from(User user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getPassword(), user.getName(), user.getSurname(), user.getEmail(), user.getAffiliate() != null ? user.getAffiliate().getId() : null, user.getAffiliate() != null ? user.getAffiliate().getName() : null, user.getDictionary().getId(), user.getDictionary().getName(), user.getStatus(), user.getCreationDate() != null ? user.getCreationDate() : null, user.getLastLogin());
    }

}
