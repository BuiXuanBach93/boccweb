package jp.bo.bocc.controller.api;

import jp.bo.bocc.controller.BoccBaseController;
import jp.bo.bocc.controller.api.request.TalkQaRequest;
import jp.bo.bocc.controller.api.response.QaDetailResponse;
import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShtQa;
import jp.bo.bocc.entity.ShtTalkQa;
import jp.bo.bocc.helper.DateUtils;
import jp.bo.bocc.service.*;
import jp.bo.bocc.system.apiconfig.bean.RequestContext;
import jp.bo.bocc.system.apiconfig.interceptor.AccessTokenAuthentication;
import jp.bo.bocc.system.exception.ServiceException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by DonBach on 4/5/2017.
 */
@RestController
public class QaController extends BoccBaseController{

    @Autowired
    RequestContext requestContext;
    @Autowired
    QaService qaService;
    @Autowired
    UserService userService;
    @Autowired
    MailService mailService;
    @Autowired
    AdminNgService adminNgService;
    @Autowired
    TalkQaService talkQaService;

    @RequestMapping(value = "/qas/{id}", method = RequestMethod.GET)
    @AccessTokenAuthentication
    public ShtQa getQa(@PathVariable long id) {
        return qaService.getQa(id);
    }

    @RequestMapping(value = "/qas", method = RequestMethod.POST)
    @AccessTokenAuthentication
    public ShtQa createQa(@RequestBody ShtQa shtQa) {

        // validate input
        validateShtQa(shtQa);

        ShmUser shmUser = requestContext.getUser();

        ShtQa qaSaved = qaService.createQa(shtQa,shmUser);

        // send mail to user
        String qaTypeStr = getQaType(shtQa);
        mailService.sendEmailSentQaToAdmin(shmUser.getEmail(),shmUser.getNickName(),qaTypeStr,shtQa.getFirstQaMsg());

        return qaSaved;
    }

    private String getQaType(ShtQa shtQa){
        String qaTypeStr = "";
        if(shtQa.getQaType() == ShtQa.QaTypeEnum.ACCOUNT_PROBLEM){
            qaTypeStr = getMessage("QA_TYPE_01");
        }
        if(shtQa.getQaType() == ShtQa.QaTypeEnum.POST_PROBLEM){
            qaTypeStr = getMessage("QA_TYPE_02");
        }
        if(shtQa.getQaType() == ShtQa.QaTypeEnum.USAGE_PROBLEM){
            qaTypeStr =getMessage("QA_TYPE_03");
        }
        if(shtQa.getQaType() == ShtQa.QaTypeEnum.HELP){
            qaTypeStr = getMessage("QA_TYPE_04");
        }
        if(shtQa.getQaType() == ShtQa.QaTypeEnum.LEAVING){
            qaTypeStr = getMessage("QA_TYPE_05");
        }
        return qaTypeStr;
    }

    @RequestMapping(value = "/qas/get-owner-qas", method = RequestMethod.GET)
    @AccessTokenAuthentication
    public Page<ShtQa> getOwnerQas(@RequestParam(value = "page", required = true) int page, @RequestParam(value = "size", required = true) int size){
        ShmUser shmUser = requestContext.getUser();
        Page<ShtQa> qas = qaService.getListQaByUserId(shmUser.getId(), page, size);
        return  qas;
    }

    private void validateShtQa(ShtQa shtQa){
        if(shtQa.getFirstQaMsg() == null){
            throw new ServiceException(HttpStatus.BAD_REQUEST, getMessage("SH_E100033"));
        }
        // check NG Words
        List<String> listNGWord = adminNgService.checkNGContent(shtQa.getFirstQaMsg());
        String nGWordStr = "";
        for (String ngW: listNGWord) {
            nGWordStr += ngW +",";
        }
        if (!StringUtils.isEmpty(nGWordStr)) {
            nGWordStr = nGWordStr.substring(0, nGWordStr.length() - 1);
        }
        if(!StringUtils.isEmpty(nGWordStr)){
            throw new ServiceException(HttpStatus.BAD_REQUEST, getMessage("SH_E100042", nGWordStr));
        }
        if(shtQa.getFirstQaMsg().length() > 300){
            throw new ServiceException(HttpStatus.BAD_REQUEST, getMessage("SH_E100034"));
        }
    }


    @RequestMapping(value = "/qas/create-msg", method = RequestMethod.POST)
    @AccessTokenAuthentication
    public ShtTalkQa createTalkQa(@RequestBody TalkQaRequest talkQaRequest) {
        // validate msg
        validateTalkQaRequest(talkQaRequest);
        ShtQa shtQa = qaService.getQaById(talkQaRequest.getQaId());
        if(shtQa == null){
            throw new ServiceException(HttpStatus.BAD_REQUEST, getMessage("SH_E100140"));
        }
        if(shtQa.getQaStatus() == ShtQa.QaStatusEnum.RESOLVED){
            throw new ServiceException(HttpStatus.BAD_REQUEST, getMessage("SH_E100141"));
        }
        ShtTalkQa talkQa = new ShtTalkQa();
        talkQa.setShtQa(shtQa);
        talkQa.setTalkQaMsg(talkQaRequest.getMsg());
        talkQa.setFromAdmin(false);
        return talkQaService.save(talkQa);
    }

    private void validateTalkQaRequest(TalkQaRequest talkQaRequest){
        if(talkQaRequest.getQaId() == null){
            throw new ServiceException(HttpStatus.BAD_REQUEST, getMessage("SH_E100140"));
        }
        // check NG Words
        List<String> listNGWord = adminNgService.checkNGContent(talkQaRequest.getMsg());
        String nGWordStr = "";
        for (String ngW: listNGWord) {
            nGWordStr += ngW +",";
        }
        if (!StringUtils.isEmpty(nGWordStr)) {
            nGWordStr = nGWordStr.substring(0, nGWordStr.length() - 1);
        }
        if(!StringUtils.isEmpty(nGWordStr)){
            throw new ServiceException(HttpStatus.BAD_REQUEST, getMessage("SH_E100042", nGWordStr));
        }
    }

    @GetMapping(value = "/qas/detail/{qaId}")
    @AccessTokenAuthentication
    public QaDetailResponse getQaDetail(@PathVariable(value = "qaId") Long qaId,
                                        @RequestParam(value = "msgTime", required = false) String msgTime,
                                        @RequestParam("isBefore") Boolean isBefore, @RequestParam("size") int size) throws Exception {
        ShmUser shmUser = requestContext.getUser();
        QaDetailResponse response = new QaDetailResponse();
        ShtQa shtQa = qaService.getQaById(qaId);
        shtQa.setFirstQaMsg(qaService.getFirstMsg(qaId));
        shtQa.setCreatedAtTxt(DateUtils.convertLocalDateTimeToStringWithoutTime(shtQa.getCreatedAt()));
        List<ShtTalkQa> listTalkQas = qaService.getListTalkQaByCondition(shmUser, msgTime,isBefore,qaId,size);
        //remove first msg from list msg
        int count = 0;
        int firstMsgIndex = -1;
        if(shtQa.getQaContentType() == ShtQa.QaContentTypeEnum.QA && listTalkQas != null){
            Long firstMsgId = qaService.getFirstMsgId(qaId);
            for (ShtTalkQa talkQa: listTalkQas) {
                if(talkQa.getTalkQaId().intValue() == firstMsgId){
                    firstMsgIndex = count;
                    break;
                }
                count++;
            }
            if(firstMsgIndex >= 0){
                listTalkQas.remove(firstMsgIndex);
            }
        }
        response.setCurrentDateTime(LocalDateTime.now());
        response.setQa(shtQa);
        response.setListTalkQas(listTalkQas);
        return response;
    }

    @GetMapping(value = "/admin/push-all/detail/{adminId}")
    @AccessTokenAuthentication
    public QaDetailResponse getQaDetailFromAdminPush(@PathVariable(value = "adminId") Long adminId,
                                        @RequestParam(value = "postType", defaultValue = "SYSTEM_PUSH_ALL")String pushTypeEnums,
                                        @RequestParam(value = "msgTime", required = false) String msgTime,
                                        @RequestParam("isBefore") Boolean isBefore, @RequestParam("size") int size) throws Exception {
        Long userId = getUserIdUsingApp();
        ShmUser shmUser = requestContext.getUser();
        QaDetailResponse response = new QaDetailResponse();
        ShtQa shtQa = qaService.getQaByAdminIdAndUserIdAndPushType(adminId, userId, ShtQa.QaContentTypeEnum.valueOf(pushTypeEnums));
        shtQa.setFirstQaMsg(qaService.getFirstMsg(shtQa.getQaId()));
        shtQa.setCreatedAtTxt(DateUtils.convertLocalDateTimeToStringWithoutTime(shtQa.getCreatedAt()));
        List<ShtTalkQa> listTalkQas = qaService.getListTalkQaByCondition(shmUser, msgTime,isBefore,shtQa.getQaId(),size);
        //remove first msg from list msg
        int count = 0;
        int firstMsgIndex = -1;
        if(shtQa.getQaContentType() == ShtQa.QaContentTypeEnum.QA && listTalkQas != null){
            Long firstMsgId = qaService.getFirstMsgId(shtQa.getQaId());
            for (ShtTalkQa talkQa: listTalkQas) {
                if(talkQa.getTalkQaId().intValue() == firstMsgId){
                    firstMsgIndex = count;
                    break;
                }
                count++;
            }
            if(firstMsgIndex >= 0){
                listTalkQas.remove(firstMsgIndex);
            }
        }
        response.setCurrentDateTime(LocalDateTime.now());
        response.setQa(shtQa);
        response.setListTalkQas(listTalkQas);
        return response;
    }

}
