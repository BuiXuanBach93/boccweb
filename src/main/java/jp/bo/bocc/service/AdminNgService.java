package jp.bo.bocc.service;

import jp.bo.bocc.entity.ShmAdmin;
import jp.bo.bocc.entity.ShmAdminNg;
import jp.bo.bocc.entity.dto.ShmAdminNgDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by HaiTH on 3/22/2017.
 */
public interface AdminNgService {

    public Page<ShmAdminNgDTO> getListAdminNg(ShmAdminNgDTO adminNgDTO, Pageable pageNumber);

    public ShmAdminNg showAdminNgById(long adminNgId);

    public  void createAdminNg(ShmAdminNg shmAdminNg, ShmAdmin shmAdmin);

    public  void updateAdminNg(ShmAdminNg shmAdminNg, long id, ShmAdmin shmAdmin);

    public  void deleteAdminNg(long id, ShmAdmin shmAdmin);

    public boolean findAdminNgContent (String adminNgContent);

    public List<String> checkNGContent(String content);

}
