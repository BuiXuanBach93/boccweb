package jp.bo.bocc.jobs;

import jp.bo.bocc.service.PushNotifyService;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 * Created by DonBach on 5/4/2017.
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class JobHandlePushNotifyFromAdmin implements Job {
    private final static Logger LOGGER = Logger.getLogger(JobHandlePushNotifyFromAdmin.class.getName());
    @Getter
    @Setter
    private String jobName;

    @Autowired
    PushNotifyService pushNotifyService;

    @Override
    @Transactional
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        LOGGER.info("START JOB: PUSH NOTIFY TO USER" );
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        final JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        Long pushId =jobDataMap.getLong("pushId");
        Long adminId =jobDataMap.getLong("adminId");
        pushNotifyService.handleJobPushNotifyOnTimer(pushId, adminId);
        LOGGER.info("END JOB: PUSH NOTIFY TO USER" );
    }
}
