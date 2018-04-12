package jp.bo.bocc.controller.api.response;

import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShtTalkPurc;
import jp.bo.bocc.entity.dto.ShmPostDTO;
import jp.bo.bocc.entity.dto.ShtTalkPurcMsgDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by DonBach on 4/21/2017.
 */
@NoArgsConstructor
@AllArgsConstructor
public class TalkPurcDetailResponse {

    @Getter @Setter
    private ShmPostDTO shmPost;

    @Getter @Setter
    private ShtTalkPurc.TalkPurcStatusEnum talkPurcStatus;

    @Getter @Setter
    private List<ShtTalkPurcMsgDTO> listTalkPurcMsg;

    @Getter @Setter
    private String partnerName;

    @Getter @Setter
    private Boolean isReviewed;

    @Getter @Setter
    private ShmUser.CtrlStatus ownerCtrlStatus;

    @Getter @Setter
    private ShmUser.CtrlStatus partnerCtrlStatus;

    @Getter @Setter
    private ShmUser.Status ownerStatus;

    @Getter @Setter
    private ShmUser.Status partnerStatus;

    @Getter @Setter
    private Boolean canReview;
    @Getter @Setter
    private boolean blocked;
    @Getter @Setter
    private boolean muted;
    @Getter @Setter
    private LocalDateTime tendToSellTime;
}
