package jp.bo.bocc.service;

import jp.bo.bocc.controller.web.request.QaSearchRequest;
import jp.bo.bocc.entity.ShmAdmin;
import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShtQa;
import jp.bo.bocc.entity.ShtTalkQa;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by DonBach on 4/5/2017.
 */
public interface QaService {
    ShtQa getQa(long id);

    ShtQa getQaById(Long id);

    ShtQa saveQa(ShtQa shtQa);

    ShtQa createQa(ShtQa shtQa, ShmUser shmUser);

    Page<ShtQa> getListQa (Integer pageNumber);

    Page<ShtQa> searchListQa(QaSearchRequest qaSearchRequest, Integer pageNumber);

    Page<ShtQa> getListQaByUserId(Long userId, int page, int size);

    List<ShtTalkQa> getListTalkQaByCondition(ShmUser shmUser, String msgTime, Boolean isBefore, Long qaId, int size);

    String getFirstMsg(Long qaId);

    Long getFirstMsgId(Long qaId);

    void sendMailToUserAfterCompletedQa(ShtQa shtQa);

    ShtQa createNotifyFromAdmin(ShmUser shmUser, ShtQa shtQa, ShmAdmin shmAdmin);

    ShtQa createNotifyFromAdminPush(ShtQa shtQa, ShmAdmin shmAdmin);

    ShtQa getQaByAdminIdAndUserIdAndPushType(Long adminId, Long userId, ShtQa.QaContentTypeEnum ordinal);
}
