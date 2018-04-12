package jp.bo.bocc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;

import javax.persistence.*;

/**
 * Created by Namlong on 3/14/2017.
 */
@Entity
@Table(name = "SHT_TALK_PURC_MSG")
@DynamicInsert @DynamicUpdate
@NoArgsConstructor @AllArgsConstructor
@Where(clause="CMN_DELETE_FLAG=0")
public class ShtTalkPurcMsg extends BaseEntity {

    @Id
    @Column(name = "TALK_PURC_MSG_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SHT_TALK_PURC_MSG_SEQ")
    @SequenceGenerator(name = "SHT_TALK_PURC_MSG_SEQ", sequenceName = "SHT_TALK_PURC_MSG_SEQ", allocationSize = 1)
    @Getter @Setter
    private Long talkPurcMsgId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TALK_PURC_ID", nullable = false)
    @Getter @Setter
    private ShtTalkPurc shtTalkPurc;

    @Column(name = "TALK_PURC_MSG_CONT")
    @Getter @Setter
    private String talkPurcMsgCont;

    @Column(name = "TALK_PURC_MSG_STATUS")
    @Getter @Setter
    @Enumerated(EnumType.ORDINAL)
    private TalkPurcMsgStatusEnum talkPurcMsgStatus;

    @Column(name = "ISACTIVE")
    @Getter @Setter
    private Boolean isActive;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TALK_PURC_MSG_CREATOR", nullable = false)
    @Getter @Setter
    private ShmUser shmUserCreator;

    public enum TalkPurcMsgStatusEnum{
        SENDING, RECEIVED
    }

    @Override
    public void onPrePersist() {
        super.onPrePersist();
        talkPurcMsgStatus = TalkPurcMsgStatusEnum.SENDING;
        isActive = true;
    }

    /**
     * 0-NORMAL<br/>
     * 1-ORDER_SENT_FOR_OWNER<br/>
     * 2-ORDER_ACCEPTED<br/>
     * 3-ORDER_REJECTED<br/>
     * 4-SYS_MSG_FOR_OWNER<br/>
     * 5-SYS_MSG_FOR_PARTNER<br/>
     * 6-ORDER_SENT_FOR_PARTNER<br/>
     * 7-REVIEW_FOR_OWNER<br/>
     * 8-REVIEW_FOR_PARTNER<br/>
     * 9-SYS_NG_MSG_FOR_PARTNER<br/>
     * 10-IMAGE
     */
    @Column(name = "TALK_PURC_MSG_TYPE")
    @Getter @Setter
    @Enumerated(EnumType.ORDINAL)
    private TalkPurcMsgTypeEnum talkPurcMsgType;

    public enum TalkPurcMsgTypeEnum{
        NORMAL, ORDER_SENT_FOR_OWNER, ORDER_ACCEPTED, ORDER_REJECTED, SYS_MSG_FOR_OWNER, SYS_MSG_FOR_PARTNER, ORDER_SENT_FOR_PARTNER, REVIEW_FOR_OWNER, REVIEW_FOR_PARTNER,SYS_NG_MSG_FOR_PARTNER, IMAGE
    }
}
