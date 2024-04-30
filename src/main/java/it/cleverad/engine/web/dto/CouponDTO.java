package it.cleverad.engine.web.dto;

import it.cleverad.engine.persistence.model.service.Coupon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CouponDTO {

    private Long id;
    private String name;
    private String description;
    private String code;
    private Integer initial;
    private Integer actual;
    private Boolean status;
    private LocalDate startDate;
    private LocalDate endDate;

    public static CouponDTO from(Coupon coupon) {
        return new CouponDTO(
                coupon.getId(), coupon.getName(),
                coupon.getDescription(), coupon.getCode(),
                coupon.getInitial(), coupon.getActual(),
                coupon.getStatus(),
                coupon.getStartDate(), coupon.getEndDate());
    }

}