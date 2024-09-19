package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.FileCoupon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FileCouponDTO {

    private Long id;
    private String name;
    private String type;
    private String note;
    private String path;
    private LocalDateTime creationDate;
    private Long advertiserId;
    private String advertiserName;
    private Long couponId;
    private String couponName;

    public static FileCouponDTO from(FileCoupon file) {
        return new FileCouponDTO(
                file.getId(),
                file.getName(),
                file.getType(),
                file.getNote(),
                file.getPath(),
                file.getCreationDate(),
                file.getAdvertiser() != null ? file.getAdvertiser().getId() : null,
                file.getAdvertiser() != null ? file.getAdvertiser().getName() : null,
                file.getCoupon() != null ? file.getCoupon().getId() : null,
                file.getCoupon() != null ? file.getCoupon().getName() : null
        );
    }

}