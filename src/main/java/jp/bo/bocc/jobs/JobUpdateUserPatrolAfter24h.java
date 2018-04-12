package jp.bo.bocc.jobs;

import jp.bo.bocc.service.MemoUserService;
import org.apache.log4j.Logger;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 * @author NguyenThuong on 5/11/2017.
 */
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class JobUpdateUserPatrolAfter24h implements Job {

    private final static Logger LOGGER = Logger.getLogger(JobUpdateUserPatrolAfter24h.class);

    @Autowired
    MemoUserService memoUserService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        LOGGER.info("Start job: update user patrol field after 24 hours");
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        memoUserService.updateUserPatrol();
        LOGGER.info("Finished job: update user patrol field after 24 hours");
    }
}
