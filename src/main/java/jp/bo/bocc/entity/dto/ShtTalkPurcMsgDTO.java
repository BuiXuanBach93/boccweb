package jp.bo.bocc.entity.dto;


import jp.bo.bocc.entity.ShrFile;
import jp.bo.bocc.entity.ShtTalkPurcMsg;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

public class ShtTalkPurcMsgDTO extends BaseDTO {

  @Getter @Setter
  private Long talkPurcMsgId;

  @Getter @Setter
  private Long talkPurcId;

  @Getter @Setter
  private String talkPurcMsgCont;

  @Getter @Setter
  private String talkPurcMsgStatus;

  @Getter @Setter
  private Long shmUserCreatorId;

  @Getter @Setter
  private LocalDateTime createdAt;

  @Getter @Setter
  private Long userId;

  @Getter @Setter
  private ShtTalkPurcMsg.TalkPurcMsgTypeEnum msgType;

  @Getter @Setter
  private boolean isUserAppMsg;

  @Getter @Setter
  private Long partnerId;

  @Getter @Setter
  private ShrFile partnerAvatar;

  @Getter @Setter
  private String partnerNickName;
}
