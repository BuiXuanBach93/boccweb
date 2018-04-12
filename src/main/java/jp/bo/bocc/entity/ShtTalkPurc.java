package jp.bo.bocc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Created by Namlong on 3/14/2017.
 */
@Entity
@Table(name = "SHT_TALK_PURC")
@DynamicInsert @DynamicUpdate
@NoArgsConstructor @AllArgsConstructor
@Where(clause="CMN_DELETE_FLAG=0")
public class ShtTalkPurc extends BaseEntity {

    @Id
    @Column(name = "TALK_PURC_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SHT_TALK_PURC_SEQ")
    @SequenceGenerator(name = "SHT_TALK_PURC_SEQ", sequenceName = "SHT_TALK_PURC_SEQ", allocationSize = 1)
    @Access(AccessType.PROPERTY)
    @Getter @Setter
    private Long talkPurcId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TALK_PURC_POST_ID")
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter @Setter
    private ShmPost shmPost;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TALK_PURC_PART_ID")
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter @Setter
    private ShmUser shmUser;

    @Column(name = "TALK_PURC_STATUS")
    @Getter @Setter
    @Enumerated(EnumType.ORDINAL)
    private TalkPurcStatusEnum talkPurcStatus;

    public enum TalkPurcStatusEnum{
        TALKING, TEND_TO_SELL, CLOSED
    }

    @Column(name = "TALK_PURC_TEND_TO_SELL_TIME")
    @Getter @Setter
    private LocalDateTime talkPurcTendToSellTime;

    @Override
    public void onPrePersist() {
        super.onPrePersist();
        talkPurcStatus = TalkPurcStatusEnum.TALKING;
    }

    @Getter @Setter
    @Transient
    private String partnerAvatar;
}
