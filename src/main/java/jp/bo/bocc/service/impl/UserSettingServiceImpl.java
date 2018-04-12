package jp.bo.bocc.service.impl;

import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShtUserSetting;
import jp.bo.bocc.repository.UserSettingRepository;
import jp.bo.bocc.service.UserSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by DonBach on 5/25/2017.
 */
@Service("userSettingService")
@Transactional
public class UserSettingServiceImpl implements UserSettingService {

    @Autowired
    UserSettingRepository userSettingRepository;

    @Override
    public ShtUserSetting getUserSetting(Long id) {
        return userSettingRepository.findOne(id);
    }

    @Override
    public ShtUserSetting saveUserSetting(ShtUserSetting userSetting) {
        return userSettingRepository.save(userSetting);
    }

    @Override
    public ShtUserSetting getByUserId(Long userId) {
        ShtUserSetting userSetting = null;
        List<ShtUserSetting> userSettings = userSettingRepository.getByUserId(userId);
        if(userSettings != null && userSettings.size() > 0){
            userSetting = userSettings.get(0);
        }
        return userSetting;
    }

    @Override
    public void createUserSetting(ShmUser shmUser) {
        ShtUserSetting currentUserSetting = getByUserId(shmUser.getId());
        if(currentUserSetting != null && currentUserSetting.getShmUser() != null){
            return;
        }
        ShtUserSetting userSetting = new ShtUserSetting();
        userSetting.setReceiveEmail(true);
        userSetting.setReceivePush(true);
        userSetting.setMailTalkRoomFirstMsg(true);
        userSetting.setMailTalkRoomTransaction(true);
        userSetting.setPushTalkRoomTransaction(true);
        userSetting.setPushFavorite(true);
        userSetting.setPushTalkRoomFirstMsg(true);
        userSetting.setShmUser(shmUser);
        saveUserSetting(userSetting);
    }

    @Override
    public boolean checkReceiveMailOn(Long userId) {
        final Boolean result = userSettingRepository.getReceiveEmailByUserId(userId);
        if (result == null || result == true) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkReceivePushOn(Long userId) {
        final Boolean result = userSettingRepository.getReceivePushByUserId(userId);
        if (result == null || result == true) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkReceivingMailInTransaction(Long userId) {
        final Boolean result = userSettingRepository.checkReceivingMailInTransaction(userId);
        if (result == null || result == true) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkReceivePushTalkRoomFirstMsg(Long userId) {
        final Boolean result = userSettingRepository.checkReceivePushTalkRoomFirstMsg(userId);
        if (result == null || result == true) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkReceivepushFavorite(Long userId) {
        final Boolean result = userSettingRepository.checkReceivepushFavorite(userId);
        if (result == null || result == true) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkReceivePushTalkRoomTransaction(Long userId) {
        final Boolean result = userSettingRepository.checkReceivePushTalkRoomTransaction(userId);
        if (result == null || result == true) {
            return true;
        }
        return false;
    }

    @Override
    public boolean checkReceivingMailInTalk(Long userId) {
        final Boolean result = userSettingRepository.checkReceivingMailInTalk(userId);
        if (result == null || result == true) {
            return true;
        }
        return false;
    }
}
