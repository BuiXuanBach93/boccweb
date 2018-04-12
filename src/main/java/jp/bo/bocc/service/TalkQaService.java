package jp.bo.bocc.service;

import jp.bo.bocc.controller.web.request.TalkQaRequest;
import jp.bo.bocc.entity.ShmAdmin;
import jp.bo.bocc.entity.ShtTalkQa;

import java.util.List;

/**
 * Created by DonBach on 4/5/2017.
 */
public interface TalkQaService {

    ShtTalkQa getTalkQa(long id);

    ShtTalkQa saveTalkQa(TalkQaRequest talkQa, ShmAdmin admin);

    ShtTalkQa save(ShtTalkQa talkQa);

    List<ShtTalkQa> getListMsgByQaId(Long qaId);

    void sendMailToUserAfterResponse(ShtTalkQa talkQa);

    Long countNewMsgByUserId(Long userId);
}
