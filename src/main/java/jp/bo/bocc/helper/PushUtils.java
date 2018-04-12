package jp.bo.bocc.helper;

import jp.bo.bocc.entity.ShtPushNotify;
import jp.bo.bocc.push.PushMsgCommon;
import jp.bo.bocc.push.PushMsgDetail;

/**
 * Created by Namlong on 5/24/2017.
 */
public class PushUtils {

    public static PushMsgCommon buildPushMsgCommon(String msgCont) {
        PushMsgCommon pushMsg = new PushMsgCommon();
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(msgCont))
            pushMsg.setMsgContent(msgCont);
        return pushMsg;
    }

    public static PushMsgDetail buildPushMsgDetail(PushMsgDetail.NtfTypeEnum ntfType, String msgChat, String dataRef, Long talkPurcId, Long postId, Long qaId) {
        PushMsgDetail pushMsgDetail = new PushMsgDetail();
        pushMsgDetail.setNtfType(ntfType);
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(msgChat))
            pushMsgDetail.setMsgContent(msgChat);
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(dataRef))
            pushMsgDetail.setDataRef(dataRef);
        if (talkPurcId != null)
            pushMsgDetail.setTalkPurcId(talkPurcId);
        if (postId != null)
            pushMsgDetail.setPostId(postId);
        if (qaId != null)
            pushMsgDetail.setQaId(qaId);

        return pushMsgDetail;
    }

    public static PushMsgDetail buildPushMsgDetail(PushMsgDetail.NtfTypeEnum ntfType, String msgChat, String dataRef, Long talkPurcId, Long postId, Long qaId, Long pushId, Long userId) {
        PushMsgDetail pushMsgDetail = new PushMsgDetail();
        pushMsgDetail.setNtfType(ntfType);
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(msgChat))
            pushMsgDetail.setMsgContent(msgChat);
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(dataRef))
            pushMsgDetail.setDataRef(dataRef);
        if (talkPurcId != null)
            pushMsgDetail.setTalkPurcId(talkPurcId);
        if (postId != null)
            pushMsgDetail.setPostId(postId);
        if (qaId != null)
            pushMsgDetail.setQaId(qaId);
        if (pushId != null){
            pushMsgDetail.setPushId(pushId);
        }
        if(userId != null){
            pushMsgDetail.setReceiverId(userId);
        }
        return pushMsgDetail;
    }


    public static PushMsgDetail buildPushMsgDetailForPushAll(PushMsgDetail.NtfTypeEnum ntfType, Long qaId, String msgCont, ShtPushNotify.PushActionType actionType) {
        PushMsgDetail pushMsgDetail = new PushMsgDetail();
        pushMsgDetail.setNtfType(ntfType);
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(msgCont))
            pushMsgDetail.setMsgContent(msgCont);
        if (qaId != null && actionType != ShtPushNotify.PushActionType.JUST_PUSH){
            pushMsgDetail.setQaId(qaId);
        }
        return pushMsgDetail;
    }
}
