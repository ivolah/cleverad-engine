package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.Coupon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponDTO {

    private Long id;
    private String name;
    private String description;
    private String code;
    private String urlPromo;
    private Integer initial;
    private Integer actual;
    private Boolean status;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long advertiserId;
    private String advertiserName;
    private List<FileCouponDTO> files;
    private Long fileId;

    public static CouponDTO from(Coupon coupon) {
        return new CouponDTO(
                coupon.getId(), coupon.getName(),
                coupon.getDescription(), coupon.getCode(), coupon.getUrlPromo(),
                coupon.getInitial(), coupon.getActual(),
                coupon.getStatus(),
                coupon.getStartDate(), coupon.getEndDate(),
                coupon.getAdvertiser() != null ? coupon.getAdvertiser().getId() : null,
                coupon.getAdvertiser() != null ? coupon.getAdvertiser().getName() : null,
                coupon.getFiles() != null ? coupon.getFiles().stream().map(FileCouponDTO::from).collect(Collectors.toList()) : null,
                coupon.getFiles() != null && !coupon.getFiles().isEmpty() ? coupon.getFiles().stream().findFirst().get().getId() : null
        );
    }

}