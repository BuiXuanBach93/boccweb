package jp.bo.bocc.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * @author manhnt
 */
@Entity
@Table(name = "SHM_USER")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "CMN_DELETE_FLAG=0")
@SQLDelete(sql = "UPDATE \"SHM_USER\" SET CMN_DELETE_FLAG = 1 WHERE USER_ID = ?")
public class ShmUser extends BaseEntity {

    @Id
    @Column(name = "USER_ID")
    @SequenceGenerator(name = "seq", sequenceName = "SHM_USER_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    @Getter
    @Setter
    @Access(AccessType.PROPERTY)
    private Long id;

    @Column(name = "USER_BSID", length = 20, updatable = false)
    @Getter
    @Setter
    private String bsid;

    @Column(name = "USER_FIRST_NAME", length = 40)
    @Getter
    @Setter
    private String firstName;

    @Column(name = "USER_LAST_NAME", length = 40)
    @Getter
    @Setter
    private String lastName;

    @Column(name = "USER_NICK_NAME", length = 40)
    @Getter
    @Setter
    private String nickName;

    @Column(name = "USER_PWD", length = 50)
    @Getter
    @Setter
    private String password;

    @Column(name = "USER_EMAIL", length = 100)
    @Getter
    @Setter
    private String email;

    @Column(name = "USER_PHONE", length = 13)
    @Getter
    @Setter
    private String phone;

    /**
     * Gender: 0 - Female; 1 - Male;
     */
    @Column(name = "USER_GENDER")
    @Enumerated
    @Getter
    @Setter
    private Gender gender;

    @Column(name = "USER_DATE_OF_BIRTH")
    @Getter
    @Setter
    private LocalDate dateOfBirth;

    @JoinColumn(name = "USER_ADDRESS_ID")
    @Getter
    @Setter
    @ManyToOne(fetch = FetchType.EAGER)
    private ShmAddr address;

    @Column(name = "USER_DESCR", length = 500)
    @Getter
    @Setter
    private String description;

    @Column(name = "USER_CAREER")
    @Getter
    @Setter
    private String career;

    @Column(name = "TEND_TO_LEAVE_DATE")
    @Getter
    @Setter
    private LocalDateTime tendToLeaveDate;

    @Column(name = "LEFT_DATE")
    @Getter
    @Setter
    private LocalDateTime leftDate;

    @Column(name = "SYNC_EXC_ID")
    @Getter
    @Setter
    private Long syncExcId;


    @Column(name = "USER_JOB")
    @Getter
    @Setter
    private String job;

    @JoinColumn(name = "USER_AVTR")
    @OneToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter
    @Setter
    private ShrFile avatar;

    @Transient
    private String avatarRaw;

    @Transient
    @Getter @Setter
    private Long addressId;

    @JsonIgnore //Ignore serialize
    public String getAvatarRaw() {
        return avatarRaw;
    }

    @JsonProperty //Accept deserialize
    public void setAvatarRaw(String avatarRaw) {
        this.avatarRaw = avatarRaw;
    }

    /**
     * 0 - EmailUnactivated, 1 - SMSUnactivated, 2 - NoPassword,
     * 3 - NoProfile, 4 - Activated, 5 - TendToLeave, 6 - Left
     */
    @Enumerated
    @Column(name = "USER_STATUS")
    @Getter
    @Setter
    private Status status;

    /**
     * 0 - Normal, 1 - Pending, 2 - Suspended, 3 - Updated, 4-OK
     */
    @Enumerated
    @Column(name = "USER_CTRL_STATUS")
    @Getter
    @Setter
    private CtrlStatus ctrlStatus;

    /**
     * 0 - Uncensored, 1 - Censoring, 2 - Censored
     */
    @Enumerated
    @Column(name = "USER_PTRL_STATUS")
    @Getter
    @Setter
    private PtrlStatus ptrlStatus;

    @Transient
    private Long postNumber;

    @Getter @Setter
    @Transient
    private int sequentNumber;

    @Transient
    @Getter @Setter
    private ShmAddr province;

    public Long getPostNumber() {
        return postNumber;
    }

    public void setPostNumber(Long postNumber) {
        this.postNumber = postNumber;
    }

    public enum Gender {
        FEMALE, MALE
    }

    public enum Status {
        EMAIL_UNACTIVATED, SMS_UNACTIVATED, NO_PASSWORD, NO_PROFILE, ACTIVATED, TEND_TO_LEAVE, LEFT
    }

    public enum CtrlStatus {
        NORMAL, PENDING, SUSPENDED, UPDATED, OK
    }

    public enum PtrlStatus {
        UNCENSORED, CENSORING, CENSORED
    }

    public ShmUser(Long userId) {
        super();
        this.id = userId;
    }

    @Column(name = "USER_IS_IN_PATROL")
    @Getter @Setter
    private Boolean isInPatrol;

    @Column(name = "USER_TIME_PATROL")
    @Getter @Setter
    private LocalDateTime timePatrol;

    @Transient
    @Getter @Setter
    private Date formatUpdatedAt;

    @Transient
    @Getter @Setter
    private String deviceToken;

    @Transient
    @Getter @Setter
    private String osType;

    @Column(name = "USER_UPDATE_AT")
    @Getter @Setter
    private LocalDateTime userUpdateAt;

    @Transient
    @Getter @Setter
    private Boolean isSameCompany;

    @Override
    public void onPrePersist() {
        super.onPrePersist();
        setUserUpdateAt(LocalDateTime.now());
    }

}
