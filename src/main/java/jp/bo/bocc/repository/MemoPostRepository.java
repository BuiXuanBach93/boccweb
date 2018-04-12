package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShmPost;
import jp.bo.bocc.entity.ShtMemoPost;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author NguyenThuong on 4/20/2017.
 */
public interface MemoPostRepository extends JpaRepository<ShtMemoPost, Long>, JpaSpecificationExecutor<ShtMemoPost> {

    List<ShtMemoPost> findByPostOrderByUpdatedAt(ShmPost post);

    @Query(value = "SELECT * FROM (SELECT * FROM SHT_MEMO_POST memo WHERE memo.POST_ID = ?1 ORDER BY memo.CMN_ENTRY_DATE DESC) WHERE ROWNUM = 1", nativeQuery = true)
    ShtMemoPost findNewestCommentByPostId(long postId);
}