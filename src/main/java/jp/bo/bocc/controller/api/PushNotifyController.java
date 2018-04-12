package jp.bo.bocc.controller.api;

import jp.bo.bocc.controller.api.request.PostBodyRequest;
import jp.bo.bocc.controller.api.request.PushBodyRequest;
import jp.bo.bocc.entity.ShmAddr;
import jp.bo.bocc.entity.ShmAdmin;
import jp.bo.bocc.entity.ShtPushNotify;
import jp.bo.bocc.enums.OSTypeEnum;
import jp.bo.bocc.helper.PushUtils;
import jp.bo.bocc.push.PushMsgCommon;
import jp.bo.bocc.push.PushMsgDetail;
import jp.bo.bocc.push.SNSMobilePushService;
import jp.bo.bocc.service.AddressService;
import jp.bo.bocc.service.AdminService;
import jp.bo.bocc.service.PushNotifyService;
import jp.bo.bocc.system.apiconfig.interceptor.AccessTokenAuthentication;
import jp.bo.bocc.system.apiconfig.interceptor.NoAuthentication;
import jp.bo.bocc.system.apiconfig.resolver.JsonArg;
import jp.bo.bocc.system.exception.BadRequestException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by DonBach on 3/16/2017.
 */
@RestController
public class PushNotifyController {
    @Autowired
    PushNotifyService service;

    @Autowired
    AdminService adminService;

    @Autowired
    SNSMobilePushService snsMobilePushService;

    @PostMapping("/push-to-user")
    @NoAuthentication
    @ResponseBody
    public ResponseEntity<PushBodyRequest>  createPush(@RequestBody PushBodyRequest pushBodyRequest){
        final String msgCont = pushBodyRequest.getPushContent();
        if(StringUtils.isEmpty(pushBodyRequest.getPassword()) || !pushBodyRequest.getPassword().equals("gK5PVspE")){
            throw new BadRequestException("Invalid password");
        }
        final PushMsgCommon pushMsg = PushUtils.buildPushMsgCommon(msgCont);
        PushMsgDetail pushMsgDetail = PushUtils.buildPushMsgDetail(PushMsgDetail.NtfTypeEnum.ADMIN_PUSH_NOTIFY, null, null, null, null, null, null, null);
        if(pushBodyRequest.getPushAndroid() != null && pushBodyRequest.getPushAndroid()){
            snsMobilePushService.sendNotificationForAndroidUser(pushBodyRequest.getUserId(), pushMsg, pushMsgDetail);
        }
        if(pushBodyRequest.getPushIOs() != null && pushBodyRequest.getPushIOs()){
            snsMobilePushService.sendNotificationForIOsUser(pushBodyRequest.getUserId(), pushMsg, pushMsgDetail);
        }
        if((pushBodyRequest.getPushAndroid() == null || !pushBodyRequest.getPushAndroid())  && (pushBodyRequest.getPushIOs() == null || !pushBodyRequest.getPushIOs())){
            snsMobilePushService.sendNotificationForUser(pushBodyRequest.getUserId(), pushMsg, pushMsgDetail);
        }
        return new ResponseEntity<PushBodyRequest>(pushBodyRequest, HttpStatus.OK);
    }

    @PostMapping("/push-receive-notify")
    @AccessTokenAuthentication
    public ResponseEntity<ShtPushNotify> receivePush(@JsonArg Long pushId, @JsonArg("osType") OSTypeEnum osType){
        ShtPushNotify notify = service.getPush(pushId);
        if(notify != null){
            if(osType == OSTypeEnum.ANDROID){
                notify.setAndroidReadNumber(notify.getAndroidReadNumber() + 1);
            }else{
                notify.setIosReadNumber(notify.getIosReadNumber() + 1);
            }
            service.savePush(notify);
        }
        return new ResponseEntity<ShtPushNotify>(notify, HttpStatus.OK);
    }

}
