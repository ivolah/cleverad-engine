package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.Operation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class OperationDTO {

    private Long id;
    private LocalDateTime creationDate = LocalDateTime.now();
    private String method;
    private String url;
    private String username;
    private String data;

    public static OperationDTO from(Operation operation) {
        return new OperationDTO(
                operation.getId(),
                operation.getCreationDate(),
                operation.getMethod(),
                operation.getUrl(),
                operation.getUsername(),
                operation.getData()
               );
    }

}