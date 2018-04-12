package jp.bo.bocc.service.impl;

import jp.bo.bocc.controller.api.request.TalkPurcCreateBody;
import jp.bo.bocc.entity.dto.ShtTalkPurcDTO;
import jp.bo.bocc.helper.MailUtils;
import jp.bo.bocc.helper.MessageUtils;
import jp.bo.bocc.service.MailService;
import jp.bo.bocc.service.UserService;
import jp.bo.bocc.service.UserSettingService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;

/**
 * Created by Namlong on 3/31/2017.
 */
@Service
public class MailServiceImpl implements MailService {

    private final static Logger LOGGER = Logger.getLogger(MailServiceImpl.class.getName());

    @Autowired
    JavaMailSender javaMailSender;
    @Value("${web.base.url}")
    private String webBaseUrl;
    @Value("${mail.admin}")
    private String mailAdmin;
    @Value("${template.mail.first.contact}")
    private String emailTemplateFirstContact;

    @Value("${template.mail.forgot.password}")
    private String emailTemplateForgotPassword;
    @Value("${template.mail.send.msg.normal}")
    private String emailTemplateSendMsgNormal;

    @Value("${template.mail.send.order.request}")
    private String emailTemplateSendOrderRequest;

    @Value("${template.mail.accept.order.request}")
    private String emailTemplateAcceptOrderRequest;

    @Value("${template.mail.sent.qa}")
    private String emailTemplateSentQa;

    @Value("${template.mail.response.qa}")
    private String emailTemplateResponseQa;

    @Value("${template.mail.completed.qa}")
    private String emailTemplateCompletedQa;
    @Value("${template.mail.review}")
    private String emailTemplateReview;
    @Value("${template.report}")
    private String emailTemplateReport;
    @Value("${template.patrol.suspend.user}")
    private String emailTemplateSuspendUser;

    @Value("${template.mail.restore.post}")
    private String emailTemplateRestorePost;
    @Value("${template.mail.restore.user}")
    private String emailTemplateRestoreUser;
    @Value("${template.mail.tend.to.leave}")
    private String emailTemplateTendToLeave;
    @Autowired
    private UserSettingService userSettingService;

    @Value("${template.patrol.suspend.post}")
    private String emailTemplateSuspendPost;

    @Value("${template_patrol_oke_post}")
    private String emailTemplateOkePost;

    @Value("${mail.name}")
    private String mailName;

    @Autowired
    private UserService userService;

    @Override
    public boolean sendEmail(String from, String to, String subject, String content) {
        LOGGER.info("BEGIN: sending email from: " + from + ". To: " + to);
        MimeMessagePreparator preparator = buildContent(new MessageUtils(from, to, subject, content));
        try {
            javaMailSender.send(preparator);
        } catch (MailException ex) {
            LOGGER.error("ERROR: Can not sent email. Error: " + ex.getMessage());
        }
        LOGGER.info("END: sent email from: " + from + ". To: " + to);
        return true;
    }

    /**
     * build message object.
     *
     * @param messageUtils
     * @return
     */
    private MimeMessagePreparator buildContent(MessageUtils messageUtils) {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {

            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "utf-8");

                helper.setSubject(messageUtils.getSubject());
                helper.setFrom(messageUtils.getFrom(), mailName);
                helper.setTo(messageUtils.getTo());

                String content = messageUtils.getContent();
                helper.setText(content, false);

            }
        };
        return preparator;
    }

    @Override
    @Async
    public void sendEmailForFirstContact(ShtTalkPurcDTO talkPurc, String fromNickName, String msgContent) {
        LOGGER.info("BEGIN: send email for talk purc.");
        try {
            String template = MailUtils.getTemplateMailFirstContact(emailTemplateFirstContact);
            final String postName = talkPurc.getPostName();
            template = template.replace("{postName}", postName);
            String title = MailUtils.getEmailTitle(template);
            String body = MailUtils.getEmailContent(template).replace("{partnerName}", fromNickName).replace("{nickName}", talkPurc.getOwnerPostNickName()).replace("{msgContent}", msgContent);
            String from = mailAdmin;
            String to = talkPurc.getEmailOwnerPost();
            sendEmail(from, to, title, body);
        } catch (Exception e) {
            LOGGER.error("ERROR: " + e.getMessage());
        }
        LOGGER.info("END: send email for talk purc. ");
    }

    @Override
    public void sendEmailForgotPassword(String email, String nickName, String password) {
        LOGGER.info("BEGIN: send email for user forgot password");
        try {
            String template = MailUtils.getTemplateMailFirstContact(emailTemplateForgotPassword);
            template = template.replace("{nickName}", nickName);
            String title = MailUtils.getEmailTitle(template);
            String body = MailUtils.getEmailContent(template).replace("{password}", password);
            String from = mailAdmin;
            String to = email;
            sendEmail(from, to, title, body);
        } catch (Exception e) {
            LOGGER.error("ERROR: " + e.getMessage());
        }
    }

    @Override
    public void sendEmailSendOrderRequest(String email, String postName, String partnerName, String ownerName, Long userId) {
        LOGGER.info("BEGIN: send email for send order request");
        sendTransactionEmail(emailTemplateSendOrderRequest, email, postName, ownerName, partnerName, userId);
    }

    @Override
    public void sendEmailAcceptOrderRequest(String email, String postName, String partnerName, String ownerName, Long userId) {
        LOGGER.info("BEGIN: send email for send order request");
        sendTransactionEmail(emailTemplateAcceptOrderRequest, email, postName, ownerName, partnerName, userId);
    }

    @Override
    public void sendEmailSentQaToAdmin(String email, String nickName, String qaType, String qaContent) {
        LOGGER.info("BEGIN: send email to user after sent qa to admin");
        try {
            String template = MailUtils.getTemplateMailFirstContact(emailTemplateSentQa);
            template = template.replace("{nickName}", nickName);
            String title = MailUtils.getEmailTitle(template);
            String body = MailUtils.getEmailContent(template).replace("{qaType}", qaType).replace("{qaContent}", qaContent);
            String from = mailAdmin;
            String to = email;
            sendEmail(from, to, title, body);
        } catch (Exception e) {
            LOGGER.error("ERROR: " + e.getMessage());
        }
    }

    @Override
    @Async
    public void sendEmailResponseQa(String email, String userName, String nickName, String msgContent, String qaType, String qaContent) {
        LOGGER.info("BEGIN: send email to user after response ");
        try {
            String template = MailUtils.getTemplateMailFirstContact(emailTemplateResponseQa);
            template = template.replace("{nickName}", nickName);
            String title = MailUtils.getEmailTitle(template).replace("{userName}", userName);
            String body = MailUtils.getEmailContent(template).replace("{qaType}", qaType).replace("{qaContent}", qaContent).replace("{msgContent}", msgContent);
            String from = mailAdmin;
            String to = email;
            sendEmail(from, to, title, body);
        } catch (Exception e) {
            LOGGER.error("ERROR: " + e.getMessage());
        }
    }

    @Override
    @Async
    public void sendEmailCompletedQa(String email, String userName, String nickName) {
        LOGGER.info("BEGIN: send email to user after response ");
        try {
            String template = MailUtils.getTemplateMailFirstContact(emailTemplateCompletedQa);
            template = template.replace("{nickName}", nickName);
            String title = MailUtils.getEmailTitle(template).replace("{userName}", userName);
            String body = MailUtils.getEmailContent(template);
            String from = mailAdmin;
            String to = email;
            sendEmail(from, to, title, body);
        } catch (Exception e) {
            LOGGER.error("ERROR: " + e.getMessage());
        }
    }

    private void sendTransactionEmail(String mailTemplate, String email, String postName, String ownerName, String partnerName, Long userId) {
        try {
            if (userSettingService.checkReceiveMailOn(userId)) {
                if (userSettingService.checkReceivingMailInTransaction(userId)) {
                    String template = MailUtils.getTemplateMailFirstContact(mailTemplate);
                    template = template.replace("{postName}", postName);
                    String title = MailUtils.getEmailTitle(template);
                    String body = MailUtils.getEmailContent(template).replace("{ownerName}", ownerName).replace("{partnerName}", partnerName).replace("{url}", webBaseUrl);
                    String from = mailAdmin;
                    String to = email;
                    sendEmail(from, to, title, body);
                }
            }
        } catch (Exception e) {
            LOGGER.error("ERROR: " + e.getMessage());
        }
        LOGGER.info("END: send email for talk purc. ");
    }

    @Override
    @Async
    public void sendEmailChatNormal(TalkPurcCreateBody talkPurcCreateBody, String receiverName, String senderName, String msgChatContent) {
        LOGGER.info("BEGIN: send email for talk purc.");
        try {
            boolean checkReceiveMail = userSettingService.checkReceiveMailOn(talkPurcCreateBody.getPartId());
            if (!checkReceiveMail) {
                LOGGER.warn("Do not send email. Mail setting: " + checkReceiveMail);
                return;
            } else if (checkReceiveMail) {
                if (userSettingService.checkReceivingMailInTransaction(talkPurcCreateBody.getPartId())) {
                    String template = MailUtils.getTemplateMailFirstContact(emailTemplateSendMsgNormal);
                    final String postName = talkPurcCreateBody.getPostName();
                    template = template.replace("{postName}", postName);
                    String title = MailUtils.getEmailTitle(template);
                    String body = MailUtils.getEmailContent(template)
                            .replace("{partnerNickname}", senderName)
                            .replace("{nickName}", receiverName)
                            .replace("{msgChatContent}", msgChatContent);
                    String from = mailAdmin;
                    String to = talkPurcCreateBody.getEmailTo();
                    sendEmail(from, to, title, body);
                } else {
                    LOGGER.warn("The user does not receive email from buy/sell transaction. " + checkReceiveMail);
                    return;
                }
            }
        } catch (Exception e) {
            LOGGER.error("ERROR: " + e.getMessage());
        }
    }

    @Override
    @Async
    public void sendEmailAfterReview(String nickName, String partnerNickname, String postName, String toEmail, Long userId) {
        LOGGER.info("BEGIN: send email after review.");
        try {
            if (userSettingService.checkReceiveMailOn(userId)) {
                if (userSettingService.checkReceivingMailInTransaction(userId)) {
                    String template = MailUtils.getTemplateMailFirstContact(emailTemplateReview);
                    template = template.replace("{postName}", postName);
                    String title = MailUtils.getEmailTitle(template);
                    String body = MailUtils.getEmailContent(template)
                            .replace("{nicknamePartner}", partnerNickname)
                            .replace("{nickName}", nickName);
                    String from = mailAdmin;
                    sendEmail(from, toEmail, title, body);
                }
            }
        } catch (Exception e) {
            LOGGER.error("ERROR: " + e.getMessage());
        }
        LOGGER.info("END: send email after review.");
    }

    @Override
    @Async
    public void sendEmailForReport(String toEmail, String postName, String reportType, String reportContent, String nickName) {
        LOGGER.info("BEGIN: send email after report.");
        try {
            String template = MailUtils.getTemplateMailFirstContact(emailTemplateReport);
            template = template.replace("{postName}", postName).replace("{nickName}", nickName);
            String title = MailUtils.getEmailTitle(template);
            String body = MailUtils.getEmailContent(template)
                    .replace("{reportType}", reportType)
                    .replace("{reportContent}", reportContent);
            String from = mailAdmin;
            sendEmail(from, toEmail, title, body);
        } catch (Exception e) {
            LOGGER.error("ERROR: " + e.getMessage());
        }
        LOGGER.info("END: send email after report.");
    }

    @Override
    @Async
    public void sendEmailSuspendPost(String postName, String email, String nickName, String reason) {
        LOGGER.info("BEGIN: send email after patrol suspended post.");
        sendMailNotifyAboutAccount(emailTemplateSuspendPost, email, nickName, postName, reason);
        LOGGER.info("END: send email after patrol suspended post.");
    }

    @Override
    @Async
    public void sendEmailOkeUser(String nickName, String email) {
        LOGGER.info("BEGIN: send email after patrol oke post.");
        sendMailNotifyAboutAccount(emailTemplateOkePost, email, nickName, "", "");
        LOGGER.info("END: send email after patrol oke post.");
    }

    @Override
    @Async
    public void sendEmailOkePost(String postName, String email) {
        LOGGER.info("BEGIN: send email after patrol oke post.");
        sendMailNotifyAboutAccount(emailTemplateOkePost, email, "", postName, "");
        LOGGER.info("END: send email after patrol oke post.");
    }

    @Override
    @Async
    public void sendEmailSuspendUser(String nickName, String email, String reason) {
        LOGGER.info("BEGIN: send email after patrol suspended user.");
//        if(StringUtils.isNotEmpty(reason)){
//            reason = reason.replace("\n","<br />");
//        }
        sendMailNotifyAboutAccount(emailTemplateSuspendUser, email, nickName, "", reason);
        LOGGER.info("END: send email after patrol suspended user.");
    }

    @Override
    @Async
    public void sendEmailRestorePost(String email, String nickName, String postName) {
        LOGGER.info("BEGIN: send email after restore post.");
        sendMailNotifyAboutAccount(emailTemplateRestorePost, email, nickName, postName, "");
        LOGGER.info("END: send email after restore post.");
    }

    @Override
    @Async
    public void sendEmailRestoreAccount(String email, String nickName) {
        LOGGER.info("BEGIN: send email after restore account.");
        sendMailNotifyAboutAccount(emailTemplateRestoreUser, email, nickName, "", "");
        LOGGER.info("END: send email after restore account.");
    }

    @Override
    @Async
    public void sendEmailTendToLeave(String email, String nickName, LocalDateTime leftDate) {
        LOGGER.info("BEGIN: send email after user tend to leave.");
        sendMailNotifyAboutAccount(emailTemplateTendToLeave, email, nickName, "", leftDate);
        LOGGER.info("END: send email after user tend to leave.");
    }

    private void sendMailNotifyAboutAccount(String mailTemplate, String email, String nickName, String postName, String reason) {
        try {
            String template = MailUtils.getTemplateMailFirstContact(mailTemplate);
            String title = MailUtils.getEmailTitle(template).replace("{nickName}", nickName).replace("{postName}", postName);
            String body = MailUtils.getEmailContent(template).replace("{nickName}", nickName).replace("{postName}", postName).replace("{reason}", reason);
            String from = mailAdmin;
            sendEmail(from, email, title, body);
        } catch (Exception e) {
            LOGGER.error("ERROR: " + e.getMessage());
        }
    }

    /**
     * udpate mail for tend to leave with date.
     *
     * @param mailTemplate
     * @param email
     * @param leftDate
     */
    private void sendMailNotifyAboutAccount(String mailTemplate, String email, String nickName, String postName, LocalDateTime leftDate) {
        try {
            String template = MailUtils.getTemplateMailFirstContact(mailTemplate);
            String title = MailUtils.getEmailTitle(template).replace("{nickName}", nickName).replace("{postName}", postName);
            String body = MailUtils.getEmailContent(template).replace("{monthLeave}", String.valueOf(leftDate.getMonthValue())).replace("{dayLeave}", String.valueOf(leftDate.getDayOfMonth())).replace("{nickName}", nickName);
            String from = mailAdmin;
            sendEmail(from, email, title, body);
        } catch (Exception e) {
            LOGGER.error("ERROR: " + e.getMessage());
        }
    }
}
