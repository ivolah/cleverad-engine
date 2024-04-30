package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.Coupon;
import it.cleverad.engine.persistence.model.service.Feed;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedDTO {

    private Long id;
    private String name;
    private String description;
    private String url;
    private Boolean status;
    private LocalDate startDate;
    private LocalDate endDate;

    public static FeedDTO from(Feed coupon) {
        return new FeedDTO(
                coupon.getId(), coupon.getName(),
                coupon.getDescription(), coupon.getUrl(),
                coupon.getStatus(),
                coupon.getStartDate(), coupon.getEndDate());
    }

}