package jp.bo.bocc.service;

import jp.bo.bocc.controller.web.request.PushRequest;
import jp.bo.bocc.entity.ShmAdmin;
import jp.bo.bocc.entity.ShtPushNotify;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * @author DonBach
 */
public interface PushNotifyService {

	ShtPushNotify savePush(ShtPushNotify pushNotify);

	ShtPushNotify getPush(Long pushId);

	Page<ShtPushNotify> getPushNotifys(Pageable pageRequest, String sortType);

	void deletePush(Long pushId);

	void pushImmediate(ShtPushNotify pushNotify, ShmAdmin shmAdmin);

	void pushSetTimer(ShtPushNotify pushNotify, ShmAdmin shmAdmin);

	void handleJobPushNotifyOnTimer(Long pushId, Long adminId);

    void addReplicatePush(PushRequest pushNotify, String adminEmail);

	void updatePush(ShtPushNotify currentPush,PushRequest pushNotify, String adminEmail);

	void addPush(PushRequest pushNotify, ShmAdmin shmAdmin);

	PushRequest editPush(ShtPushNotify currentPush);

	void suspendPush(Long pushId);
}
