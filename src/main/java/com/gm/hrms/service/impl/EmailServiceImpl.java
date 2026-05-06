package com.gm.hrms.service.impl;

import com.gm.hrms.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.name:GM HRMS}")
    private String appName;

    // ════════════════════ SEND CREDENTIALS ═══════════════════════════════════

    @Async
    @Override
    public void sendCredentials(String to, String name, String password) {

        String subject = appName + " — Your Login Credentials";

        String html = """
                <div style="font-family:Arial,sans-serif;max-width:520px;margin:auto;padding:32px;
                            border:1px solid #e5e7eb;border-radius:12px;background:#fff;">
                  <h2 style="color:#C35E33;margin-top:0;">Welcome to %s, %s!</h2>
                  <p style="color:#374151;">Your account has been created by HR.
                     Use the credentials below to log in.</p>
                  <div style="background:#FDF5F1;border-radius:8px;padding:20px;margin:20px 0;">
                    <p style="margin:4px 0;color:#374151;"><strong>Username / Email:</strong> %s</p>
                    <p style="margin:4px 0;color:#374151;"><strong>Temporary Password:</strong> %s</p>
                  </div>
                  <p style="color:#6b7280;font-size:13px;">
                    Please log in and change your password immediately.</p>
                  <p style="color:#6b7280;font-size:12px;margin-top:32px;">
                    — HR Team, %s</p>
                </div>
                """.formatted(appName, name, to, password, appName);

        sendHtml(to, subject, html);
    }

    // ════════════════════════ SEND OTP ═══════════════════════════════════════

    @Async
    @Override
    public void sendOtp(String to, String name, String otp) {

        String subject = appName + " — Password Reset OTP";

        String html = """
                <div style="font-family:Arial,sans-serif;max-width:520px;margin:auto;padding:32px;
                            border:1px solid #e5e7eb;border-radius:12px;background:#fff;">
                  <h2 style="color:#C35E33;margin-top:0;">Password Reset Request</h2>
                  <p style="color:#374151;">Hi <strong>%s</strong>,</p>
                  <p style="color:#374151;">
                    We received a request to reset the password for your %s account.
                    Use the OTP below — it is valid for <strong>5 minutes</strong>.</p>
 
                  <div style="text-align:center;margin:32px 0;">
                    <div style="display:inline-block;background:#FDF5F1;border:2px dashed #C35E33;
                                border-radius:12px;padding:20px 40px;">
                      <span style="font-size:36px;font-weight:800;letter-spacing:10px;color:#C35E33;">
                        %s
                      </span>
                    </div>
                  </div>
 
                  <p style="color:#6b7280;font-size:13px;">
                    If you didn't request this, you can safely ignore this email.
                    Your password will not be changed.</p>
                  <p style="color:#6b7280;font-size:12px;margin-top:32px;">
                    — Security Team, %s</p>
                </div>
                """.formatted(name, appName, otp, appName);

        sendHtml(to, subject, html);
    }

    // ══════════════════════ INTERNAL HELPER ══════════════════════════════════

    private void sendHtml(String to, String subject, String html) {
        try {
            MimeMessage message  = mailSender.createMimeMessage();
            MimeMessageHelper h  = new MimeMessageHelper(message, true, "UTF-8");
            h.setFrom(fromEmail);
            h.setTo(to);
            h.setSubject(subject);
            h.setText(html, true);
            mailSender.send(message);
            log.info("Email sent → {} [{}]", to, subject);
        } catch (MessagingException ex) {
            log.error("Failed to send email to {}: {}", to, ex.getMessage());
            // Don't propagate — email failure should not break the API response
        }
    }
}