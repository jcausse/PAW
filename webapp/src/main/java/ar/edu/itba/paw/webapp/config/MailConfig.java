package ar.edu.itba.paw.webapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.EnableAsync;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Configuration
@EnableAsync
public class MailConfig {

    private final Environment env;

    @Autowired
    public MailConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public JavaMailSender javaMailSender() {
        var mailSender = new JavaMailSenderImpl();
        mailSender.setHost(Objects.requireNonNull(env.getRequiredProperty("mail.host")));
        mailSender.setPort(Integer.parseInt(Objects.requireNonNull(env.getRequiredProperty("mail.port"))));
        mailSender.setUsername(Objects.requireNonNull(env.getRequiredProperty("mail.username")));
        mailSender.setPassword(Objects.requireNonNull(env.getRequiredProperty("mail.password")));

        var props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        return mailSender;
    }

    @Bean(name = "emailTemplateEngine")
    public SpringTemplateEngine emailTemplateEngine() {
        var templateEngine = new SpringTemplateEngine();
        templateEngine.addTemplateResolver(emailTemplateResolver());
        return templateEngine;
    }

    private ClassLoaderTemplateResolver emailTemplateResolver() {
        var resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("mail/");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        resolver.setCacheable(false);
        return resolver;
    }
}
