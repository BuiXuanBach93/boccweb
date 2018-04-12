package jp.bo.bocc.service.impl;

import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShtUserRprt;
import jp.bo.bocc.repository.UserRprtRepository;
import jp.bo.bocc.service.UserRprtService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by DonBach on 3/31/2017.
 */
@Service
public class UserRprtServiceImpl implements UserRprtService {

    @Autowired
    UserRprtRepository userRprtRepository;

    @Override
    public ShtUserRprt getUserRprt(long postId, long userId) {
        return userRprtRepository.findUserRprtByPostIdAndUserId(postId, userId);
    }

    @Override
    public ShtUserRprt save(ShtUserRprt shtUserRprt) {
        return userRprtRepository.save(shtUserRprt);
    }

    @Override
    @Transactional(readOnly = true)
    public long countTotalReport(long userId) {
        return userRprtRepository.countReportByUserId(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public long countTotalReportMe(long userId) {
        return userRprtRepository.countReportMe(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public String getNicknameLastUserReportPost(Long postId) {
        List<ShmUser> nicknameLastUserReportPost = userRprtRepository.getLastUserReportPost(postId);
        if (CollectionUtils.isNotEmpty(nicknameLastUserReportPost)) {
            ShmUser shmUser = nicknameLastUserReportPost.get(0);
            return shmUser.getNickName();
        }
        else
            return "";
    }

    @Override
    public Long countTotalReportByPostId(Long postId) {
        return userRprtRepository.countTotalReportByPostId(postId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShtUserRprt> getPostReports(Long postId) {

        List<ShtUserRprt> result = userRprtRepository.getPostReport(postId);
        for (ShtUserRprt report: result) {
            report.setReportTitle(report.getUserRprtType().value());
            if(report.getReportPtrlStatus() == ShtUserRprt.ReportPatrolStatus.UNCENSORED){
                report.setReportStatusStr("未対応");
            }else if(report.getReportPtrlStatus() == ShtUserRprt.ReportPatrolStatus.CENSORING){
                report.setReportStatusStr("対応中");
            }else{
                report.setReportStatusStr("対応完了");
            }
        }
        return result;
    }
}
