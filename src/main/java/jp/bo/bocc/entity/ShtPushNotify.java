package jp.bo.bocc.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jp.bo.bocc.helper.StringUtils;
import jp.bo.bocc.system.apiconfig.bean.AppContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;

/**
 * @author DonBach
 */
@Entity
@Table(name = "SHT_PUSH_NOTIFY")
@DynamicInsert  @DynamicUpdate
@NoArgsConstructor
@AllArgsConstructor
@Where(clause="CMN_DELETE_FLAG=0")
@SQLDelete(sql = "UPDATE \"SHT_PUSH_NOTIFY\" SET CMN_DELETE_FLAG = 1 WHERE PUSH_ID = ?")
public class ShtPushNotify extends BaseEntity {

	@Id
	@Column(name = "PUSH_ID")
	@SequenceGenerator(name = "seq", sequenceName = "SHT_PUSH_NOTIFY_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
	@Getter @Setter
	private Long pushId;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ADMIN_ID")
	@NotFound(action = NotFoundAction.IGNORE)
	@Getter @Setter
	private ShmAdmin adminSender;

	@Column(name = "PUSH_TITLE")
	@Getter @Setter
	private String pushTitle;

	@Column(name = "PUSH_CONTENT")
	@Getter @Setter
	private String pushContent;

	@Column(name = "PUSH_ANDROID")
	@Getter @Setter
	private Boolean pushAndroid;

	@Column(name = "PUSH_IOS")
	@Getter @Setter
	private Boolean pushIos;

	@Column(name = "PUSH_IMMEDIATE")
	@Getter @Setter
	private Boolean pushImmediate;

	@Getter @Setter
	@Column(name = "ANDROID_NUMBER")
	private Long androidNumber;

	@Getter @Setter
	@Column(name = "IOS_NUMBER")
	private Long iosNumber;

	@Getter @Setter
	@Column(name = "ANDROID_READ_NUMBER")
	private Long androidReadNumber;

	@Getter @Setter
	@Column(name = "IOS_READ_NUMBER")
	private Long iosReadNumber;


	@Column(name = "PUSH_STATUS", nullable = false)
	@Getter @Setter
	@Enumerated
	private PushStatus pushStatus;

	@Column(name = "PUSH_ACTION_TYPE")
	@Getter @Setter
	@Enumerated
	private PushActionType pushActionType;

	@Column(name = "SEND_DATE")
	@Getter @Setter
	private LocalDateTime sendDate;

	@Column(name = "TIMER_DATE")
	@Getter @Setter
	private LocalDateTime timerDate;

	@Transient
	@Getter @Setter
	private Long pushNumber;

	@Transient
	@Getter @Setter
	private Long readNumber;

	@Transient
	@Getter @Setter
	private String timerDateStr;

	public enum PushStatus {
		SENT, WAITING, SUSPENDED
	}

	public enum PushActionType {
		JUST_PUSH, JUST_MESSAGE, PUSH_AND_MESSAGE
	}
}
