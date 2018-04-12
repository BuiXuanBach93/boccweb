package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtQa;
import jp.bo.bocc.entity.ShtTalkQa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;


/**
 * Created by DonBach on 4/12/2017.
 */
public interface QaRepositoryCustom {

    boolean haveFeedback(ShtQa shtQa);
    Page<ShtQa> getListQaOrder(Pageable page, String whereClause);
    Page<ShtQa> getListQaByUserId(Long userId, Pageable pageable);
    List<ShtTalkQa> getListTalkQaByCondition(LocalDateTime msgTime, Boolean isBefore, Long qaId, int size);
    String getFirstMsg(Long qaId);
    Long getFirstMsgId(Long qaId);

    ShtQa getQaByAdminIdAndUserIdAndPushType(Long adminId, Long userId, ShtQa.QaContentTypeEnum system_push_all);
}
