package it.cleverad.engine.business;

import it.cleverad.engine.config.model.Refferal;
import it.cleverad.engine.service.ReferralService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Transactional
public class ReferralBusiness {

    @Autowired
    private ReferralService referralService;

    /**
     * ============================================================================================================
     **/

    // UPDATE
    public Refferal decode(Filter filter) {
        return referralService.decodificaReferral(filter.getReferral());
    }

    /**
     * ============================================================================================================
     **/

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Filter {
        private String referral;
    }

}