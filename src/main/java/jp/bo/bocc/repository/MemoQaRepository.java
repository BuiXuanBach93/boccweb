package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtMemoQa;
import jp.bo.bocc.entity.ShtQa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author NguyenThuong on 5/26/2017.
 */
public interface MemoQaRepository extends JpaRepository<ShtMemoQa, Long> {

    List<ShtMemoQa> findByQaOrderByUpdatedAt(ShtQa qa);
}
