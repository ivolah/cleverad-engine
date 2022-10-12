package it.cleverad.engine.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_tracking")
@Getter
@Setter
@NoArgsConstructor
public class Tracking {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String refferalId;
    private String ip;
    private String agent;
    private LocalDateTime creationDate;
    private Boolean read;

}
