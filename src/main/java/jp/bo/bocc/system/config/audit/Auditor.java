package jp.bo.bocc.system.config.audit;

import lombok.Getter;
import lombok.Setter;

/**
 * Contains information about who's performing the action
 * @author manhnt
 */
@Getter @Setter
public class Auditor {

	private Long userId;

	private UserType userType = UserType.NORMAL_USER; //Default

	public enum UserType {
		NORMAL_USER, SUPER_USER
	}
}
