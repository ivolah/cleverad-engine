package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.Campaign;
import it.cleverad.engine.persistence.model.service.Media;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Data
@NoArgsConstructor
public class MediaDTO {

    private long id;
    private String name;
    private String url;
    private String mailSubject;
    private String bannerCode;
    private String note;
    private String idFile;
    private String sender;
    private Boolean status;
    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    private Long campaignId;
    private String campaignName;

    private Long typeId;
    private String typeName;

    private String imageHash;

    private List<TargetDTO> targets;

    private String target;

    private Boolean visibile;

    private String description;
    private String title;
    private Long formatId;
    private String formatName;

    public MediaDTO(long id, String name, String url, String mailSubject, String bannerCode, String note, String idFile, Boolean status, LocalDateTime creationDate, LocalDateTime lastModificationDate, Long campaignId, String campaignName, Long typeId,
                    String typeName,
                    // List<TargetDTO> targets,
                    String sender,
                    String target,
                    Boolean visibile,
                    String description,
                    String title, Long formatId, String formatName) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.mailSubject = mailSubject;
        this.bannerCode = bannerCode;
        this.note = note;
        this.idFile = idFile;
        this.status = status;
        this.creationDate = creationDate;
        this.lastModificationDate = lastModificationDate;
        this.campaignId = campaignId;
        this.campaignName = campaignName;
        this.typeId = typeId;
        this.typeName = typeName;
        this.sender = sender;
        this.target = target;
        this.visibile = visibile;
        this.description = description;
        this.title = title;
        this.formatId = formatId;
        this.formatName = formatName;
    }

    public static MediaDTO from(Media media) {
        Campaign campaign = media.getCampaigns().stream().findFirst().orElse(null);

        return new MediaDTO(media.getId(), media.getName(), media.getUrl(), media.getMailSubject(),
                media.getBannerCode(), media.getNote(), media.getIdFile(), media.getStatus(),
                media.getCreationDate(), media.getLastModificationDate(),
                campaign != null ? campaign.getId() : null,
                campaign != null ? campaign.getName() : "NON ASSOCIATO A CAMPAGNA",
                media.getMediaType().getId(),
                media.getMediaType().getName(),
                media.getSender(),
                media.getTarget()
                , media.getVisibile()
                , media.getDescription(), media.getTitle(),
                media.getDictionary() != null ? media.getDictionary().getId() : null,
                media.getDictionary() != null ? media.getDictionary().getName() : null
        );
    }

}