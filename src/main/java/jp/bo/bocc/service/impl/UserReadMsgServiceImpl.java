package jp.bo.bocc.service.impl;

import jp.bo.bocc.entity.ShtUserReadMsg;
import jp.bo.bocc.repository.UserReadMsgRepository;
import jp.bo.bocc.service.UserReadMsgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author bachbx
 */
@Service
public class UserReadMsgServiceImpl implements UserReadMsgService {

	@Autowired
	private UserReadMsgRepository repository;

	@Override
	public Boolean checkUserReadMsg(Long userId, Long qaId) {
		Long count = repository.checkUserReadQa(userId, qaId);
		if(count > 0){
			return  true;
		}
		return false;
	}

	@Override
	public ShtUserReadMsg createUserReadMsg(ShtUserReadMsg readMsg) {
		return repository.save(readMsg);
	}

	@Override
	public int countNewMsgSystemPushAll(Long userId) {
		return repository.countNewMsgSystemPushAll(userId);
	}
}
