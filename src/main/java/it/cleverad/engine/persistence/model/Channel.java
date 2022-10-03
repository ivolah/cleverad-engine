package it.cleverad.engine.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "t_channel")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class Channel {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String shortDescription;
    private String type;
    private String approvazione;
    private String url;

    private Boolean status;
    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    @OneToMany(mappedBy = "channel")
    private Set<ChannelCategory> channelCategories;

    @OneToMany(mappedBy = "channel")
    private Set<Transaction> transactions;

}
