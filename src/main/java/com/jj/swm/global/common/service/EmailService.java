package com.jj.swm.global.common.service;

import com.jj.swm.global.common.enums.ErrorCode;
import com.jj.swm.global.exception.GlobalException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private static final String title = "스터디 윗 미 이메일 인증코드";

    private final JavaMailSender mailSender;

    @Async("asyncExecutor")
    public CompletableFuture<Boolean> sendEmail(String toEmail, String text) {
        String htmlContent = createHTMLEmail(text);

        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject(title);
            helper.setText(htmlContent, true);

            mailSender.send(message);

            return CompletableFuture.completedFuture(true);
        } catch (MessagingException | MailException e) {
            log.error("MailService.sendEmail exception occur toEmail: {}, " +
                    "title: {}, text: {}", toEmail, title, text);

            return CompletableFuture.completedFuture(false);
        }
    }

    private String createHTMLEmail(String text) {
        return String.format(
                "<!DOCTYPE html>\n" +
                        "<html lang=\"ko\">\n" +
                        "  <head>\n" +
                        "    <meta charset=\"UTF-8\" />\n" +
                        "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n" +
                        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
                        "    <title>이메일 인증코드 안내</title>\n" +
                        "  </head>\n" +
                        "  <body style=\"margin-top: 20px; padding: 0\">\n" +
                        "    <div\n" +
                        "      style=\"\n" +
                        "        max-width: 600px;\n" +
                        "        margin: 0 auto;\n" +
                        "        padding: 20px;\n" +
                        "        background-color: #ffffff;\n" +
                        "        border-radius: 8px;\n" +
                        "        box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);\n" +
                        "      \"\n" +
                        "    >\n" +
                        "      <h2 style=\"font-size: 24px; color: #000; margin-bottom: 16px\">\n" +
                        "        이메일 인증코드 안내\n" +
                        "      </h2>\n" +
                        "      <p style=\"font-size: 16px; color: #000; margin-bottom: 12px\">\n" +
                        "        안녕하세요, 스터디 윗 미입니다.\n" +
                        "      </p>\n" +
                        "      <p style=\"font-size: 16px; color: #000; margin-bottom: 20px\">\n" +
                        "        고객님의 요청으로 이메일 인증코드 안내 메일을 발송했습니다.\n" +
                        "      </p>\n" +
                        "      <p style=\"font-size: 16px; color: #000; margin-bottom: 24px\">\n" +
                        "        아래 인증 코드를 통해 인증을 진행해 주세요.\n" +
                        "      </p>\n" +
                        "\n" +
                        "      <div\n" +
                        "        style=\"\n" +
                        "          padding: 16px;\n" +
                        "          background-color: #e5f9ed;\n" +
                        "          border-radius: 8px;\n" +
                        "          text-align: center;\n" +
                        "          margin-bottom: 24px;\n" +
                        "        \"\n" +
                        "      >\n" +
                        "        <p style=\"font-size: 24px; font-weight: bold; color: #000\">\n" +
                        "          인증코드\n" +
                        "        </p>\n" +
                        "        <strong style=\"font-size: 28px; font-weight: bold; color: #018786\">\n" +
                        "          %s\n" +  // text 변수 삽입
                        "        </strong>\n" +
                        "      </div>\n" +
                        "\n" +
                        "      <p style=\"font-size: 14px; color: #000; margin-bottom: 24px\">\n" +
                        "        스터디 윗 미를 이용해 주셔서 감사합니다.<br />스터디 윗 미 팀 드림.\n" +
                        "      </p>\n" +
                        "    </div>\n" +
                        "  </body>\n" +
                        "</html>",
                text // 여기서 인증 코드 삽입
        );
    }
}
