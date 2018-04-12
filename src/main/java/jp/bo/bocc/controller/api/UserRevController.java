package jp.bo.bocc.controller.api;

import jp.bo.bocc.controller.BoccBaseController;
import jp.bo.bocc.controller.api.request.UserRevBodyRequest;
import jp.bo.bocc.controller.api.response.UserReviewCount;
import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShtUserRev;
import jp.bo.bocc.helper.PushUtils;
import jp.bo.bocc.push.PushMsgCommon;
import jp.bo.bocc.push.PushMsgDetail;
import jp.bo.bocc.push.SNSMobilePushService;
import jp.bo.bocc.service.*;
import jp.bo.bocc.system.apiconfig.bean.RequestContext;
import jp.bo.bocc.system.apiconfig.interceptor.AccessTokenAuthentication;
import jp.bo.bocc.system.apiconfig.interceptor.NoAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by DonBach on 4/4/2017.
 */
@RestController
public class UserRevController extends BoccBaseController {

    @Autowired
    private UserRevService userRevService;

    @Autowired
    MessageSource messageSource;

    @Autowired
    private TalkPurcService talkPurcService;

    @Autowired
    UserService userService;

    @Autowired
    RequestContext requestContext;

    @Autowired
    MailService mailService;
    @Autowired
    private SNSMobilePushService snsMobilePushService;
    @Autowired
    private UserSettingService userSettingService;

    @RequestMapping(value = "/user/reviews/{id}", method = RequestMethod.GET)
    @AccessTokenAuthentication
    public ShtUserRev getUserRev(@PathVariable long id) {
        return userRevService.getUserRevById(id);
    }

    @RequestMapping(value = "/user/reviews-count-review", method = RequestMethod.GET)
    @AccessTokenAuthentication
    public UserReviewCount countReviewByUserId(@RequestParam long userId) {
        UserReviewCount reviewCount = new UserReviewCount();
        Long goodNumber = userRevService.countReviewByUserId(userId, ShtUserRev.UserReviewRate.GOOD);
        Long fairNumber = userRevService.countReviewByUserId(userId, ShtUserRev.UserReviewRate.FAIR);
        Long badNumber = userRevService.countReviewByUserId(userId, ShtUserRev.UserReviewRate.BAD);
        reviewCount.setGoodTypeNumber(goodNumber);
        reviewCount.setFairTypeNumber(fairNumber);
        reviewCount.setBadTypeNumber(badNumber);
        return reviewCount;
    }

    @RequestMapping(value = "/user/reviews", method = RequestMethod.GET)
    @AccessTokenAuthentication
    public List<ShtUserRev> getReviewByUserId(@RequestParam Long userId, ShtUserRev.UserReviewRate userReviewRate){
        List<ShtUserRev> userRevs = userRevService.getReviewByUserId(userId, userReviewRate);
        return userRevs;
    }

    @RequestMapping(value = "/user/list-reviews", method = RequestMethod.GET)
    @AccessTokenAuthentication
    public Page<ShtUserRev> getReviewByUserId(@RequestParam(value ="userId", required = false) Long userId,
                                              @RequestParam(value ="userReviewRate", required = false) ShtUserRev.UserReviewRate userReviewRate,
                                              @RequestParam(value ="page", required = true) int page,
                                              @RequestParam(value ="size", required = true) int size){
        if(userId == null && requestContext.getUser() != null){
            userId = requestContext.getUser().getId();
        }
        Pageable pageable = createPage(page, size);
        Page<ShtUserRev> userRevs = userRevService.getReviewToUserId(userId,pageable,userReviewRate);
        return userRevs;
    }

    @RequestMapping(value = "/user/list-reviews-no-login", method = RequestMethod.GET)
    @NoAuthentication
    public Page<ShtUserRev> getReviewByUserIdNoLogin(@RequestParam(value ="userId", required = false) Long userId,
                                              @RequestParam(value ="userReviewRate", required = false) ShtUserRev.UserReviewRate userReviewRate,
                                              @RequestParam(value ="page", required = true) int page,
                                              @RequestParam(value ="size", required = true) int size){
        if(userId == null && requestContext.getUser() != null){
            userId = requestContext.getUser().getId();
        }
        Pageable pageable = createPage(page, size);
        Page<ShtUserRev> userRevs = userRevService.getReviewToUserId(userId,pageable,userReviewRate);
        return userRevs;
    }

    @RequestMapping(value = "/user/create-reviews", method = RequestMethod.POST)
    @AccessTokenAuthentication
    @ResponseBody
    public ResponseEntity<ShtUserRev> createReview(@RequestBody UserRevBodyRequest userRevBodyRequest) throws Exception {
        ShmUser currentUser = requestContext.getUser();
        ShtUserRev shtUserRevSaved = userRevService.createReview(userRevBodyRequest,currentUser);
        final ShmUser fromShmUser = shtUserRevSaved.getFromShmUser();
        final ShmUser toShmUser = shtUserRevSaved.getToShmUser();
        final String postName = shtUserRevSaved.getShtTalkPurc().getShmPost().getPostName();
        boolean checkTurnOnEmail = userSettingService.checkReceivingMailInTransaction(toShmUser.getId());
        if (checkTurnOnEmail)
            mailService.sendEmailAfterReview(toShmUser.getNickName(), fromShmUser.getNickName(), postName, toShmUser.getEmail(), toShmUser.getId());

        boolean muteBlockFlag = talkPurcService.checkUserMutedOrBlocked(fromShmUser.getId(), toShmUser.getId());
        boolean turnOnPushTrans = userSettingService.checkReceivePushTalkRoomTransaction(toShmUser.getId());
        if (!muteBlockFlag) {
            if (turnOnPushTrans) {
                String msgContent = getMessage("PUSH_REVIEW_MSG", currentUser.getNickName());
                PushMsgCommon pushMsg = PushUtils.buildPushMsgCommon(msgContent);
                PushMsgDetail pushMsgDetail = PushUtils.buildPushMsgDetail(PushMsgDetail.NtfTypeEnum.REVIEW_USER, null, null, null, null, null, null, toShmUser.getId());
                snsMobilePushService.sendNotificationForUser(toShmUser.getId(), pushMsg, pushMsgDetail);
            }
        }

        // interrupt job remind review
        Long currentUserId = currentUser.getId();
        ShmUser partner = shtUserRevSaved.getShtTalkPurc().getShmUser();
        Long partnerId = partner.getId();
        if(currentUserId.intValue() == partnerId.intValue()){
            userRevService.interruptReviewRemindJob(shtUserRevSaved.getShtTalkPurc().getTalkPurcId(),true);
        }else{
            userRevService.interruptReviewRemindJob(shtUserRevSaved.getShtTalkPurc().getTalkPurcId(),false);
        }

        return new ResponseEntity<ShtUserRev>(shtUserRevSaved, HttpStatus.CREATED);
    }
}
