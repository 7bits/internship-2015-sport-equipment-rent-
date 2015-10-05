package it.sevenbits.web.controllers;

import de.neuland.jade4j.JadeConfiguration;
import de.neuland.jade4j.template.JadeTemplate;
import it.sevenbits.domain.Deal;
import it.sevenbits.domain.Goods;
import it.sevenbits.domain.User;
import it.sevenbits.service.exceptions.GoodsException;
import it.sevenbits.service.GoodsService;
import it.sevenbits.service.UserService;
import it.sevenbits.service.exceptions.UserServiceException;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by awemath on 7/17/15.
 */
@RestController
public class MailSubmissionController {


    private final JavaMailSender javaMailSender;

    private Logger logger = Logger.getLogger(MailSubmissionController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private GoodsService goodsService;


    @Autowired
    private JadeConfiguration jade;

    @Autowired
    MailSubmissionController(final JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @ResponseStatus(HttpStatus.CREATED)
    public void sendEmail(final String html,
                          final String title,
                          final String to) {
        JavaMailSenderImpl sender = (JavaMailSenderImpl) javaMailSender;

        try {
            MimeMessage mimeMessage = sender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");
            mimeMessage.setContent(html, "text/html; charset=\"UTF-8\"");

            helper.setTo(to);
            helper.setSubject(title);
            helper.setFrom(sender.getUsername());

            sender.send(mimeMessage);
        } catch (MailException e) {
            logger.error("Email didn`t send", e);
        } catch (MessagingException e) {
            logger.error("Email didn`t send", e);
        }
    }

    //letter_deny
    @ResponseStatus(HttpStatus.CREATED)
    public void sendDeny(final Deal deal) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        try {
            User landlord = userService.getUser(deal.getLandlordId());
            User renting = userService.getUser(deal.getRentingId());
            Goods goods = goodsService.getGoods(deal.getGoodsId());
            JadeTemplate template = jade.getTemplate("home/letter/letter_deny");
            HashMap<String, Object> model = new HashMap<String, Object>();
            String title = "";
            model.put("confirmLink", "sport-equipment-rent.7bits.it");
            model.put("landlord", landlord);
            model.put("renting", renting);
            model.put("goods", goods);

            String html = jade.renderTemplate(template, model);

            sendEmail(html, title, renting.getEmail());

        } catch (GoodsException e) {
            logger.error("Email didn`t send", e);
        } catch (IOException e) {
            logger.error("Email didn`t send", e);
        } catch (UserServiceException e) {
            logger.error("Email didn`t send", e);
        }
    }

    //letter_confirm_start_rent
    @ResponseStatus(HttpStatus.CREATED)
    public void sendConfirmationMail(final Deal deal) {
        try {
            User landlord = userService.getUser(deal.getLandlordId());
            User renting = userService.getUser(deal.getRentingId());
            Goods goods = goodsService.getGoods(deal.getGoodsId());


            JadeTemplate template = jade.getTemplate("home/letter/letter_confirm_start_rent");
            HashMap<String, Object> model = new HashMap<String, Object>();
            String title = "";
            model.put("denyLink", "sport-equipment-rent.7bits.it");
            model.put("confirmLink", "sport-equipment-rent.7bits.it");
            model.put("landlord", landlord);
            model.put("renting", renting);
            model.put("goods", goods);
            String html = jade.renderTemplate(template, model);
            sendEmail(html, title, renting.getEmail());
        } catch (GoodsException e) {
            logger.error("Email didn`t send", e);
        } catch (IOException e) {
            logger.error("Email didn`t send", e);
        } catch (UserServiceException e) {
            logger.error("Email didn`t send", e);
        }
    }

    //letter_end_of_rent
    @ResponseStatus(HttpStatus.CREATED)
    public void sendClose(final Deal deal) {
        try {
            User landlord = userService.getUser(deal.getLandlordId());
            User renting = userService.getUser(deal.getRentingId());
            Goods goods = goodsService.getGoods(deal.getGoodsId());
            JadeTemplate template = jade.getTemplate("home/letter/letter_confirm_start_rent");
            HashMap<String, Object> model = new HashMap<String, Object>();
            String title = "";
            model.put("confirmLink", "sport-equipment-rent.7bits.it");
            model.put("landlord", landlord);
            model.put("renting", renting);
            model.put("goods", goods);
            String html = jade.renderTemplate(template, model);
            sendEmail(html, title, landlord.getEmail());
        } catch (GoodsException e) {
            logger.error("Email didn`t send", e);
        } catch (UserServiceException e) {
            logger.error("Email didn`t send", e);
        } catch (IOException e) {
            logger.error("Email didn`t send", e);
        }
    }

    //letter
    @ResponseStatus(HttpStatus.CREATED)
    public void sendHtmlEmail(final Deal deal) {
        try {
            User landlord = userService.getUser(deal.getLandlordId());
            User renting = userService.getUser(deal.getRentingId());
            Goods goods = goodsService.getGoods(deal.getGoodsId());
            JadeTemplate template = jade.getTemplate("home/letter/letter");
            HashMap<String, Object> model = new HashMap<String, Object>();
            String title = "";
            model.put("denyLink", "sport-equipment-rent.7bits.it");
            model.put("confirmLink", "sport-equipment-rent.7bits.it");
            model.put("renting", renting);
            model.put("goods", goods);
            String html = jade.renderTemplate(template, model);
            sendEmail(html, title, landlord.getEmail());

        } catch (GoodsException e) {
            logger.error("Email didn`t send", e);
        } catch (IOException e) {
            logger.error("Email didn`t send", e);
        } catch (UserServiceException e) {
            logger.error("Email didn`t send", e);
        }
    }
}
