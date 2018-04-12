package jp.bo.bocc.entity;

import jp.bo.bocc.enums.OSTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Created by Namlong on 5/18/2017.
 */
@Entity
@Table(name = "SHT_USER_NTF")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Where(clause="CMN_DELETE_FLAG=0")
public class ShtUserNtf extends BaseEntity {
    @Id
    @Column(name = "USER_NTF_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SHT_USER_NTF_SEQ")
    @SequenceGenerator(name = "SHT_USER_NTF_SEQ", sequenceName = "SHT_USER_NTF_SEQ", allocationSize = 1)
    @Getter
    @Setter
    private long userNtfId;

    @JoinColumn(name = "USER_ID")
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter @Setter
    private ShmUser shmUser;

    @Column(name = "USER_NTF_DEVICE_TOKEN")
    @Getter @Setter
    private String userNtfDeviceToken;

    /**
     * 0-IOS (Apple), 1-Android (other device)
     */
    @Column(name = "USER_NTF_OS_TYPE")
    @Getter @Setter
    @Enumerated(EnumType.ORDINAL)
    private OSTypeEnum userNtfOsType;

    /**
     * false - not subscribe, true =subscribed
     */
    @Column(name = "USER_NTF_SUBSCRIBED")
    @Getter @Setter
    private Boolean  userNtfSubscribed;

    @Column(name = "USER_NTF_SUBSCRIBED_ARN")
    @Getter @Setter
    private String  userNtfSubscribedARN;

}
