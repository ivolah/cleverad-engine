package it.cleverad.engine.service;

import it.cleverad.engine.business.*;
import it.cleverad.engine.config.model.Refferal;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
@Slf4j
public class ReferralService {

    @Autowired
    private TargetBusiness targetBusiness;
    @Autowired
    private AffiliateBusiness affiliateBusiness;
    @Autowired
    private MediaBusiness mediaBusiness;
    @Autowired
    private CampaignBusiness campaignBusiness;
    @Autowired
    private ChannelBusiness channelBusiness;


    public Map<String, String> estrazioneInfo(String input) {
        Map<String, String> keyValueMap = new HashMap<>();
        if (StringUtils.isNotEmpty(input)) {
            Pattern pattern = Pattern.compile("(\\w+)\\s*:\\s*([^,]+)");
            Matcher matcher = pattern.matcher(input);
            while (matcher.find()) {
                String key = matcher.group(1).trim();
                String value = matcher.group(2).trim();
                keyValueMap.put(key.toLowerCase(), value);
                log.trace("key: " + key + " value: " + value);
            }
        }
        return keyValueMap;
    }

    public String replacePlaceholders(String input, Map<String, String> keyValueMap) {
        for (Map.Entry<String, String> entry : keyValueMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            input = input.replaceAll("\\{" + key + "\\}", value).replaceAll("\\{" + key.toUpperCase() + "\\}", value).replaceAll("\\[" + key + "\\]", value).replaceAll("\\[" + key.toUpperCase() + "\\]", value);
            log.trace("REPLACE PLACEHOLDERS >" + key + "< : >" + value + "< = " + input);
        }
        return input;
    }

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
            if (Boolean.TRUE.equals(continua) && tokens.length > 1 && tokens[1] != null) {
                try {
                    refferal.setMediaId(Long.valueOf(decodifica(tokens[1])));
                } catch (Exception nf) {
                    log.warn("Error decoding media id : {}", tokens[1]);
                    continua = false;
                }
            }
            if (Boolean.TRUE.equals(continua) && tokens.length > 2 && tokens[2] != null) {
                try {
                    refferal.setAffiliateId(Long.valueOf(decodifica(tokens[2])));
                } catch (Exception nf) {
                    log.warn("Error decoding affiliate id : {}", tokens[2]);
                    continua = false;
                }
            }
            if (Boolean.TRUE.equals(continua) && tokens.length > 3 && tokens[3] != null) {
                try {
                    Long cahanneID = Long.valueOf(decodifica(tokens[3]));
                    refferal.setChannelId(cahanneID);
                } catch (Exception nf) {
                    log.warn("Error decoding channel id : {}", tokens[3]);
                    continua = false;
                }
            }
            if (Boolean.TRUE.equals(continua) && tokens.length > 4 && tokens[4] != null) {
                try {
                    refferal.setTargetId(Long.valueOf(decodifica(tokens[4])));
                } catch (Exception nf) {
                    log.warn("Error decoding target id : {}", tokens[4]);
                    refferal.setTargetId(0L);
                }
            }
            return refferal;
        }
        return null;
    }

    public ReferralDTO descrivi(String refs) {

        Refferal refferal = decodificaReferral(refs);

        String affiliateName = "";
        if (refferal.getAffiliateId() != null) {
            affiliateName = affiliateBusiness.findById(refferal.getAffiliateId()).getName();
        }
        String mediaName = "";
        if (refferal.getMediaId() != null) {
            mediaName = mediaBusiness.findById(refferal.getMediaId()).getName();
        }
        String campaignName = "";
        if (refferal.getCampaignId() != null) {
            campaignName = campaignBusiness.findById(refferal.getCampaignId()).getName();
        }
        String channelName = "";
        if (refferal.getChannelId() != null) {
            channelName = channelBusiness.findById(refferal.getChannelId()).getName();
        }
        String targetName = "";
        if (refferal.getTargetId() != null && refferal.getTargetId() != 0L) {
            targetName = targetBusiness.findById(refferal.getTargetId()).getTarget();
        }

        ReferralDTO referralDTO = new ReferralDTO();
        referralDTO.setAffiliateId(refferal.getAffiliateId());
        referralDTO.setAffiliateName(affiliateName);
        referralDTO.setMediaId(refferal.getMediaId());
        referralDTO.setMediaName(mediaName);
        referralDTO.setCampaignId(refferal.getCampaignId());
        referralDTO.setCampaignName(campaignName);
        referralDTO.setChannelId(refferal.getChannelId());
        referralDTO.setChannelName(channelName);
        referralDTO.setRefferal(refs);
        referralDTO.setTargetId(refferal.getTargetId());
        referralDTO.setTargetName(targetName);
        if (refferal.getMediaId() != null)
            referralDTO.setDestinationUrl(mediaBusiness.findById(refferal.getMediaId()).getTarget());

        return referralDTO;
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

    private String decodifica(String refferalString) {
        byte[] decoder = Base64.getDecoder().decode(refferalString);
        return new String(decoder);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    public class ReferralDTO {
        private String refferal;
        private Long mediaId;
        private String mediaName;
        private Long campaignId;
        private String campaignName;
        private Long affiliateId;
        private String affiliateName;
        private Long channelId;
        private String channelName;
        private Long targetId;
        private String targetName;
        private String destinationUrl;
    }

}