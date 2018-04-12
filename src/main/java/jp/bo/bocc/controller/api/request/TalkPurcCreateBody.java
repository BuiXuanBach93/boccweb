package jp.bo.bocc.controller.api.request;

import jp.bo.bocc.entity.ShtTalkPurcMsg;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Namlong on 3/27/2017.
 */
public class TalkPurcCreateBody {
    @Getter @Setter
    private Long postId;

    @Getter @Setter
    private Long partId;

    @Getter @Setter
    private String msgContent;

    @Getter @Setter
    private ShtTalkPurcMsg.TalkPurcMsgTypeEnum msgType;

    @Getter @Setter
    private Long talkPurcId;

    @Getter @Setter
    private String msgStatus;

    @Getter @Setter
    private String partnerNickname;

    @Getter @Setter
    private String postName;

    @Getter @Setter
    private String emailTo;

    @Getter @Setter
    private Long ownerPostId;

    @Getter @Setter
    private String ownerPostNickName;

    @Getter @Setter
    private String accessToken;

    @Getter @Setter
    private Long msgId;

    @Getter @Setter
    private LocalDateTime createdAt;

    @Getter @Setter
    private String talkPurcMsgCreatorNickName;

    @Getter @Setter
    private String avatarPartnerpath;

    @Getter @Setter
    private String avatarOwnerPostpath;

    @Getter @Setter
    private List<String> productImgList;
}
