package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShtGroupPblDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by buixu on 11/14/2017.
 */
@Repository
public interface GroupPblDetailRepository extends JpaRepository<ShtGroupPblDetail, Long>{

    @Query(value = "SELECT gd FROM ShtGroupPblDetail gd WHERE gd.groupPublish.id =:groupId order by gd.createdAt DESC ")
    List<ShtGroupPblDetail> getGroupDetail(@Param("groupId") Long groupId);

    @Query(value = "SELECT gd FROM ShtGroupPblDetail gd WHERE gd.legalId =:legalId order by gd.createdAt DESC ")
    List<ShtGroupPblDetail> getGroupDetailByLegalId(@Param("legalId") String legalId);
}
