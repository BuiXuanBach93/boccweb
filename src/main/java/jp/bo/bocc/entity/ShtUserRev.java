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
@Table(name = "SHT_USER_REV")
@DynamicInsert @DynamicUpdate
@NoArgsConstructor @AllArgsConstructor
@Where(clause = "CMN_DELETE_FLAG=0")
public class ShtUserRev extends BaseEntity {

    @Id
    @Column(name = "USER_REV_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SHT_USER_REV_SEQ")
    @SequenceGenerator(name = "SHT_USER_REV_SEQ", sequenceName = "SHT_USER_REV_SEQ", allocationSize = 1)
    @Getter @Setter
    private Long userRevId;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "TALK_PURC_ID", nullable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter @Setter
    private  ShtTalkPurc shtTalkPurc;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_REVIEW_FROM_USER_ID", nullable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter @Setter
    private  ShmUser fromShmUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_REVIEW_TO_USER_ID", nullable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter @Setter
    private  ShmUser toShmUser;

    @Column(name = "USER_REV_TYPE")
    @Getter @Setter
    @Enumerated(EnumType.ORDINAL)
    private UserReviewType userRevType;

    @Column(name = "USER_REV_RATE")
    @Enumerated(EnumType.ORDINAL)
    @Getter @Setter
    private UserReviewRate userRevRate;

    @Column(name = "USER_REV_CONT")
    @Getter @Setter
    private String userRevCont;

    @Transient
    @Getter @Setter
    private String createdAtTxt;

    public enum UserReviewType {
        OWNER_TO_PARTNER, PARTNER_TO_OWNER
    }

    public enum UserReviewRate {
        GOOD, FAIR, BAD
    }
}
