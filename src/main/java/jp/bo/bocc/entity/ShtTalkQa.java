package jp.bo.bocc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Namlong on 3/14/2017.
 */
@Entity
@Table(name = "SHT_TALK_QA")
@DynamicInsert @DynamicUpdate
@NoArgsConstructor @AllArgsConstructor
@Where(clause = "CMN_DELETE_FLAG=0")
public class ShtTalkQa extends BaseEntity {

    @Id
    @Column(name = "TALk_QA_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SHT_TALK_QA_SEQ")
    @SequenceGenerator(name = "SHT_TALK_QA_SEQ", sequenceName = "SHT_TALK_QA_SEQ", allocationSize = 1, initialValue = 1)
    @Getter @Setter
    private Long talkQaId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "QA_ID")
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter @Setter
    private ShtQa shtQa;

    @Column(name = "TALK_QA_MSG")
    @Getter @Setter
    private String talkQaMsg;

    @Column(name = "TALK_QA_MSG_STATUS")
    @Getter @Setter
    @Enumerated(EnumType.ORDINAL)
    private TalkQaMsgStatusEnum talkQaMsgStatus;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ADMIN_ID")
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter @Setter
    private ShmAdmin shmAdmin;

    @Column(name = "FROM_ADMIN")
    @Getter @Setter
    private Boolean fromAdmin;

    public enum TalkQaMsgStatusEnum{
        SENDING, RECEIVED
    }

    @Override
    public void onPrePersist() {
        super.onPrePersist();
        talkQaMsgStatus = TalkQaMsgStatusEnum.SENDING;
    }
}
