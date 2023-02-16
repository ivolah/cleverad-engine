package it.cleverad.engine.service;

import freemarker.template.Configuration;
import freemarker.template.Template;
import it.cleverad.engine.business.AffiliateBusiness;
import it.cleverad.engine.business.CampaignBusiness;
import it.cleverad.engine.business.MailTempalteBusiness;
import it.cleverad.engine.web.dto.AffiliateDTO;
import it.cleverad.engine.web.dto.CampaignDTO;
import it.cleverad.engine.web.dto.MailDTO;
import it.cleverad.engine.web.dto.MailTemplateDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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

    @Autowired
    MailTempalteBusiness mailTempalteBusiness;
    @Autowired
    AffiliateBusiness affiliateBusiness;
    @Autowired
    CampaignBusiness campaignBusiness;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private Configuration configuration;

    /**
     * ============================================================================================================
     **/

    // Invia Mail Custom totale
    public MailDTO inviaCustom(BaseCreateRequest request) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom("info@cleverad.it");
        message.setTo(request.email);
        message.setSubject(request.oggetto);
        message.setText(request.testo);

        emailSender.send(message);

        return null;
    }

    // Invia Mail Singola
    public MailDTO inviaSingola(BaseCreateRequest request) {

        AffiliateDTO affiliato = affiliateBusiness.findById(request.getAffiliateId());
        CampaignDTO campaign = campaignBusiness.findById(request.getCampaignId());

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("info@cleverad.it");
        message.setTo(affiliato.getPrimaryMail());
        message.setSubject("TEST MAIL CLEVERAD");
        message.setText("CONTENUTO DI TESTTTT");

        emailSender.send(message);

        return null;
    }

    @SneakyThrows
    public MailDTO inviaMailRegistrazione(BaseCreateRequest request) {
        AffiliateDTO affiliate = affiliateBusiness.findById(request.getAffiliateId());

        MailTemplateDTO mailTemplate = mailTempalteBusiness.findById(request.getTemplateId());
        String templateStr = mailTemplate.getContent();

        Template t = new Template(mailTemplate.getName(), new StringReader(templateStr), new Configuration());
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        model.put("affiliate", affiliate);
        t.process(model, stringWriter);

        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setFrom("info@cleverad.it");
        helper.setTo(affiliate.getPrimaryMail());
        helper.setSubject(mailTemplate.getSubject());
        String emailContent = stringWriter.toString();
        helper.setText(emailContent, true);
        emailSender.send(mimeMessage);

        return null;
    }

    @SneakyThrows
    public MailDTO conferma(BaseCreateRequest request, String canale) {

        AffiliateDTO affiliate = affiliateBusiness.findById(request.getAffiliateId());

        MailTemplateDTO mailTemplate = null;
        if (canale.equals("CANALE")) {
            mailTemplate = mailTempalteBusiness.findById(4L);
        } else {
            mailTemplate = mailTempalteBusiness.findById(3L);
        }

        String templateStr = mailTemplate.getContent();

        Template t = new Template(mailTemplate.getName(), new StringReader(templateStr), new Configuration());
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        model.put("affiliate", affiliate);
        t.process(model, stringWriter);

        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setFrom("info@cleverad.it");
        helper.setTo(affiliate.getPrimaryMail());
        helper.setSubject(mailTemplate.getSubject());
        String emailContent = stringWriter.toString();
        helper.setText(emailContent, true);
        emailSender.send(mimeMessage);

        return null;
    }

    @SneakyThrows
    public MailDTO invitoCampagna(BaseCreateRequest request) {
        AffiliateDTO affiliate = affiliateBusiness.findById(request.affiliateId);
        CampaignDTO campaign = campaignBusiness.findById(request.campaignId);
        MailTemplateDTO mailTemplate = mailTempalteBusiness.findById(5L);
        String templateStr = mailTemplate.getContent();

        Template t = new Template(mailTemplate.getName(), new StringReader(templateStr), new Configuration());
        StringWriter stringWriter = new StringWriter();
        Map<String, Object> model = new HashMap<>();
        model.put("affiliate", affiliate);
        model.put("campaign", campaign);
        model.put("plannerName", campaign.getPlannerName());
        t.process(model, stringWriter);

        MimeMessage mimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setFrom(campaign.getPlannerMail());
        helper.setTo(affiliate.getPrimaryMail());
        helper.setSubject(mailTemplate.getSubject() + campaign.getName());
        String emailContent = stringWriter.toString();
        helper.setText(emailContent, true);
        emailSender.send(mimeMessage);

        return null;
    }

    /**
     * ============================================================================================================
     **/

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BaseCreateRequest {
        private Long templateId;
        private Long campaignId;
        private Long affiliateId;
        private String email;
        private String oggetto;
        private String testo;
    }

}
