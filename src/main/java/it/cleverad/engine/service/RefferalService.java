package it.cleverad.engine.service;

import it.cleverad.engine.config.model.Refferal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@Slf4j
public class RefferalService {

    public Refferal decodificaRefferal(String refferalString) {
        String[] tokens = refferalString.split("-");
        log.trace("NOM TOKEN  REFF  {}", tokens.length);
        Refferal refferal = new Refferal();
        if (tokens[0] != null) {
            refferal.setCampaignId(Long.valueOf(decodifica(tokens[0])));
        }
        if (tokens[1] != null) {
            refferal.setMediaId(Long.valueOf(decodifica(tokens[1])));
        }
        if (tokens[2] != null) {
            refferal.setAffiliateId(Long.valueOf(decodifica(tokens[2])));
        }
        if (tokens[3] != null) {
            refferal.setChannelId(Long.valueOf(decodifica(tokens[3])));
        }
        return refferal;
    }

    public String decodifica(String refferalString) {
        byte[] decoder = Base64.getDecoder().decode(refferalString);
        return new String(decoder);
    }

    public String creaEncoding(String campaignId, String mediaID, String affilaiteID, String channelID) {

        campaignId = encode(campaignId);
        mediaID = encode(mediaID);
        affilaiteID = encode(affilaiteID);
        channelID = encode(channelID);

        return campaignId + "-" + mediaID + "-" + affilaiteID + "-" + channelID;
    }

    private String encode(String str) {
        byte[] encodedRefferal = Base64.getEncoder().encode(str.getBytes(StandardCharsets.UTF_8));
        String reString = new String(encodedRefferal);
        if (reString.endsWith("=")) {
            reString = reString.substring(0, reString.length() - 1);
        }
        return reString;
    }

}
