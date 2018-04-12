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
 * Created by DonBach on 5/25/2017.
 */

@Entity
@Table(name = "SHT_USER_SETTING")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Where(clause = "CMN_DELETE_FLAG=0")
public class ShtUserSetting extends BaseEntity{

    @Id
    @Column(name = "USER_SETTING_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SHT_USER_SETTING_SEQ")
    @SequenceGenerator(name = "SHT_USER_SETTING_SEQ", sequenceName = "SHT_USER_SETTING_SEQ", allocationSize = 1)
    @Getter
    @Setter
    private Long userSettingId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "USER_ID")
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter @Setter
    private ShmUser shmUser;

    @Column(name = "RECEIVE_EMAIL")
    @Getter @Setter
    private Boolean receiveEmail;

    @Column(name = "RECEIVE_PUSH")
    @Getter @Setter
    private Boolean receivePush;

    @Column(name = "PUSH_TALKROOM_FIRST_MSG")
    @Getter @Setter
    private Boolean pushTalkRoomFirstMsg;

    @Column(name = "PUSH_TALKROOM_TRANSACTION")
    @Getter @Setter
    private Boolean pushTalkRoomTransaction;

    @Column(name = "PUSH_FAVORITE")
    @Getter @Setter
    private Boolean pushFavorite;

    @Column(name = "MAIL_TALKROOM_FIRST_MSG")
    @Getter @Setter
    private Boolean mailTalkRoomFirstMsg;

    @Column(name = "MAIL_TALKROOM_TRANSACTION")
    @Getter @Setter
    private Boolean mailTalkRoomTransaction;
}
