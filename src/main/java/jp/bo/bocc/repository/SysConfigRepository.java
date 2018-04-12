package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShrSysConfig;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by Namlong on 6/30/2017.
 */
public interface SysConfigRepository extends JpaRepository<ShrSysConfig, String> {

    ShrSysConfig findBySysConfigCode(String maintainMode);
}
