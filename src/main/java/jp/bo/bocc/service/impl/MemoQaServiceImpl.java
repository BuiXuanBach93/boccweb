package jp.bo.bocc.service.impl;

import jp.bo.bocc.entity.ShmAdmin;
import jp.bo.bocc.entity.ShtMemoQa;
import jp.bo.bocc.entity.ShtQa;
import jp.bo.bocc.repository.MemoQaRepository;
import jp.bo.bocc.service.MemoQaService;
import jp.bo.bocc.service.QaService;
import jp.bo.bocc.system.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by NguyenThuong on 5/26/2017.
 */
@Service
public class MemoQaServiceImpl implements MemoQaService {

    @Autowired
    MemoQaRepository memoQaRepository;

    @Autowired
    QaService qaService;

    @Override
    public ShtMemoQa save(Long qaId, ShmAdmin admin, String content) {
        ShtQa qa = qaService.getQaById(qaId);
        if (qa == null) {
            throw new ResourceNotFoundException("");
        }

        ShtMemoQa memoQa = new ShtMemoQa();
        memoQa.setAdmin(admin);
        memoQa.setContent(content);
        memoQa.setQa(qa);
        memoQaRepository.save(memoQa);

        return memoQa;
    }

    @Override
    public List<ShtMemoQa> getMemoQaListByQAId(Long qaId) {
        ShtQa qa = qaService.getQaById(qaId);
        if (qa == null)
            throw new ResourceNotFoundException("");

        return memoQaRepository.findByQaOrderByUpdatedAt(qa);
    }
}
