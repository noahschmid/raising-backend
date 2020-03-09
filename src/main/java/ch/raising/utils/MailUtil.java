package ch.raising.utils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class MailUtil {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    /**
     * Send password forgot email using html template
     * @param recipient the recipient email
     * @param code the code to send
     * @throws MessagingException
     */
    public void sendPasswordForgotEmail(final String recipient, final String code) throws MessagingException {
        final Context ctx = new Context(LocaleContextHolder.getLocale());
        ctx.setVariable("code", code);

        final MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
        final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "UTF-8");
        message.setSubject("Your Password reset code");
        message.setFrom("thymeleaf@example.com");
        message.setTo(recipient);

        final String htmlContent = this.templateEngine.process("forgotPassword", ctx);
        message.setText(htmlContent, true);

        this.javaMailSender.send(mimeMessage);
    }
}