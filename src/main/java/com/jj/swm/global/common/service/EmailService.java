package com.jj.swm.global.common.service;

import com.jj.swm.domain.user.core.dto.event.BusinessInspectionUpdateEvent;
import com.jj.swm.global.common.enums.EmailSendType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private static final String AUTH_CODE_EMAIL_TITLE = "스터디 윗 미 인증코드";
    private static final String RETRIEVE_EMAIL_TITLE = "스터디 윗 미 가입 이메일 안내";
    private static final String BUSINESS_VERIFICATION_EMAIL_TITLE = "스터디 윗 미 사업자 검수 결과 안내";

    private static final String AUTH_CODE_EMAIL_TEMPLATE = "emails/auth-code-email";
    private static final String RETRIEVE_EMAIL_TEMPLATE = "emails/retrieve-email";
    private static final String BUSINESS_VERIFICATION_EMAIL_TEMPLATE = "emails/business-verification-email";

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Async("asyncExecutor")
    public CompletableFuture<Boolean> sendAuthCodeEmail(String toEmail, String code, EmailSendType type) {
        Context context = new Context();
        context.setVariable("code", code);
        context.setVariable("type", type.getType());

        String htmlContent = templateEngine.process(AUTH_CODE_EMAIL_TEMPLATE, context);

        return sendEmail(toEmail, AUTH_CODE_EMAIL_TITLE, htmlContent);
    }

    @Async("asyncExecutor")
    public void sendRetrieveEmail(String toEmail) {
        Context context = new Context();
        context.setVariable("email", toEmail);

        String htmlContent = templateEngine.process(RETRIEVE_EMAIL_TEMPLATE, context);

        sendEmail(toEmail, RETRIEVE_EMAIL_TITLE, htmlContent);
    }

    @Async("asyncExecutor")
    public CompletableFuture<Boolean> sendBusinessVerificationEmail(BusinessInspectionUpdateEvent event) {
        Context context = new Context();
        context.setVariable("userNickname", event.getUserNickname());
        context.setVariable("businessName", event.getBusinessName());
        context.setVariable("status", event.getStatus().name());

        String htmlContent = templateEngine.process(BUSINESS_VERIFICATION_EMAIL_TEMPLATE, context);

        return sendEmail(event.getUserEmail(), BUSINESS_VERIFICATION_EMAIL_TITLE, htmlContent);
    }

    private CompletableFuture<Boolean> sendEmail(String toEmail, String subject, String htmlContent) {
        MimeMessage message = mailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            return CompletableFuture.completedFuture(true);
        } catch (MessagingException | MailException e) {
            log.error("Email sending failed. To: {}, Subject: {}", toEmail, subject, e);
            return CompletableFuture.completedFuture(false);
        }
    }
}

