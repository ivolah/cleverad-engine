package it.cleverad.engine.persistence.model.tracking;

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

    @Column(name = "refferal_id")
    private String refferalId;
    private String ip;
    private String agent;
    @Column(name = "creation_date")
    private LocalDateTime creationDate;
    private Boolean read;

}
