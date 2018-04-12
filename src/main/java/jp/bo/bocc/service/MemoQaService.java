package jp.bo.bocc.service;

import jp.bo.bocc.entity.ShmAdmin;
import jp.bo.bocc.entity.ShtMemoQa;

import java.util.List;

/**
 * @author NguyenThuong on 5/26/2017.
 */
public interface MemoQaService {

    ShtMemoQa save(Long qaId, ShmAdmin admin, String content);

    List<ShtMemoQa> getMemoQaListByQAId(Long qaId);
}
