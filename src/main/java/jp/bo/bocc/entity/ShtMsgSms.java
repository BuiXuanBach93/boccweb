package jp.bo.bocc.entity;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Where;

import javax.persistence.*;

/**
 * @author manhnt
 */
@Entity
@Table(name = "SHT_MSG_SMS")
@DynamicInsert @DynamicUpdate
@Where(clause = "CMN_DELETE_FLAG=0")
@Getter @Setter
public class ShtMsgSms extends BaseEntity {

	@Id
	@Column(name = "SHT_MSG_SMS_ID")
	@SequenceGenerator(name = "seq", sequenceName = "SHT_MSG_SMS_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
	@Getter @Setter
	private Long id;

	@Column(name = "SHT_MSG_SMS_PHONE")
	@Getter @Setter
	private String phoneNumber;

	@Column(name = "SHT_MSG_SMS_CONTENT", length = 500)
	@Getter @Setter
	private String content;

	@Column(name = "SHT_MSG_SMS_STATUS")
	@Enumerated
	@Getter @Setter
	private SendStatus status;

	@Column(name = "SHT_MSG_SMS_TYPE")
	@Enumerated
	@Getter @Setter
	private SMSType type;

	public enum SendStatus {
		SENDING, SEND
	}

	public enum SMSType {
		REGISTRATION
	}
}
