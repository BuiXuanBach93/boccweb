package jp.bo.bocc.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * Created by Namlong on 3/14/2017.
 */
@Entity
@Table(name = "SHT_QA")
@DynamicInsert @DynamicUpdate
@NoArgsConstructor @AllArgsConstructor
@Where(clause = "CMN_DELETE_FLAG=0")
public class ShtQa extends BaseEntity {

    @Id
    @Column(name = "QA_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SHT_QA_SEQ")
    @SequenceGenerator(name = "SHT_QA_SEQ", sequenceName = "SHT_QA_SEQ", allocationSize = 1, initialValue = 1)
    @Getter @Setter
    private Long qaId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID")
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter @Setter
    private ShmUser shmUser;

    @Getter @Setter
    @Column(name = "QA_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private QaTypeEnum qaType;

    @Getter @Setter
    @Column(name = "QA_STATUS")
    @Enumerated(EnumType.ORDINAL)
    private QaStatusEnum qaStatus;

    @Column(name = "QA_TITLE")
    @Getter @Setter
    private String qaTitle;

    @Getter @Setter
    @Transient
    private String firstQaMsg;

    @Getter @Setter
    @Transient
    private String lastQaMsg;

    @Getter @Setter
    @Transient
    private Integer countNewMsg;

    @Getter @Setter
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "QA_CONTENT_TYPE")
    private QaContentTypeEnum qaContentType;


    @Getter @Setter
    @Transient
    private boolean haveFeedback;

    @Getter @Setter
    @Transient
    private LocalDateTime lastUpdateAt;

    public enum QaTypeEnum{
        OTHER, ACCOUNT_PROBLEM, POST_PROBLEM, USAGE_PROBLEM, HELP, LEAVING
    }

    public enum QaStatusEnum{
        NO_RESPONSE, INPROGRESS, RESOLVED
    }

    public enum QaContentTypeEnum{
        SYSTEM, QA, SYSTEM_PUSH_ALL
    }

    @Override
    public void onPrePersist() {
        super.onPrePersist();
        qaStatus = QaStatusEnum.NO_RESPONSE;
    }

    @Transient
    @Getter @Setter
    private String createdAtTxt;
}
