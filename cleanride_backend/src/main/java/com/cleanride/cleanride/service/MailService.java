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

    // The email design lives in src/main/resources/mail/verification-email.html
    // (placeholders: {{name}}, {{code}}, {{ttl}}). Loaded once at startup, so a
    // missing file fails fast instead of at the first registration.
    private final String htmlTemplate = readResource("mail/verification-email.html");

    @Value("${spring.mail.username}")
    private String fromAddress;
    @Async
    public void sendVerificationCode(String to, String name, String code, int ttlMinutes) {
        String html = htmlTemplate
                .replace("{{name}}", HtmlUtils.htmlEscape(name))
                .replace("{{code}}", code)
                .replace("{{ttl}}", String.valueOf(ttlMinutes));

        String text = "Szia " + name + "!\n\n"
                + "Az email címed megerősítő kódja: " + code + "\n"
                + "A kód " + ttlMinutes + " percig érvényes.\n\n"
                + "Ha nem te regisztráltál, nyugodtan hagyd figyelmen kívül ezt a levelet.\n\n"
                + "Clean Ride Cosmetics · Mohács\n";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(new InternetAddress(fromAddress, "Clean Ride Cosmetics", "UTF-8"));
            helper.setTo(to);
            helper.setSubject("Clean Ride – email cím megerősítése");
            helper.setText(text, html);
            helper.addInline("logo", new ClassPathResource("mail/logo.png"));

            mailSender.send(message);
            log.info("Verification email handed to the SMTP server");
        } catch (MailAuthenticationException e) {
            log.error("Gmail rejected the login. Check GMAIL_USERNAME and that "
                    + "GMAIL_APP_PASSWORD is an App Password (not the normal password): {}",
                    e.getMessage());
        } catch (Exception e) {
            log.error("Could not send the verification email", e);
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
