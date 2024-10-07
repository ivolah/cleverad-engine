package it.cleverad.engine.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import it.cleverad.engine.business.*;
import it.cleverad.engine.config.security.JwtUserDetailsService;
import it.cleverad.engine.web.dto.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@Configurable
public class MailService {

    private static String MAIL_INFO = "info@cleverad.it";
    @Autowired
    MailTempalteBusiness mailTempalteBusiness;
    @Autowired
    AffiliateBusiness affiliateBusiness;
    @Autowired
    CampaignBusiness campaignBusiness;
    @Autowired
    UserBusiness userBusiness;
    @Autowired
    ChannelBusiness channelBusiness;
    @Autowired
    PlannerBusiness plannerBusiness;
    @Autowired
    PayoutBusiness payoutBusiness;
    @Autowired
    AdvertiserBusiness advertiserBusiness;
    @Autowired
    private JwtUserDetailsService jwtUserDetailsService;
    @Autowired
    private JavaMailSender emailSender;
    @Autowired
    private CampaignAffiliateRequestBusiness campaignAffiliateRequestBusiness;

    /**
     * ============================================================================================================
     **/

    // Invia Mail Custom totale
    public MailDTO inviaCustom(BaseCreateRequest request) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(MAIL_INFO);
        message.setTo(request.email);
        message.setSubject(request.oggetto);
        message.setText(request.testo);

        emailSender.send(message);

        return null;
    }

    // Invia Mail Singola
    public MailDTO inviaSingola(BaseCreateRequest request) {

        AffiliateDTO affiliato = affiliateBusiness.findById(request.getAffiliateId());

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(MAIL_INFO);
        message.setTo(affiliato.getPrimaryMail());
        message.setSubject("TEST MAIL CLEVERAD");
        message.setText("CONTENUTO DI TESTTTT");

        emailSender.send(message);

        return null;
    }


    /**
     * ============================================================================================================
     **/


    @SneakyThrows
    public MailDTO invio(BaseCreateRequest request) {
        log.info("INVIO {} - {}", request.getTemplateId(), request);
        MailTemplateDTO mailTemplate = mailTempalteBusiness.findById(request.templateId);

        AffiliateDTO affiliate = request.affiliateId != null ? affiliateBusiness.findById(request.affiliateId) : null;
        CampaignDTO campaign = request.campaignId != null ? campaignBusiness.findById(request.campaignId) : null;
        UserDTO user = request.userId != null ? userBusiness.findById(request.userId) : null;
        ChannelDTO channelDTO = request.channelId != null ? channelBusiness.findById(request.channelId) : null;
        PlannerDTO plannerDTO = request.plannerId != null ? plannerBusiness.findById(request.plannerId) : null;
        PayoutDTO payoutDTO = request.payoutId != null ? payoutBusiness.findById(request.payoutId) : null;
        AdvertiserDTO advertiserDTO = request.advertiserId != null ?  advertiserBusiness.findById(request.advertiserId) : null;

        if (affiliate != null && StringUtils.isBlank(affiliate.getIban()))
            affiliate.setIban(">>> INSERISCI IL TUO IBAN <<<");
        if (affiliate != null && StringUtils.isBlank(affiliate.getSwift()))
            affiliate.setSwift(">>> INSERISCI IL TUO SWIFT <<<");

        Template t = new Template(mailTemplate.getName(), new StringReader(mailTemplate.getContent()), new Configuration());
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        if (request.campaignId != null) model.put("plannerName", campaign.getPlannerName());
        if (request.status != null) model.put("status", request.status);
        model.put("affiliate", affiliate);
        model.put("advertiser", advertiserDTO);
        model.put("campaign", campaign);
        model.put("channel", channelDTO);
        model.put("planner", plannerDTO);
        model.put("user", user);
        model.put("payout", payoutDTO);

        t.process(model, stringWriter);

        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setFrom(MAIL_INFO);
        if (request.getEmail() != null) helper.setTo(request.getEmail());
        else helper.setTo(affiliate.getPrimaryMail());
        helper.setSubject(mailTemplate.getSubject());
        String emailContent = stringWriter.toString();
        helper.setText(emailContent, true);
        emailSender.send(mimeMessage);

        return null;
    }

    public MailDTO inviaMailReset(BaseCreateRequest request) {
        request.setTemplateId(3L);
        this.invio(request);
        return null;
    }

    public MailDTO invioTemplate(BaseCreateRequest request) {
        return this.invio(request);
    }

    public MailDTO invitoCampagna(BaseCreateRequest request) {
        request.setTemplateId(11L);
        request.setPlannerId(campaignBusiness.findById(request.campaignId).getPlannerId());
        this.invio(request);
        return null;
    }

    public MailDTO inviaMailRegistrazione(BaseCreateRequest request) {
        request.setTemplateId(6L);
        this.invio(request);
        return null;
    }

    public MailDTO confermaCanale(BaseCreateRequest request) {
        request.setTemplateId(9L);
        this.invio(request);
        return null;
    }

    public MailDTO rifiutoCanale(BaseCreateRequest request) {
        request.setTemplateId(10L);
        this.invio(request);
        return null;
    }

    public MailDTO confermaAffiliato(BaseCreateRequest request) {
        request.setTemplateId(7L);
        this.invio(request);
        return null;
    }

    public MailDTO rifiutoAffiliato(BaseCreateRequest request) {
        request.setTemplateId(8L);
        this.invio(request);
        return null;
    }

    public MailDTO inviaMailPayout(BaseCreateRequest request) {
        request.setTemplateId(4L);
        this.invio(request);
        return null;
    }

    public MailDTO inviaMailFatturaCaricata(BaseCreateRequest request) {
        request.setTemplateId(15L);
        this.invio(request);
        return null;
    }

    public void invioRichiesta(BaseCreateRequest request) {
        AffiliateDTO affiliato = affiliateBusiness.findById(jwtUserDetailsService.getAffiliateId());
        CampaignDTO campaign = campaignBusiness.findById(request.getCampaignId());
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(MAIL_INFO);
        log.info("MAIL {}", campaign.getPlannerMail());
        message.setTo(campaign.getPlannerMail(), MAIL_INFO);
        message.setSubject("Richiesta partecipazione campagna " + campaign.getName());
        message.setText("L'affiliato " + affiliato.getName() + " ha richiesto di partecipare alla campagmna " + campaign.getName() + ".");
        emailSender.send(message);

        CampaignAffiliateRequestBusiness.BaseCreateRequest reqCrea = new CampaignAffiliateRequestBusiness.BaseCreateRequest();
        reqCrea.setStatusId(64L);
        reqCrea.setCampaignId(campaign.getId());
        reqCrea.setAffiliateId(affiliato.getId());
        campaignAffiliateRequestBusiness.create(reqCrea);
    }

    /**
     * ============================================================================================================
     **/

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BaseCreateRequest {
        private String status;
        private Long plannerId;
        private Long templateId;
        private Long campaignId;
        private Long affiliateId;
        private Long advertiserId;
        private Long channelId;
        private Long userId;
        private Long payoutId;
        private String email;
        private String oggetto;
        private String testo;
        private String note;
        private ChannelDTO channelDTO;
    }

}