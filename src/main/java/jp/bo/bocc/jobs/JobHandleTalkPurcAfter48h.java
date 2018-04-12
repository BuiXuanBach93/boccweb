package jp.bo.bocc.jobs;

import jp.bo.bocc.service.TalkPurcMsgService;
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
public class JobHandleTalkPurcAfter48h implements Job {
    private final static Logger LOGGER = Logger.getLogger(JobHandleTalkPurcAfter48h.class.getName());
    @Getter
    @Setter
    private String jobName;

    @Autowired
    TalkPurcMsgService talkPurcMsgService;

    @Override
    @Transactional
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        LOGGER.info("START JOB: Talk purc auto execute after 48 hours" );
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
        talkPurcMsgService.handleTalkAfter48h();
        LOGGER.info("END JOB: Talk purc auto execute after 48 hours" );
    }
}
