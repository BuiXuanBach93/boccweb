package jp.bo.bocc.jobs;

import jp.bo.bocc.service.UserService;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 * Created by DonBach on 6/2/2017.
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class JobHandleUserLeft implements Job {
    private final static Logger LOGGER = Logger.getLogger(JobHandleTalkPurcAfter48h.class.getName());
    @Getter
    @Setter
    private String jobName;
    @Autowired
    UserService userService;

    @Override
    @Transactional
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        LOGGER.info("START JOB: User left" );
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        final JobDataMap jobDataMap = jobExecutionContext.getJobDetail().getJobDataMap();
        Long userId =jobDataMap.getLong("userId");
        userService.handleUserLeft(userId);
        LOGGER.info("END JOB: User left" );
    }
}
