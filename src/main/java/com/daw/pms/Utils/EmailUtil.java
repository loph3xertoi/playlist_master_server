package com.daw.pms.Utils;

import java.io.UnsupportedEncodingException;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Component
public class EmailUtil {
  private final JavaMailSender mailSender;

  @Value("${spring.mail.username}")
  private String tokenSenderEmail;

  public EmailUtil(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  public void sendEmail(String recipientEmail, String token)
      throws UnsupportedEncodingException, MessagingException {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message);

    helper.setFrom(tokenSenderEmail, "Reset password");
    helper.setTo(recipientEmail);

    String subject = "Token for resetting your password in playlist master.";

    String content =
        "<p>Hello,</p>"
            + "<p>You have requested to reset your password.</p>"
            + "<p>Please use the following verification code(10 minutes):</p>"
            + "<p>"
            + token
            + "</p>"
            + "<br>"
            + "<p>Ignore this email if you do remember your password, "
            + "or you have not made the request.</p>";

    helper.setSubject(subject);

    helper.setText(content, true);

    mailSender.send(message);
  }
}
