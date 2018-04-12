package jp.bo.bocc.push;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Namlong on 5/24/2017.
 */
public class PushMsgDetail extends PushMsgCommon {
    @Getter
    @Setter
    String dataRef;

    @Getter @Setter
    Long receiverId;

    @Getter
    @Setter
    Long talkPurcId;
    @Getter
    @Setter
    Long postId;
    @Getter
    @Setter
    Long qaId;

    @Getter
    @Setter
    String msgChat;

    @Getter @Setter
    Long pushId;

    @Getter @Setter
    NtfTypeEnum ntfType;

    @Getter @Setter
    Long adminId;

    public enum NtfTypeEnum {
        TR_FIRST_CONTACT,
        TR_SEND_MSG,
        TR_ORDER_REQUEST_SEND,
        TR_ORDER_REQUEST_REPLY,
        REVIEW_USER,
        TR_NOT_REVIEW_FOR_OWNER_POST,
        TR_NOT_REVIEW_FOR_OWNER_PARTNER,
        POST_HANDLE_SUSPENDED,
        USER_HANDLE_SUSPENDED,
        POST_HANDLE_OK,
        USER_HANDLE_OK,
        TR_ADM_ANSWER_QA,
        TR_ADM_DONE_QA,
        USER_FAVORITE,
        USER_TEND_TO_LEAVE,
        USER_TEND_TO_LEAVE_FOR_OWNER,
        USER_TEND_TO_LEAVE_FOR_PARTNER,
        USER_TEND_TO_LEAVE_DONE_FOR_OWNER,
        USER_TEND_TO_LEAVE_DONE_FOR_PARTNER,
        ADMIN_PUSH_NOTIFY,
        POST_LIKED_CHANGE_INFO
    }
}
