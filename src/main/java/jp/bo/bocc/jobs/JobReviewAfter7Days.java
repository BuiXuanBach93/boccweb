package jp.bo.bocc.jobs;

import jp.bo.bocc.push.PushMsgCommon;
import jp.bo.bocc.push.SNSMobilePushService;
import org.apache.log4j.Logger;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 * Created by Namlong on 5/29/2017.
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class JobReviewAfter7Days implements Job {
    private final static Logger LOGGER = Logger.getLogger(JobReviewAfter7Days.class);

    @Autowired
    SNSMobilePushService snsMobilePushService;

    @Autowired
    MessageSource messageSource;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        LOGGER.info("Start job: remind user review after 7 days");
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        PushMsgCommon pushMsgCommon = new PushMsgCommon();
        final JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        String postName =jobDataMap.getString("postName");
        pushMsgCommon.setMsgContent(messageSource.getMessage("PUSH_REVIEW_MSG_AFTER_7_DAY", new Object[]{postName},null));
        long userId = jobDataMap.getLongValue("userId");
        snsMobilePushService.sendNotificationForUser(userId, pushMsgCommon, null);
        LOGGER.info("Finished job: remind user review after 7 days");
    }

}
