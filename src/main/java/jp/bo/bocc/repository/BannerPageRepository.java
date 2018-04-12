package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtBannerPage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by buixu on 12/29/2017.
 */
@Repository
public interface BannerPageRepository extends JpaRepository<ShtBannerPage, Long> {

    List<ShtBannerPage> findByBannerId(Long bannerId);
}
