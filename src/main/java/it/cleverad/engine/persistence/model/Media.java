package it.cleverad.engine.persistence.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "t_media")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public class Media {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String url;
    private String target;
    private String bannerCode;
    private String note;
    private String idFile;
    private Boolean status;
    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    @OneToOne(mappedBy = "media")
    private MediaCampaign mediaCampaign;

    @OneToOne(mappedBy = "media")
    private MediaType mediaType;

    private Long typeId;

    @Override
    public String toString() {
        return "Media{" + "id=" + id + ", name='" + name + '\'' + ", typeId='" + typeId + '\'' + ", url='" + url + '\'' + ", target='" + target + '\'' + ", bannerCode='" + bannerCode + '\'' + ", note='" + note + '\'' + ", idFile='" + idFile + '\'' + ", status='" + status + '\'' + ", creationDate=" + creationDate + ", lastModificationDate=" + lastModificationDate + ", mediaCampaign=" + mediaCampaign + '}';
    }
}
