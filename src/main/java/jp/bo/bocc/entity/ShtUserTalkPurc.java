package jp.bo.bocc.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Created by Namlong on 4/7/2017.
 */
@Entity
@Table(name = "SHT_USER_TALK_PURC")
@Where(clause = "CMN_DELETE_FLAG=0")
public class ShtUserTalkPurc extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SHT_USER_TALK_PURC_SEQ")
    @SequenceGenerator(name = "SHT_USER_TALK_PURC_SEQ", sequenceName = "SHT_USER_TALK_PURC_SEQ", allocationSize = 1)
    @Column(name = "SHT_USER_TALK_PURC_ID")
    private long userTalkPurcId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SHT_USER_TALK_PURC_FROM")
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter @Setter
    private ShmUser shmUserFrom;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "SHT_USER_TALK_PURC_TO")
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter @Setter
    private ShmUser shmUserTo;

//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "TALK_PURC_ID")
//    @NotFound(action = NotFoundAction.IGNORE)
//    @Getter @Setter
//    private ShtTalkPurc shtTalkPurc;

    @Column(name = "USER_TALK_PURC_BLOCK")
    @Getter @Setter
    private boolean userTalkPurcBlock;

    @Column(name = "USER_TALK_PURC_MUTE")
    @Getter @Setter
    private boolean userTalkPurcMute;

    @Column(name = "USER_BLOCK_TIME")
    @Getter @Setter
    private LocalDateTime userBlockedTIme;
}
