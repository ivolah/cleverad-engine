package it.cleverad.engine.persistence.model.service;

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
    private String dimension;
    private String country;
    @Column(name = "short_description")
    private String shortDescription;
    private String url;
    private Boolean status = true;
    @Column(name = "creation_date")
    private LocalDateTime creationDate = LocalDateTime.now();
    @Column(name = "last_modification_date")
    private LocalDateTime lastModificationDate = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "dictionary_id")
    private Dictionary dictionary;

    @ManyToOne
    @JoinColumn(name = "business_type_id")
    private Dictionary dictionaryBusinessType;

    @ManyToOne
    @JoinColumn(name = "affiliate_id")
    private Affiliate affiliate;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private Dictionary dictionaryType;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private Dictionary dictionaryOwner;


//    @ManyToMany
//    @JoinTable(
//            name = "t_channel_category",
//            joinColumns = @JoinColumn(name = "channel_id"),
//            inverseJoinColumns = @JoinColumn(name = "category_id"))
//    private Collection<Category> categories;

    @OneToMany(mappedBy = "channel")
    private Set<ChannelCategory> channelCategories;

    @OneToMany(mappedBy = "channel")
    private Set<TransactionCPC> transactionCPCS;

    @OneToMany(mappedBy = "channel")
    private Set<TransactionCPM> transactionCPMS;

    @OneToMany(mappedBy = "channel")
    private Set<TransactionCPL> transactionCPLS;
}
