package ar.edu.itba.paw.service;

import ar.edu.itba.paw.model.Listing;
import ar.edu.itba.paw.model.User;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Service
public class MailingServiceImpl implements MailingService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine emailTemplateEngine;
    private final MessageSource messageSource;
    private final String mailFrom;
    private final String baseUrl;

    public MailingServiceImpl(
            JavaMailSender mailSender,
            @Qualifier("emailTemplateEngine") SpringTemplateEngine emailTemplateEngine,
            MessageSource messageSource,
            @Value("${mail.username}") String mailFrom,
            @Value("${app.baseUrl}") String baseUrl
    ) {
        this.mailSender = mailSender;
        this.emailTemplateEngine = emailTemplateEngine;
        this.messageSource = messageSource;
        this.mailFrom = mailFrom;
        this.baseUrl = baseUrl;
    }

    @Async
    @Override
    public void sendWelcomeEmail(User user, Locale locale) {
        var context = new Context(locale);
        context.setVariable("user", user);
        context.setVariable("baseUrl", baseUrl);
        context.setVariable("actionUrl", baseUrl + "/profile/" + user.getId());

        String subject = messageSource.getMessage("email.welcome.subject", null, locale);
        sendEmail(user.getEmail(), subject, "welcome", context);
    }

    @Async
    @Override
    public void sendListingPublishedEmail(User seller, Listing listing, Locale locale) {
        var context = new Context(locale);
        context.setVariable("user", seller);
        context.setVariable("listing", listing);
        context.setVariable("baseUrl", baseUrl);
        context.setVariable("actionUrl", baseUrl + "/listing/" + listing.getId());

        String subject = messageSource.getMessage("email.listing.subject", null, locale);
        sendEmail(seller.getEmail(), subject, "listing-published", context);
    }

    @Async
    @Override
    public void sendPurchaseSellerEmail(User seller, User buyer, Listing listing, String message, Locale locale) {
        var context = new Context(locale);
        context.setVariable("seller", seller);
        context.setVariable("buyer", buyer);
        context.setVariable("listing", listing);
        context.setVariable("message", message);
        context.setVariable("baseUrl", baseUrl);

        String subject = messageSource.getMessage("email.purchase.seller.subject", null, locale);
        sendEmail(seller.getEmail(), subject, "purchase-seller", context);
    }

    @Async
    @Override
    public void sendPurchaseBuyerEmail(User buyer, User seller, Listing listing, Locale locale) {
        var context = new Context(locale);
        context.setVariable("buyer", buyer);
        context.setVariable("seller", seller);
        context.setVariable("listing", listing);
        context.setVariable("baseUrl", baseUrl);

        String subject = messageSource.getMessage("email.purchase.buyer.subject", null, locale);
        sendEmail(buyer.getEmail(), subject, "purchase-buyer", context);
    }

    private void sendEmail(String to, String subject, String templateName, Context context) {
        try {
            var mimeMessage = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());
            helper.setFrom(mailFrom);
            helper.setTo(to);
            helper.setSubject(subject);

            var htmlContent = emailTemplateEngine.process(templateName, context);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
        } catch (Exception e) {
            // Error handling to be added later
        }
    }
}
