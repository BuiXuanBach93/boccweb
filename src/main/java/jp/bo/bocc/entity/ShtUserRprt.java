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
@Table(name = "SHT_USER_RPRT")
@DynamicInsert @DynamicUpdate
@NoArgsConstructor @AllArgsConstructor
@Where(clause = "CMN_DELETE_FLAG=0")
public class ShtUserRprt extends BaseEntity {

    @Id
    @Column(name = "USER_RPRT_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SHT_USER_RPRT_SEQ")
    @SequenceGenerator(name = "SHT_USER_RPRT_SEQ", sequenceName = "SHT_USER_RPRT_SEQ", allocationSize = 1)
    @Getter @Setter
    private Long userRprtId;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "POST_ID")
    @Getter @Setter
    private ShmPost shmPost;

    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    @JoinColumn(name = "USER_ID")
    @Getter @Setter
    private ShmUser shmUser;

    @Column(name = "USER_RPRT_TYPE")
    @Getter @Setter
    @Enumerated(EnumType.ORDINAL)
    private UserReportTypeEnum userRprtType;

    @Column(name = "USER_RPRT_CONT")
    @Getter @Setter
    private String userRprtCont;

    @Getter @Setter
    @Column(name = "REPORT_PTRL_STATUS")
    @Enumerated(EnumType.ORDINAL)
    private ReportPatrolStatus reportPtrlStatus;

    public enum ReportPatrolStatus {
        UNCENSORED,
        CENSORING,
        CENSORED
    }

    @Transient
    @Getter @Setter
    private String reportStatusStr;

    @Transient
    @Getter @Setter
    private String reportTitle;

    public enum UserReportTypeEnum{
        INVALID_IMAGE_NAME(0, "画像・商品名 が 不適切 で ある。"),
        INVALID_CONTAIN_PERSONAL_INFO(1, "個人 を 特定 できる 内容 が 含ま れ て いる。"),
        INVALID_CONTENT(2, "誹謗中傷 の 内容 が 含ま れ て いる。"),
        INVALID_CHEAT(3, "詐欺の疑いがある。"),
        INVALID_TERM(4, "その他 規約 に 反する内容 が ある。");

        private int index;
        private String value;

        UserReportTypeEnum(int index, String value) {
            this.index = index;
            this.value = value;
        }
        public String value(){
            return value;
        }

        public int index(){
            return index;
        }
    }
}
