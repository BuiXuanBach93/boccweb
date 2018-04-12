package jp.bo.bocc.service;

import jp.bo.bocc.entity.ShmAddr;
import jp.bo.bocc.entity.ShtUserReadMsg;

import java.util.List;

/**
 * @author manhnt
 */
public interface UserReadMsgService {

	Boolean checkUserReadMsg(Long userId, Long qaId);

	ShtUserReadMsg createUserReadMsg(ShtUserReadMsg readMsg);

	int countNewMsgSystemPushAll(Long userId);
}
