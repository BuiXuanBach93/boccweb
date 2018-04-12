package jp.bo.bocc.service;

import jp.bo.bocc.entity.ShtUserRprt;

import java.util.List;

/**
 * Created by DonBach on 3/31/2017.
 */
public interface UserRprtService {
    ShtUserRprt getUserRprt(long postId, long userId);

    /**
     * Create report.
     *
     * @param shtUserRprt
     * @return
     */
    ShtUserRprt save(ShtUserRprt shtUserRprt);

    long countTotalReport(long userId);

    long countTotalReportMe(long userId);

    String getNicknameLastUserReportPost(Long postId);

    Long countTotalReportByPostId(Long postId);

    List<ShtUserRprt> getPostReports(Long postId);
}
