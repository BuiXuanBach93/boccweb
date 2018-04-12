package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShtMemoUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author NguyenThuong on 4/5/2017.
 */
public interface MemoUserRepository extends JpaRepository<ShtMemoUser, Long> {

    List<ShtMemoUser> findByUserOrderByUpdatedAt(ShmUser user);

    @Query(value = "SELECT * FROM (SELECT * FROM SHT_MEMO_USER memo WHERE memo.USER_ID = ?1 ORDER BY memo.CMN_ENTRY_DATE DESC) WHERE ROWNUM = 1", nativeQuery = true)
    ShtMemoUser findNewestCommentByUserId(long userId);
}
