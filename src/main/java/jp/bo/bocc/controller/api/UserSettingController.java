package jp.bo.bocc.controller.api;

import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShtUserSetting;
import jp.bo.bocc.service.UserService;
import jp.bo.bocc.service.UserSettingService;
import jp.bo.bocc.system.apiconfig.bean.RequestContext;
import jp.bo.bocc.system.apiconfig.interceptor.AccessTokenAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by DonBach on 5/25/2017.
 */
@RestController
public class UserSettingController {
    @Autowired
    UserSettingService userSettingService;

    @Autowired
    RequestContext requestContext;

    @Autowired
    UserService userService;

    @RequestMapping(value = "/user/get-setting", method = RequestMethod.GET)
    @AccessTokenAuthentication
    public ShtUserSetting getUserSetting() {
        ShtUserSetting userSetting = null;
        ShmUser shmUser = requestContext.getUser();
        userSetting = userSettingService.getByUserId(shmUser.getId());
        if(userSetting == null){
            userSetting = new ShtUserSetting();
            userSetting.setReceiveEmail(true);
            userSetting.setReceivePush(true);
            userSetting.setPushFavorite(true);
            userSetting.setPushTalkRoomFirstMsg(true);
            userSetting.setReceivePush(true);
            userSetting.setMailTalkRoomFirstMsg(true);
            userSetting.setMailTalkRoomTransaction(true);
        }
        return userSetting;
    }

    @RequestMapping(value = "/user/setting",method = RequestMethod.PUT)
    @AccessTokenAuthentication
    public ShtUserSetting setting(@RequestBody ShtUserSetting userSetting){
        ShmUser shmUser = requestContext.getUser();
        ShtUserSetting result;
        ShtUserSetting currentUserSetting = userSettingService.getByUserId(shmUser.getId());
        if(currentUserSetting != null && currentUserSetting.getShmUser() != null){
            currentUserSetting.setReceiveEmail(userSetting.getReceiveEmail());
            currentUserSetting.setReceivePush(userSetting.getReceivePush());
            currentUserSetting.setPushTalkRoomFirstMsg(userSetting.getPushTalkRoomFirstMsg());
            currentUserSetting.setPushFavorite(userSetting.getPushFavorite());
            currentUserSetting.setPushTalkRoomTransaction(userSetting.getPushTalkRoomTransaction());
            currentUserSetting.setMailTalkRoomTransaction(userSetting.getMailTalkRoomTransaction());
            currentUserSetting.setMailTalkRoomFirstMsg(userSetting.getMailTalkRoomFirstMsg());
            result = userSettingService.saveUserSetting(currentUserSetting);
        }else {
            userSetting.setShmUser(shmUser);
            result = userSettingService.saveUserSetting(userSetting);
        }
        return result;
    }

}
