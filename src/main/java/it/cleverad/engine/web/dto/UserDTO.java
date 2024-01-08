package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.User;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@ToString
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
    private List<FileUserDTO> fileUser;

    public UserDTO(long id, String username, String password, String name, String surname, String email, Long affiliateId, String affiliateName, Long roleId, String role, Boolean status, LocalDateTime creationDate, List<FileUserDTO> fileUsers) {
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
        this.fileUser = fileUsers;
    }

    public static UserDTO from(User user) {

        List<FileUserDTO> listaFile = null;
        if (user.getFileUsers() != null && !user.getFileUsers().isEmpty()) {
            listaFile = user.getFileUsers().stream().map(fileUser -> {
                FileUserDTO file = new FileUserDTO();
                file.setId(fileUser.getId());
                file.setName(fileUser.getName());
                file.setType(fileUser.getType());
                file.setAvatar(fileUser.getAvatar());
                return file;
            }).collect(Collectors.toList());
        }

        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getName(),
                user.getSurname(),
                user.getEmail(),
                user.getAffiliate() != null ? user.getAffiliate().getId() : null,
                user.getAffiliate() != null ? user.getAffiliate().getName() : null,
                user.getDictionary().getId(), user.getDictionary().getName(),
                user.getStatus(),
                user.getCreationDate() != null ? user.getCreationDate() : null,
                listaFile);
    }

}