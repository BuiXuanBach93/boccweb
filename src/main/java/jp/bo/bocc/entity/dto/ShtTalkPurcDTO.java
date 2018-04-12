package jp.bo.bocc.entity.dto;


import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShrFile;
import jp.bo.bocc.entity.ShtTalkPurc;
import jp.bo.bocc.entity.ShtTalkPurcMsg;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

public class ShtTalkPurcDTO extends BaseDTO {

  @Getter @Setter
  private Long talkPurcId;

  @Getter @Setter
  private long shmPostId;

  @Getter @Setter
  private long shmUserId;

  @Getter @Setter
  private ShtTalkPurc.TalkPurcStatusEnum talkPurcStatus;

  @Getter @Setter
  private boolean ownerBlckFlag;

  @Getter @Setter
  private boolean partBlckFlag;

  @Getter @Setter
  private ShrFile userAvatarInTalkPurcList;

  @Getter @Setter
  private String lastMesageInTalkPurcList;

  @Getter @Setter
  private String userNickname;

  @Getter @Setter
  Page<ShtTalkPurcMsgDTO> shtTalkPurcMsgDTOS;

  @Getter @Setter
  private String purcMsgSystem;

  @Getter @Setter
  List<ShtTalkPurcMsgDTO> shtTalkPurcMsgDTOsList;

  @Getter @Setter
  Integer totalNewMsgFromOther;

  @Getter @Setter
  Long talkPurcMsgCreator;

  @Getter @Setter
  private ShrFile partnerAvatarPath;

  @Getter @Setter
  private boolean isUserAppMsg;

  @Getter @Setter
  boolean lastMsgIsOwnerPost;

  @Getter @Setter
  private String partnerNickName;

  @Getter @Setter
  private String emailOwnerPost;

  @Getter @Setter
  private String postName;

  @Getter @Setter
  private String ownerPostNickName;
  @Getter @Setter
  private Long ownerPostId;

  @Setter @Getter
  private String talkPurcMsgCreatorNickName;

  @Setter @Getter
  private String msgContent;

  @Getter @Setter
  private ShtTalkPurcMsg.TalkPurcMsgTypeEnum msgType;

  @Setter @Getter
  private Long partnerId;

  @Setter @Getter
  private Long msgId;


  @Getter @Setter
  private String avatarPartnerpath;

  @Getter @Setter
  private String avatarOwnerPostpath;

  @Getter @Setter
  private ShmUser.CtrlStatus ownerCtrlStatus;

  @Getter @Setter
  private ShmUser.CtrlStatus partnerCtrlStatus;

  @Getter @Setter
  private ShmUser.Status ownerStatus;

  @Getter @Setter
  private ShmUser.Status partnerStatus;
}
