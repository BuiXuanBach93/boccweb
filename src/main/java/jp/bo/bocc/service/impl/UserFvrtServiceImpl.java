package jp.bo.bocc.service.impl;

import jp.bo.bocc.entity.ShtUserFvrt;
import jp.bo.bocc.repository.UserFvrtRepository;
import jp.bo.bocc.service.UserFvrtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by DonBach on 3/31/2017.
 */
@Service
public class UserFvrtServiceImpl implements UserFvrtService{

    @Autowired
    UserFvrtRepository userFvrtRepository;

    @Override
    public ShtUserFvrt getUserFvrt(long postId, long userId) {
        return userFvrtRepository.findUserFvrtByPostIdAndUserId(postId, userId);
    }

    @Override
    public ShtUserFvrt updateUserFavoriteStatus(ShtUserFvrt shtUserFvrt, String status) {
        shtUserFvrt.setUserFvrtStatus(ShtUserFvrt.UserFavoriteEnum.valueOf(status));
        return userFvrtRepository.save(shtUserFvrt);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countLikeTimeByPostId(Long postId) {
        return userFvrtRepository.countLikeTimeByPostId(postId);
    }

    @Override
    public List<ShtUserFvrt> getUserFvrts(Long postId) {
        return userFvrtRepository.getUserFvrts(postId);
    }
}
