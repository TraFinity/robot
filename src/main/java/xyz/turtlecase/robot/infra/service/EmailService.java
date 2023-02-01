package xyz.turtlecase.robot.infra.service;

import java.io.File;
import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import xyz.turtlecase.robot.infra.config.ConfigProperties;
import xyz.turtlecase.robot.infra.exception.BaseException;

@Slf4j
@Service
public class EmailService {

    @Resource
    private JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String from;
    @Autowired
    private ConfigProperties configProperties;

    /**
     * 发送纯文本邮件
     *
     * @param to      目标email地址
     * @param subject 邮件主题
     * @param text    文本内容
     */
    public void sendMail(String to, String subject, String text) {
        // 如果未启用发邮件, 则直接返回
        if (!this.configProperties.isEnableEmailSend()) {
            return;
        }
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(this.from);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        this.javaMailSender.send(message);
    }

    /**
     * 发送纯文本邮件, 并支持附件
     *
     * @param to       目标email地址
     * @param subject  邮件主题
     * @param text     文本内容
     * @param filePath 附件路径
     * @throws MessagingException
     */
    public void sendMailWithAttachment(String to, String subject, String text, String filePath) throws MessagingException {
        // 如果未启用发邮件, 则直接返回
        if (!this.configProperties.isEnableEmailSend()) {
            return;
        }
        File attachment = new File(filePath);
        MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(this.from);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);
        helper.addAttachment(attachment.getName(), attachment);
        this.javaMailSender.send(mimeMessage);
    }

    /**
     * 发送富文本邮件
     *
     * @param to
     * @param subject
     * @param text
     */
    public void sendRichMail(String to, String subject, String text) {

        if (!this.configProperties.isEnableEmailSend()) {
            return;
        }
        MimeMessage mimeMessage = this.javaMailSender.createMimeMessage();
        try {

            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setFrom(this.from);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);

        } catch (MessagingException e) {
            log.error("sendRichMail error, to: {}, subject: {} body:\n {}", new Object[]{to, subject, text, e});
            throw new BaseException("Send email failure");
        }
        this.javaMailSender.send(mimeMessage);
    }
}
