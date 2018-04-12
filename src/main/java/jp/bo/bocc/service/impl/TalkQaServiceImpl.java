package jp.bo.bocc.service.impl;

import jp.bo.bocc.controller.web.request.TalkQaRequest;
import jp.bo.bocc.entity.ShmAdmin;
import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShtQa;
import jp.bo.bocc.entity.ShtTalkQa;
import jp.bo.bocc.repository.TalkQaRepository;
import jp.bo.bocc.service.MailService;
import jp.bo.bocc.service.QaService;
import jp.bo.bocc.service.TalkQaService;
import jp.bo.bocc.system.exception.ServiceException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by DonBach on 4/5/2017.
 */
@Service
public class TalkQaServiceImpl implements TalkQaService {

    @Autowired
    TalkQaRepository repo;

    @Autowired
    QaService qaService;

    @Autowired
    MailService mailService;

    @Autowired
    MessageSource messageSource;


    @Override
    @Transactional(readOnly = true)
    public ShtTalkQa getTalkQa(long id) {
        return repo.getOne(id);
    }

    @Override
    public ShtTalkQa save(ShtTalkQa talkQa) {
        return repo.save(talkQa);
    }

    @Override
    public ShtTalkQa saveTalkQa(TalkQaRequest talkQaRequest, ShmAdmin admin) {

        ShtQa qa = qaService.getQaById(talkQaRequest.getQaId());
        if (qa == null)
            throw new ServiceException(HttpStatus.NOT_FOUND, "");

        ShtTalkQa talkQa = new ShtTalkQa();
        talkQa.setShtQa(qa);
        talkQa.setTalkQaMsg(StringEscapeUtils.escapeHtml4(talkQaRequest.getQaContent()));
        talkQa.setShmAdmin(admin);
        talkQa.setFromAdmin(true);

        return repo.save(talkQa);
    }

    @Override
     public void sendMailToUserAfterResponse(ShtTalkQa talkQa){
        if(talkQa == null || talkQa.getShtQa() == null || talkQa.getShtQa().getShmUser() == null){
            return;
        }
        ShtQa shtQa = talkQa.getShtQa();
        ShmUser shmUser = shtQa.getShmUser();
        String firstMsg = qaService.getFirstMsg(shtQa.getQaId());
        String qaTypeStr = "";
        if(shtQa.getQaType() == ShtQa.QaTypeEnum.ACCOUNT_PROBLEM){
            qaTypeStr = messageSource.getMessage("QA_TYPE_01", null, null);
        }
        if(shtQa.getQaType() == ShtQa.QaTypeEnum.POST_PROBLEM){
            qaTypeStr = messageSource.getMessage("QA_TYPE_02", null, null);
        }
        if(shtQa.getQaType() == ShtQa.QaTypeEnum.USAGE_PROBLEM){
            qaTypeStr = messageSource.getMessage("QA_TYPE_03", null, null);
        }
        if(shtQa.getQaType() == ShtQa.QaTypeEnum.HELP){
            qaTypeStr = messageSource.getMessage("QA_TYPE_04", null, null);
        }
        if(shtQa.getQaType() == ShtQa.QaTypeEnum.LEAVING){
            qaTypeStr = messageSource.getMessage("QA_TYPE_05", null, null);
        }
        mailService.sendEmailResponseQa(shmUser.getEmail(), shmUser.getNickName(), shmUser.getNickName(), talkQa.getTalkQaMsg(), qaTypeStr, firstMsg);
    }

    @Override
    public Long countNewMsgByUserId(Long userId) {
        return repo.countNewMsgByUserId(userId);
    }

    @Override
    public List<ShtTalkQa> getListMsgByQaId(Long qaId) {
        return repo.getListMsgByQaId(qaId);
    }
}
