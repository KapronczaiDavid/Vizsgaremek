package com.cleanride.cleanride.service;

import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender mailSender;
    private final String verificationTemplate = readResource("mail/verification-email.html");
    private final String passwordResetTemplate = readResource("mail/password-reset-email.html");

    @Value("${app.mail-from}")
    private String fromAddress;

    @Async
    public void sendVerificationCode(String to, String name, String code, int ttlMinutes) {
        String text = "Szia " + name + "!\n\n"
                + "Az email címed megerősítő kódja: " + code + "\n"
                + "A kód " + ttlMinutes + " percig érvényes.\n\n"
                + "Ha nem te regisztráltál, nyugodtan hagyd figyelmen kívül ezt a levelet.\n\n"
                + "Clean Ride Cosmetics · Mohács\n";
        sendCodeMail(to, name, code, ttlMinutes, verificationTemplate, text,
                "Clean Ride – email cím megerősítése");
    }

    @Async
    public void sendPasswordResetCode(String to, String name, String code, int ttlMinutes) {
        String text = "Szia " + name + "!\n\n"
                + "A jelszó-visszaállító kódod: " + code + "\n"
                + "A kód " + ttlMinutes + " percig érvényes.\n\n"
                + "Ha nem te kérted, hagyd figyelmen kívül ezt a levelet, a jelszavad nem változik.\n\n"
                + "Clean Ride Cosmetics · Mohács\n";
        sendCodeMail(to, name, code, ttlMinutes, passwordResetTemplate, text,
                "Clean Ride – jelszó visszaállítása");
    }

    private void sendCodeMail(String to, String name, String code, int ttlMinutes,
                              String template, String text, String subject) {
        String html = template
                .replace("{{name}}", HtmlUtils.htmlEscape(name))
                .replace("{{code}}", code)
                .replace("{{ttl}}", String.valueOf(ttlMinutes));

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(new InternetAddress(fromAddress, "Clean Ride Cosmetics", "UTF-8"));
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, html);
            helper.addInline("logo", new ClassPathResource("mail/logo.png"));

            mailSender.send(message);
            log.info("Mail '{}' handed to the SMTP server", subject);
        } catch (MailAuthenticationException e) {
            log.error("The mail server rejected the login. Check BREVO_SMTP_LOGIN and "
                    + "BREVO_SMTP_KEY: {}",
                    e.getMessage());
        } catch (Exception e) {
            log.error("Could not send the mail '{}'", subject, e);
        }
    }

    private static String readResource(String path) {
        try {
            return new String(new ClassPathResource(path).getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Missing email template: " + path, e);
        }
    }
}
