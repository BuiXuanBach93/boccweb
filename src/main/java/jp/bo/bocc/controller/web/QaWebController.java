package jp.bo.bocc.controller.web;

import jp.bo.bocc.controller.BoccBaseWebController;
import jp.bo.bocc.controller.web.request.QaMemoRequest;
import jp.bo.bocc.controller.web.request.QaSearchRequest;
import jp.bo.bocc.controller.web.request.TalkQaRequest;
import jp.bo.bocc.entity.*;
import jp.bo.bocc.helper.PushUtils;
import jp.bo.bocc.push.PushMsgCommon;
import jp.bo.bocc.push.PushMsgDetail;
import jp.bo.bocc.push.SNSMobilePushService;
import jp.bo.bocc.service.*;
import jp.bo.bocc.system.exception.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by ThaiVD on 4/8/2017.
 */

@Controller
public class QaWebController extends BoccBaseWebController{

    @Value("${page.size}")
    private int maxRecordsInPage;

    @Autowired
    QaService qaService;

    @Autowired
    TalkQaService talkQaService;

    @Autowired
    AdminService adminService;

    @Autowired
    private SNSMobilePushService snsMobilePushService;

    @Autowired
    MemoQaService memoQaService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    UserSettingService userSettingService;

    @RequestMapping (value = "/list-qa", method = RequestMethod.GET)
    public String listQa (@RequestParam(defaultValue="1") Integer page, Model model) throws UnsupportedEncodingException {
        adminService.getAdminForSuperAdminAndAdminAndCusSupport(getEmail());
        Page<ShtQa> pageQa = qaService.getListQa(page);
        List<ShtQa> result = pageQa.getContent();
        model.addAttribute("totalPage", pageQa.getTotalPages() - 1);
        model.addAttribute("totalElements", pageQa.getTotalElements());
        model.addAttribute("curPage", page);
        model.addAttribute("startElement", (page * maxRecordsInPage) + 1);
        model.addAttribute("curElements", (page * maxRecordsInPage) + result.size());
        model.addAttribute("listQa", result);
        QaSearchRequest qaSearchRequest = new QaSearchRequest();
        qaSearchRequest.setIsNoResponse(true);
        qaSearchRequest.setIsInProgress(true);
        model.addAttribute("qaSearch", qaSearchRequest);
        if(result != null && result.size() > 0){
            model.addAttribute("user", result.get(0).getShmUser());
        }
        return "list-qa";
    }

    @RequestMapping ("/detail-qa")
    public String detailQa (Model model, @RequestParam("qaId") Long qaId) throws UnsupportedEncodingException {
        adminService.getAdminForSuperAdminAndAdminAndCusSupport(getEmail());
        ShtQa qa = qaService.getQaById(qaId);
        List<ShtTalkQa> talkQas = talkQaService.getListMsgByQaId(qaId);

        model.addAttribute("talkQas",talkQas);
        model.addAttribute("qa", qa);
        return "detail-qa";
    }

    @RequestMapping(value = "/web/qa/create-msg", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<ShtTalkQa> responseQa(Model model, @RequestBody TalkQaRequest request) throws UnsupportedEncodingException {
        ShmAdmin admin = adminService.getAdminForSuperAdminAndAdminAndCusSupport(getEmail());
        if (admin == null)
            throw new ServiceException(HttpStatus.NOT_FOUND, "Can not found Admin");

        ShtTalkQa talkQa = talkQaService.saveTalkQa(request, admin);
        ShtQa qa = qaService.getQaById(request.getQaId());
        if(qa != null && qa.getQaStatus() == ShtQa.QaStatusEnum.NO_RESPONSE){
            qa.setQaStatus(ShtQa.QaStatusEnum.INPROGRESS);
            qaService.saveQa(qa);
        }

        //send mail to user
        talkQaService.sendMailToUserAfterResponse(talkQa);

        // push notify
        final Long userIdReceivePush = qa.getShmUser().getId();
//        final boolean checkReceivePushOn = userSettingService.checkReceivePushOn(userIdReceivePush);
//        if (checkReceivePushOn) {
            final String msgCont = getMessage("PUSH_ANSWER_QA");
            final PushMsgCommon pushMsg = PushUtils.buildPushMsgCommon(msgCont);
            PushMsgDetail pushMsgDetail = PushUtils.buildPushMsgDetail(PushMsgDetail.NtfTypeEnum.TR_ADM_ANSWER_QA, null, null, null, null, qa.getQaId(), null, userIdReceivePush);
            snsMobilePushService.sendNotificationForUserFromAdmin(userIdReceivePush, pushMsg, pushMsgDetail);
//        }
        return new ResponseEntity<ShtTalkQa>(talkQa, HttpStatus.OK);
    }

    @RequestMapping(value = "/web/qa/resolved-qa", method = RequestMethod.PUT)
    @ResponseBody
    public String resolvedQa(Model model, @RequestBody TalkQaRequest request) throws UnsupportedEncodingException {
        ShmAdmin admin = adminService.getAdminForSuperAdminAndAdminAndCusSupport(getEmail());
        if (admin == null)
            throw new ServiceException(HttpStatus.NOT_FOUND, "Can not found Admin");

        ShtQa qa = qaService.getQaById(request.getQaId());
        qa.setQaStatus(ShtQa.QaStatusEnum.RESOLVED);
        qaService.saveQa(qa);

        //send mail to user
        qaService.sendMailToUserAfterCompletedQa(qa);

        // push notify
        final ShmUser shmUser = qa.getShmUser();
        if (shmUser != null) {
            String msgContent = getMessage("PUSH_RESOLVED_QA");
            PushMsgCommon pushMsg = PushUtils.buildPushMsgCommon(msgContent);
            PushMsgDetail pushMsgDetail = PushUtils.buildPushMsgDetail(PushMsgDetail.NtfTypeEnum.TR_ADM_DONE_QA, null, null, null, null, qa.getQaId(), null,shmUser.getId());
            snsMobilePushService.sendNotificationForUser(shmUser.getId(), pushMsg, pushMsgDetail);
        }

        return "list-qa";
    }

    @RequestMapping(value = "/search-qa", method = RequestMethod.GET)
    public String searchQa(@ModelAttribute(value = "qaSearch") QaSearchRequest qaSearchRequest,
                           @RequestParam(defaultValue = "0") Integer page, Model model
                           ) throws UnsupportedEncodingException {
        adminService.getAdminForSuperAdminAndAdminAndCusSupport(getEmail());
        Page<ShtQa> pageQa = qaService.searchListQa(qaSearchRequest, page);
        List<ShtQa> qas = pageQa.getContent();
        model.addAttribute("listQa", qas);
        model.addAttribute("totalPage", pageQa.getTotalPages() - 1);
        model.addAttribute("totalElements", pageQa.getTotalElements());
        model.addAttribute("curPage", page);
        model.addAttribute("startElement", (page * maxRecordsInPage) + 1);
        model.addAttribute("curElements", (page * maxRecordsInPage) + qas.size());

        return "list-qa";
    }

    @RequestMapping(value = "/web/memo-qa", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<List<ShtMemoQa>> getCategories(@RequestParam(value = "qaId") Long qaId) {
        adminService.getAdminForSuperAdminAndAdminAndCusSupport(getEmail());
        List<ShtMemoQa> memoQaList = memoQaService.getMemoQaListByQAId(qaId);
        return new ResponseEntity<>(memoQaList, HttpStatus.OK);
    }

    @RequestMapping(value = "/web/save-qa-memo", method = RequestMethod.POST)
    @ResponseBody
    public ShtMemoQa saveUserMemo(@RequestBody QaMemoRequest qaMemoRequest) {
        ShmAdmin admin = adminService.getAdminForSuperAdminAndAdminAndCusSupport(getEmail());
        if (admin == null)
            throw new ServiceException(HttpStatus.NOT_FOUND, "Can not found Admin");
        return memoQaService.save(qaMemoRequest.getQaId(), admin, qaMemoRequest.getContent());
    }
}
