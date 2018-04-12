package jp.bo.bocc.jobs;

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
public class JobHandleSyncLeftUser implements Job {
    private final static Logger LOGGER = Logger.getLogger(JobHandleSyncLeftUser.class.getName());

    @Getter
    @Setter
    private String jobName;

    @Autowired
    SyncExcService syncExcService;

    @Override
    @Transactional
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        LOGGER.info("START JOB: SYNC LEFT USERS" );
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        final JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        Long syncExcId = new Long(jobDataMap.get("syncExcId").toString());
        syncExcService.handleSyncUserLeft(syncExcId);
        LOGGER.info("END JOB: SYNC LEFT USERS SYNC_ID: " + syncExcId);
    }
}
