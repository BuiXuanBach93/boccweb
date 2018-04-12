package jp.bo.bocc.service.impl;

import jp.bo.bocc.controller.web.request.QaSearchRequest;
import jp.bo.bocc.entity.ShmAdmin;
import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShtUserReadMsg;
import jp.bo.bocc.entity.ShtQa;
import jp.bo.bocc.entity.ShtTalkQa;
import jp.bo.bocc.helper.ConverterUtils;
import jp.bo.bocc.repository.QaRepository;
import jp.bo.bocc.service.MailService;
import jp.bo.bocc.service.UserReadMsgService;
import jp.bo.bocc.service.QaService;
import jp.bo.bocc.service.TalkQaService;
import jp.bo.bocc.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by DonBach on 4/5/2017.
 */
@Service
public class QaServiceImpl implements QaService {

    private final static Logger LOGGER = Logger.getLogger(QaServiceImpl.class.getName());

    @Value("${page.size}")
    private int maxRecordsInPage;

    @Autowired
    QaRepository qaRepository;
    @Autowired
    QaService qaService;
    @Autowired
    TalkQaService talkQaService;
    @Autowired
    MailService mailService;
    @Value("${mail.admin}")
    private String mailAdminAddress;
    @Autowired
    MessageSource messageSource;

    @Autowired
    UserService userService;

    @Autowired
    UserReadMsgService userReadMsgService;

    @Value("${user.system.id}")
    private Long systemUserId;

    @Override
    @Transactional(readOnly = true)
    public ShtQa getQa(long id) {
        return qaRepository.getQaByQaId(id);
    }

    @Override
    @Transactional(readOnly = true)
    public ShtQa getQaById(Long id) {
        return qaRepository.getQaByQaId(id);
    }

    @Override
    public ShtQa saveQa(ShtQa shtQa) {
        return qaRepository.save(shtQa);
    }

    @Override
    public ShtQa createQa(ShtQa shtQa, ShmUser shmUser) {
        shtQa.setShmUser(shmUser);
        if(shtQa.getQaType() == null){
            shtQa.setQaType(ShtQa.QaTypeEnum.OTHER);
        }
        shtQa.setQaContentType(ShtQa.QaContentTypeEnum.QA);
        ShtQa qaSaved = qaService.saveQa(shtQa);
        ShtTalkQa talkQa = new ShtTalkQa();
        talkQa.setShtQa(qaSaved);
        talkQa.setTalkQaMsg(shtQa.getFirstQaMsg());
        talkQa.setFromAdmin(false);
        talkQaService.save(talkQa);
        return shtQa;
    }


    @Override
    @Transactional(readOnly = true)
    public Page<ShtQa> getListQa(Integer pageNumber) {
        Pageable pageable = new PageRequest(pageNumber, maxRecordsInPage);
        Page<ShtQa> shtQas = qaRepository.getListQaOrder(pageable," QA_STATUS IN (0,1) ");
        List<ShtQa> result = shtQas.getContent();
        for (ShtQa shtQa: result) {
            shtQa.setHaveFeedback(qaRepository.haveFeedback(shtQa));
        }
        return shtQas;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShtQa> searchListQa(QaSearchRequest qaSearchRequest, Integer pageNumber) {
        Pageable pageable = new PageRequest(pageNumber , maxRecordsInPage);
        String whereClause = buildSqlCondition(qaSearchRequest);
        Page<ShtQa> shtQas = qaRepository.getListQaOrder(pageable, whereClause);
        List<ShtQa> result = shtQas.getContent();
        for (ShtQa shtQa: result) {
            shtQa.setHaveFeedback(qaRepository.haveFeedback(shtQa));
        }
        return shtQas;
    }

    private String buildSqlCondition(QaSearchRequest qaSearchRequest){
        String sqlCondition = " 1=1 ";
        String sqlStatusCondition = "";
        if(qaSearchRequest.getIsNoResponse()){
            sqlStatusCondition += " QA_STATUS = 0 ";
        }
        if(qaSearchRequest.getIsInProgress()){
            if(StringUtils.isEmpty(sqlStatusCondition)){
                sqlStatusCondition += " QA_STATUS = 1 ";
            }else{
                sqlStatusCondition += " OR QA_STATUS = 1 ";
            }
        }
        if(qaSearchRequest.getIsResolved()){
            if(StringUtils.isEmpty(sqlStatusCondition)){
                sqlStatusCondition += " QA_STATUS = 2 ";
            }else{
                sqlStatusCondition += " OR QA_STATUS = 2 ";
            }
        }
        if(!StringUtils.isEmpty(sqlStatusCondition)){
            sqlCondition += " AND ( " + sqlStatusCondition + " ) ";
        }
        if(qaSearchRequest.getCreatedAt() != null){
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String createdAt = dateFormat.format(qaSearchRequest.getCreatedAt());
            sqlCondition += " AND TO_CHAR(CMN_ENTRY_DATE,'yyyy-MM-dd') LIKE '" +createdAt+"'";
        }
        if(qaSearchRequest.getHaveFeedback()){
            sqlCondition += " AND QA_STATUS = 1 " +
                    " AND (SELECT FROM_ADMIN FROM  SHT_QA " +
                    " INNER JOIN SHT_TALK_QA stq2 ON SHT_QA.QA_ID = stq2.QA_ID WHERE SHT_QA.QA_ID = sq.QA_ID " +
                    " AND stq2.CMN_ENTRY_DATE = ( SELECT MAX(CMN_ENTRY_DATE) FROM SHT_TALK_QA WHERE SHT_TALK_QA.QA_ID = stq2.QA_ID  )) = 0 ";
        }
        return  sqlCondition;
    }

    @Transactional(readOnly = true)
    public Page<ShtQa> getListQaByUserId(Long userId, int page, int size) {
        Pageable pageable = new PageRequest(page,size);
        return qaRepository.getListQaByUserId(userId, pageable);
    }

    @Override
    @Transactional
    public List<ShtTalkQa> getListTalkQaByCondition(ShmUser shmUser, String msgTime, Boolean isBefore, Long qaId, int size) {
        if (StringUtils.isEmpty(msgTime)) {
            isBefore = true;
        }
        List<ShtTalkQa> listTalkQas = qaRepository.getListTalkQaByCondition(ConverterUtils.convertStringToLocaldatetime(msgTime),isBefore,qaId,size);
        // update read message
        ShtQa shtQa = qaService.getQaById(qaId);
        if(shtQa.getShmUser() != null && shtQa.getShmUser().getId().intValue() != systemUserId.intValue()){
            qaRepository.readedMsgUpdate(qaId);
        }else {
            Boolean checkRead = userReadMsgService.checkUserReadMsg(shmUser.getId(), qaId);
            if(checkRead != null && checkRead == false){
                ShtUserReadMsg userReadMsg = new ShtUserReadMsg();
                userReadMsg.setQaId(qaId);
                userReadMsg.setUserId(shmUser.getId());
                userReadMsgService.createUserReadMsg(userReadMsg);
            }
        }
        return listTalkQas;
    }

    @Override
    @Transactional(readOnly = true)
    public String getFirstMsg(Long qaId) {
        return qaRepository.getFirstMsg(qaId);
    }

    @Override
    public Long getFirstMsgId(Long qaId) {
        return qaRepository.getFirstMsgId(qaId);
    }

    @Override
    public void sendMailToUserAfterCompletedQa(ShtQa shtQa){
        if(shtQa == null || shtQa.getShmUser() == null){
            return;
        }
        ShmUser shmUser = shtQa.getShmUser();
        mailService.sendEmailCompletedQa(shmUser.getEmail(),shmUser.getNickName(),shmUser.getNickName());
    }

    @Override
    public ShtQa createNotifyFromAdminPush(ShtQa shtQa, ShmAdmin shmAdmin) {
        LOGGER.info("BEGIN: ======================= QaServiceImpl.createNotifyFromAdminPush." );
        ShmUser systemUser = userService.getSystemUser();
        shtQa.setShmUser(systemUser);
        if(shtQa.getQaType() == null){
            shtQa.setQaType(ShtQa.QaTypeEnum.OTHER);
        }
        shtQa.setQaContentType(ShtQa.QaContentTypeEnum.SYSTEM_PUSH_ALL);
        ShtQa qaSaved = qaService.saveQa(shtQa);
        ShtTalkQa talkQa = new ShtTalkQa();
        talkQa.setShtQa(qaSaved);
        talkQa.setTalkQaMsg(shtQa.getFirstQaMsg());
        talkQa.setFromAdmin(true);
        talkQa.setShmAdmin(shmAdmin);
        talkQaService.save(talkQa);
        LOGGER.info("END: ======================= QaServiceImpl.createNotifyFromAdminPush." );
        return  qaSaved;
    }

    @Override
    public ShtQa createNotifyFromAdmin(ShmUser shmUser, ShtQa shtQa, ShmAdmin shmAdmin) {
        shtQa.setShmUser(shmUser);
        if(shtQa.getQaType() == null){
            shtQa.setQaType(ShtQa.QaTypeEnum.OTHER);
        }
        shtQa.setQaContentType(ShtQa.QaContentTypeEnum.SYSTEM);
        ShtQa qaSaved = qaService.saveQa(shtQa);
        ShtTalkQa talkQa = new ShtTalkQa();
        talkQa.setShtQa(qaSaved);
        talkQa.setTalkQaMsg(shtQa.getFirstQaMsg());
        talkQa.setFromAdmin(true);
        talkQa.setShmAdmin(shmAdmin);
        talkQaService.save(talkQa);
        return  qaSaved;
    }

    @Override
    public ShtQa getQaByAdminIdAndUserIdAndPushType(Long adminId, Long userId, ShtQa.QaContentTypeEnum SYSTEM_PUSH_ALL) {
        return qaRepository.getQaByAdminIdAndUserIdAndPushType(adminId, userId,SYSTEM_PUSH_ALL);
    }
}
