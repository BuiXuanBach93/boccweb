package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtUserRev;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Created by DonBach on 4/4/2017.
 */
public interface UserRevRepositoryCustom {
    Page<ShtUserRev> getReviewToUser(Long userId, Pageable pageable, ShtUserRev.UserReviewRate userReviewRate);
}
