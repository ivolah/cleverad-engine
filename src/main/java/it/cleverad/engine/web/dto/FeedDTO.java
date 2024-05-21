package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.Feed;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedDTO {

    private Long id;
    private String name;
    private String description;
    private String urlPromo;
    private Boolean status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long advertiserId;
    private String advertiserName;
    private List<FileFeedDTO> fileIds;

    public static FeedDTO from(Feed coupon) {
        return new FeedDTO(
                coupon.getId(), coupon.getName(),
                coupon.getDescription(), coupon.getUrlPromo(),
                coupon.getStatus(),
                coupon.getStartDate(), coupon.getEndDate(),
                coupon.getAdvertiser() != null ? coupon.getAdvertiser().getId() : null,
                coupon.getAdvertiser() != null ? coupon.getAdvertiser().getName() : null,
                coupon.getFiles() != null ? coupon.getFiles().stream().map(FileFeedDTO::from).collect(Collectors.toList()) : null);
    }

}