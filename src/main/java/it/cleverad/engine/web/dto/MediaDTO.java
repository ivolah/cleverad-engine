package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.Campaign;
import it.cleverad.engine.persistence.model.service.Media;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

    public MediaDTO(long id, String name, String url, String mailSubject, String bannerCode, String note, String idFile, Boolean status, LocalDateTime creationDate, LocalDateTime lastModificationDate, Long campaignId, String campaignName, Long typeId, String typeName,
                   // List<TargetDTO> targets,
                    String target) {
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
       // this.targets = targets;
        this.target = target;
    }

    public static MediaDTO from(Media media) {
        Campaign campaign = media.getCampaigns().stream().findFirst().orElse(null);

        List<TargetDTO> targets = null;
        if (media.getTargets() != null) {
            targets = media.getTargets().stream().map(tt -> {
                TargetDTO dto = new TargetDTO();
                dto.setId(tt.getId());
                dto.setTarget(tt.getTarget());
                return dto;
            }).collect(Collectors.toList());
        }

        return new MediaDTO(media.getId(), media.getName(), media.getUrl(), media.getMailSubject(), media.getBannerCode(), media.getNote(), media.getIdFile(), media.getStatus(), media.getCreationDate(), media.getLastModificationDate(),
                campaign != null ? campaign.getId() : null,
                campaign != null ? campaign.getName() : "NON ASSOCIATO A CAMPAGNA",
                media.getMediaType().getId(),
                media.getMediaType().getName(),
                //targets,
                media.getTarget());
    }

}


