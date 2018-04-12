package jp.bo.bocc.controller.web;

import jp.bo.bocc.controller.BoccBaseWebController;
import jp.bo.bocc.controller.web.request.PushRequest;
import jp.bo.bocc.controller.web.validator.AdminNgValidator;
import jp.bo.bocc.controller.web.validator.PushNotifyValidator;
import jp.bo.bocc.entity.ShmAdmin;
import jp.bo.bocc.entity.ShtPushNotify;
import jp.bo.bocc.push.SNSMobilePushService;
import jp.bo.bocc.service.AdminService;
import jp.bo.bocc.service.PushNotifyService;
import jp.bo.bocc.service.UserNtfService;
import jp.bo.bocc.system.exception.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Created by buixu on 9/14/2017.
 */
@Controller
public class PushNotifyWebController extends BoccBaseWebController {

    private final static Logger LOGGER = Logger.getLogger(PushNotifyWebController.class.getName());

    @Value("${page.size}")
    private int maxRecordsInPage;

    @Autowired
    PushNotifyService pushNotifyService;

    @Autowired
    AdminNgValidator adminNgValidator;

    @Autowired
    AdminService adminService;

    @Autowired
    UserNtfService userNtfService;

    @Autowired
    SNSMobilePushService snsMobilePushService;

    public static int NOTIFY_PUSH_PAGE_SIZE = 20;

    @RequestMapping(value = "/list-push", method = RequestMethod.GET)
    public String listPushNotify(@RequestParam(defaultValue="0") Integer pageNumber, @RequestParam(defaultValue="DESC") String sortType, Model model) {
        adminService.getAdminForOnlySuperAdmin(getEmail());
        if (pageNumber == null)
            pageNumber = 0;
        if(sortType == null){
            sortType = "DESC";
        }
        Pageable pageItem = new PageRequest(pageNumber, NOTIFY_PUSH_PAGE_SIZE);
        Page<ShtPushNotify> page = pushNotifyService.getPushNotifys(pageItem, sortType);
        List<ShtPushNotify> listPush = page.getContent();
        if(listPush.size()==0) {
            String nullResult = "データはありません !";
            model.addAttribute("nullResult", nullResult);
        }
        model.addAttribute("sortType",sortType);
        model.addAttribute("sizeResult", listPush.size());
        model.addAttribute("listPush", listPush);
        int current = page.getNumber();
        model.addAttribute("deploymentLog", page);
        model.addAttribute("currentIndex", current);
        model.addAttribute("totalPage", page.getTotalPages());
        model.addAttribute("totalElements", page.getTotalElements());
        model.addAttribute("curPage", current);
        model.addAttribute("startElement", ((pageNumber) * NOTIFY_PUSH_PAGE_SIZE) + 1);
        model.addAttribute("curElements", ((pageNumber) * NOTIFY_PUSH_PAGE_SIZE) + listPush.size());
        return "list-push";
    }

    @RequestMapping(value = "/push/suspend", method = RequestMethod.PUT)
    @ResponseBody
    public String suspendPush(Model model, @RequestBody PushRequest pushRequest) throws UnsupportedEncodingException {
        ShmAdmin admin = adminService.getAdminForOnlySuperAdmin(getEmail());
        if (admin == null)
            throw new ServiceException(HttpStatus.NOT_FOUND, "Can not found Admin");
        if(pushRequest.getPushId() == null){
            return "list-push?pageNumber=0";
        }
        ShtPushNotify pushNotify = pushNotifyService.getPush(pushRequest.getPushId());
        if(pushNotify.getPushStatus() == ShtPushNotify.PushStatus.SUSPENDED || pushNotify.getPushStatus() == ShtPushNotify.PushStatus.SENT){
            return "list-push?pageNumber=0";
        }

        // delete all job of pushNotify records created before
        pushNotifyService.suspendPush(pushNotify.getPushId());

        pushNotify.setPushStatus(ShtPushNotify.PushStatus.SUSPENDED);
        pushNotify.setAdminSender(admin);
        pushNotifyService.savePush(pushNotify);
        return "list-push?pageNumber=0";
    }

    @RequestMapping(value = "/push/detele-push", method = RequestMethod.PUT)
    @ResponseBody
    public String deletePush(Model model, @RequestBody PushRequest pushRequest) throws UnsupportedEncodingException {
        ShmAdmin admin = adminService.getAdminForOnlySuperAdmin(getEmail());
        if (admin == null)
            throw new ServiceException(HttpStatus.NOT_FOUND, "Can not found Admin");
        if(pushRequest.getPushId() == null){
            return "list-push?pageNumber=0";
        }
        ShtPushNotify pushNotify = pushNotifyService.getPush(pushRequest.getPushId());
        if(pushNotify.getDeleteFlag()){
            return "list-push?pageNumber=0";
        }
        pushNotifyService.deletePush(pushNotify.getPushId());
        return "list-push?pageNumber=0";
    }

    @RequestMapping(value = "/create-push")
    public String createPush(Model model) {
        adminService.getAdminForOnlySuperAdmin(getEmail());
        Long iosNumber = userNtfService.countIOSDeviceActive();
        Long androidNumber = userNtfService.countAndroidDeviceActive();
        ShtPushNotify newPush = new ShtPushNotify();
        newPush.setIosNumber(iosNumber);
        newPush.setAndroidNumber(androidNumber);
        model.addAttribute("pushNotify", newPush);
        return "create-push";
    }

    @RequestMapping(value = "/add-push", method = RequestMethod.POST)
    public String addPush(@ModelAttribute(value = "pushNotify") PushRequest pushNotify, Model model, BindingResult bindingResult) {
        LOGGER.info("BEGIN: ======================= PushNotifyWebController.addPush" );
        ShmAdmin shmAdmin = adminService.getAdminForOnlySuperAdmin(getEmail());

        if (StringUtils.isEmpty(pushNotify.getPushTitle())) {
            model.addAttribute("msgPushTitle", getMessage("SH_E100155"));
            return "create-push";
        }
        if (StringUtils.isEmpty(pushNotify.getPushContent())) {
            model.addAttribute("msgPushContent", getMessage("SH_E100156"));
            return "create-push";
        }
        if(pushNotify.getPushActionType() != null && pushNotify.getPushActionType().equals("JUST_MESSAGE")){
            pushNotify.setPushAndroid(true);
            pushNotify.setPushIos(true);
        }
        if(!pushNotify.getPushAndroid() && !pushNotify.getPushIos()){
            model.addAttribute("msgPushType", getMessage("SH_E100158"));
            return "create-push";
        }
        if(pushNotify.getPushImmediate() != null && pushNotify.getPushImmediate().equals("WAIT")
                && StringUtils.isEmpty(pushNotify.getTimerDateStr())){
            model.addAttribute("msgTimerDate", getMessage("SH_E100157"));
            return "create-push";
        }

        pushNotifyService.addPush(pushNotify, shmAdmin);
        LOGGER.info("END: ======================= PushNotifyWebController.addPush. Redirect list-push" );
        return "redirect:list-push?pageNumber=0";
    }

    @RequestMapping(value = "/validate-push", method = RequestMethod.POST)
    public @ResponseBody PushNotifyValidator validateBanner(Model model, @RequestBody PushRequest request) {
        adminService.getAdminForOnlySuperAdmin(getEmail());
        return validatePush(request);
    }

    private PushNotifyValidator validatePush(PushRequest request){
        PushNotifyValidator validator = new PushNotifyValidator();
        if(StringUtils.isNotEmpty(request.getTimerDateStr())){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
            LocalDateTime pushTime = LocalDateTime.parse(request.getTimerDateStr(), formatter);
            if(pushTime.isBefore(LocalDateTime.now())){
                validator.setError(1);
                validator.setErrorMsg("配信日時を現在時点の前に設定することはできません。");
                return validator;
            }
        }
        return validator;
    }

    @RequestMapping(value = "/edit-push")
    public String editPush(Model model, @RequestParam Long pushId) {
        adminService.getAdminForOnlySuperAdmin(getEmail());
        ShtPushNotify currentPush = pushNotifyService.getPush(pushId);
        if(currentPush == null){
            return "list-push?pageNumber=0";
        }

        PushRequest pushNotify = pushNotifyService.editPush(currentPush);
        model.addAttribute("pushNotify", pushNotify);

        return "edit-push";
    }

    @RequestMapping(value = "/update-push", method = RequestMethod.POST)
    public String updatePush(@ModelAttribute(value = "pushNotify") PushRequest pushNotify, Model model, BindingResult bindingResult) {

        if (StringUtils.isEmpty(pushNotify.getPushTitle())) {
            model.addAttribute("msgPushTitle", getMessage("SH_E100155"));
            return "edit-push";
        }
        if (StringUtils.isEmpty(pushNotify.getPushContent())) {
            model.addAttribute("msgPushContent", getMessage("SH_E100156"));
            return "edit-push";
        }
        if(pushNotify.getPushActionType() != null && pushNotify.getPushActionType().equals("JUST_MESSAGE")){
            pushNotify.setPushAndroid(true);
            pushNotify.setPushIos(true);
        }
        if(!pushNotify.getPushAndroid() && !pushNotify.getPushIos()){
            model.addAttribute("msgPushType", getMessage("SH_E100158"));
            return "edit-push";
        }
        if(pushNotify.getPushImmediate() != null && pushNotify.getPushImmediate().equals("WAIT")
                && StringUtils.isEmpty(pushNotify.getTimerDateStr())){
            model.addAttribute("msgTimerDate", getMessage("SH_E100157"));
            return "edit-push";
        }

        ShtPushNotify currentPush = pushNotifyService.getPush(pushNotify.getPushId());
        if(currentPush == null || currentPush.getPushStatus() != ShtPushNotify.PushStatus.WAITING){
            return "list-push";
        }

        pushNotifyService.updatePush(currentPush, pushNotify, getEmail());

        return "redirect:list-push?pageNumber=0";
    }

    @RequestMapping(value = "/replicate-push")
    public String replicatePush(Model model, @RequestParam Long pushId) {
        adminService.getAdminForOnlySuperAdmin(getEmail());
        ShtPushNotify currentPush = pushNotifyService.getPush(pushId);
        if(currentPush == null){
            return "list-push?pageNumber=0";
        }
        PushRequest pushNotify = new PushRequest();
        pushNotify.setPushId(currentPush.getPushId());
        pushNotify.setPushTitle(currentPush.getPushTitle());
        pushNotify.setPushContent(currentPush.getPushContent());
        pushNotify.setPushIos(currentPush.getPushIos());
        pushNotify.setPushAndroid(currentPush.getPushAndroid());
        pushNotify.setPushStatus(currentPush.getPushStatus());
        if(currentPush.getTimerDate() != null){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm");
            String timerDateStr = currentPush.getTimerDate().format(formatter);
            pushNotify.setTimerDateStr(timerDateStr);
        }
        if(currentPush.getPushActionType() == ShtPushNotify.PushActionType.JUST_PUSH){
            pushNotify.setPushActionType("JUST_PUSH");
        }
        if(currentPush.getPushActionType() == ShtPushNotify.PushActionType.JUST_MESSAGE){
            pushNotify.setPushActionType("JUST_MESSAGE");
        }
        if(currentPush.getPushActionType() == ShtPushNotify.PushActionType.PUSH_AND_MESSAGE){
            pushNotify.setPushActionType("PUSH_AND_MESSAGE");
        }
        if(currentPush.getPushImmediate()){
            pushNotify.setPushImmediate("IMMEDIATE");
        }else{
            pushNotify.setPushImmediate("WAIT");
        }
        pushNotify.setAndroidNumber(currentPush.getAndroidNumber());
        pushNotify.setIosNumber(currentPush.getIosNumber());

        Long iosNumber = userNtfService.countIOSDeviceActive();
        Long androidNumber = userNtfService.countAndroidDeviceActive();
        pushNotify.setCurrentAndroidNumber(androidNumber);
        pushNotify.setCurrentIosNumber(iosNumber);

        model.addAttribute("pushNotify", pushNotify);
        return "replicate-push";
    }

    @RequestMapping(value = "/add-replicate-push", method = RequestMethod.POST)
    public String addReplicatePush(@ModelAttribute(value = "pushNotify") PushRequest pushNotify, Model model, BindingResult bindingResult) {
        ShmAdmin shmAdmin = adminService.getAdminForOnlySuperAdmin(getEmail());

        if (StringUtils.isEmpty(pushNotify.getPushTitle())) {
            model.addAttribute("msgPushTitle", getMessage("SH_E100155"));
            return "replicate-push";
        }
        if (StringUtils.isEmpty(pushNotify.getPushContent())) {
            model.addAttribute("msgPushContent", getMessage("SH_E100156"));
            return "replicate-push";
        }
        if(pushNotify.getPushActionType() != null && pushNotify.getPushActionType().equals("JUST_MESSAGE")){
            pushNotify.setPushAndroid(true);
            pushNotify.setPushIos(true);
        }
        if(!pushNotify.getPushAndroid() && !pushNotify.getPushIos()){
            model.addAttribute("msgPushType", getMessage("SH_E100158"));
            return "replicate-push";
        }
        if(pushNotify.getPushImmediate() != null && pushNotify.getPushImmediate().equals("WAIT")
                && StringUtils.isEmpty(pushNotify.getTimerDateStr())){
            model.addAttribute("msgTimerDate", getMessage("SH_E100157"));
            return "replicate-push";
        }
        pushNotifyService.addReplicatePush(pushNotify, getEmail());

        return "redirect:list-push?pageNumber=0";
    }

}
