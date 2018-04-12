package jp.bo.bocc.jobs;

import jp.bo.bocc.service.PushNotifyService;
import jp.bo.bocc.service.SyncExcService;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.text.ParseException;

/**
 * Created by DonBach on 5/4/2017.
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class JobHandleSyncTendToLeaveUser implements Job {
    private final static Logger LOGGER = Logger.getLogger(JobHandleSyncTendToLeaveUser.class.getName());

    @Getter
    @Setter
    private String jobName;

    @Autowired
    SyncExcService syncExcService;

    @Override
    @Transactional
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        LOGGER.info("START JOB: SYNC TEND TO LEAVE USERS" );
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        final JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        try {
            syncExcService.handleSyncUserTendToLeave();
        } catch (SchedulerException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LOGGER.info("END JOB: SYNC TEND TO LEAVE USERS" );
    }
}
