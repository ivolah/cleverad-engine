package it.cleverad.engine.service;

import it.cleverad.engine.config.model.Refferal;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@Slf4j
public class ReferralService {

    public Refferal decodificaReferral(String refferalString) {
        if (StringUtils.isNotBlank(refferalString) && !refferalString.contains("{{refferalId}}")) {
            Refferal refferal = new Refferal();

            refferal.setRefferal(refferalString);

            Boolean continua = true;

            String[] tokens = refferalString.split("-");
            if (tokens[0] != null) {
                try {
                    refferal.setCampaignId(Long.valueOf(decodifica(tokens[0])));
                } catch (Exception nf) {
                    log.warn("Error decoding campaign id : {}", tokens[0]);
                    continua = false;
                }
            }
            if (continua && tokens.length > 1 && tokens[1] != null) {
                try {
                    refferal.setMediaId(Long.valueOf(decodifica(tokens[1])));
                } catch (Exception nf) {
                    log.warn("Error decoding media id : {}", tokens[1]);
                    continua = false;
                }
            }
            if (continua && tokens.length > 2 && tokens[2] != null) {
                try {
                    refferal.setAffiliateId(Long.valueOf(decodifica(tokens[2])));
                } catch (Exception nf) {
                    log.warn("Error decoding affiliate id : {}", tokens[2]);
                    continua = false;
                }
            }
            if (continua && tokens.length > 3 && tokens[3] != null) {
                try {
                    Long cahanneID = Long.valueOf(decodifica(tokens[3]));
                    refferal.setChannelId(cahanneID);
                } catch (Exception nf) {
                    log.warn("Error decoding channel id : {}", tokens[3]);
                    continua = false;
                }
            }
            try {
                if (continua && tokens.length > 4 && tokens[4] != null) {
                    try {
                        refferal.setTargetId(Long.valueOf(decodifica(tokens[4])));
                    } catch (Exception nf) {
                        log.warn("Error decoding target id : {}", tokens[4]);
                        refferal.setTargetId(0L);
                    }
                }
            } catch (Exception cc) {
                log.warn("Eccezione token 4 :: {}", cc);
            }

            return refferal;
        }
        return null;
    }

    public String decodifica(String refferalString) {
        byte[] decoder = Base64.getDecoder().decode(refferalString);
        return new String(decoder);
    }

    public String creaEncoding(String campaignId, String mediaID, String affilaiteID, String channelID, String targetId) {

        campaignId = encode(campaignId);
        mediaID = encode(mediaID);
        affilaiteID = encode(affilaiteID);
        channelID = encode(channelID);
        targetId = encode(targetId);

        return StringUtils.stripEnd(campaignId, "=") + "-" + StringUtils.stripEnd(mediaID, "=") + "-" + StringUtils.stripEnd(affilaiteID, "=") + "-" + StringUtils.stripEnd(channelID, "=") + "-" + StringUtils.stripEnd(targetId, "=");
    }

    public String encode(String str) {
        byte[] encodedRefferal = Base64.getEncoder().encode(str.getBytes(StandardCharsets.UTF_8));
        String reString = new String(encodedRefferal);
        if (reString.endsWith("=")) {
            reString = reString.substring(0, reString.length() - 1);
        }
        if (reString.endsWith("=")) {
            reString = reString.substring(0, reString.length() - 1);
        }
        return reString;
    }

}