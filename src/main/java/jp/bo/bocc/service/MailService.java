package jp.bo.bocc.service;

import jp.bo.bocc.controller.api.request.TalkPurcCreateBody;
import jp.bo.bocc.entity.dto.ShtTalkPurcDTO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Namlong on 3/31/2017.
 */
public interface MailService {

    /**
     * Send email for single email address.
     *
     * @param from
     * @param to
     * @param subject
     * @param content
     * @return
     */
    boolean sendEmail(String from, String to, String subject, String content);

    /**
     * Send message for first contact
     * @param talkPurc
     * @param nickName
     */
    void sendEmailForFirstContact(ShtTalkPurcDTO talkPurc, String nickName, String msgContent);

    void sendEmailForgotPassword(String email, String nickName, String password);

    void sendEmailChatNormal(TalkPurcCreateBody talkPurcCreateBody, String receiverName, String senderName, String msgChatContent);
    void sendEmailSendOrderRequest(String email, String postName, String partnerName, String ownerName, Long userId);
    void sendEmailAcceptOrderRequest(String email, String postName, String partnerName, String ownerName, Long userId);
    void sendEmailSentQaToAdmin(String email, String nickName, String qaType, String qaContent);
    void sendEmailResponseQa(String email, String userName,String nickName,String msgContent, String qaType, String qaContent);
    void sendEmailCompletedQa(String email, String userName,String nickName);

    void sendEmailAfterReview(String nickName, String partnerNickname, String postName, String toEmail, Long userId);

    void sendEmailForReport(String toEmail, String postName, String reportType, String reportContent, String nickName);

    void sendEmailSuspendUser(String nickName, String email, String reason);

    void sendEmailSuspendPost(String postName, String email, String nickName,String reason);

    void sendEmailOkeUser(String nickName, String email);

    void sendEmailOkePost(String postName, String email);

    void sendEmailRestorePost(String email, String nickName, String postName);

    void sendEmailRestoreAccount(String email, String nickName);

    void sendEmailTendToLeave(String email, String nickName, LocalDateTime leftDate);
}
