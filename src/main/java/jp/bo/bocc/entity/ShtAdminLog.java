package jp.bo.bocc.entity;

import jp.bo.bocc.enums.PatrolActionEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

/**
 * author NguyenThuong on 3/14/2017.
 */
@Entity
@Table(name = "SHT_ADMIN_LOG")
@DynamicInsert @DynamicUpdate
@NoArgsConstructor @AllArgsConstructor
public class ShtAdminLog extends BaseEntity{

    @Id
    @SequenceGenerator(name = "seq", sequenceName = "SHT_ADMIN_LOG_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    @Column(name = "ADMIN_LOG_ID")
    @Getter @Setter
    private Long adminLogId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ADMIN_ID")
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter @Setter
    private ShmAdmin shmAdmin;

    /**
     * PATROL_POST_OK(0),PATROL_POST_RESERVED(1), PATROL_POST_SUSPENDED(2),
     * PATROL_USER_OK(3),PATROL_USER_RESERVED(4), PATROL_USER_SUSPENDED(5)
     */
    @Getter @Setter
    @Column(name = "ADMIN_LOG_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private PatrolActionEnum adminLogType;

    @Getter @Setter
    @Column(name = "ADMIN_LOG_TITLE")
    private String adminLogTitle;

    @Getter @Setter
    @Column(name = "ADMIN_LOG_CONT")
    private String adminLogCont;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_ID")
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter @Setter
    private ShmPost shmPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID")
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter @Setter
    private ShmUser shmUser;

}
