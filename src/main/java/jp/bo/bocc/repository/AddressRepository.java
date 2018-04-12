package jp.bo.bocc.repository;

import jp.bo.bocc.entity.ShmAddr;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author manhnt
 */
public interface AddressRepository extends JpaRepository<ShmAddr, Long> {
    @Query(" SELECT adr FROM jp.bo.bocc.entity.ShmAddr adr WHERE adr.addrParentId IS NULL ORDER BY adr.addressId ")
    List<ShmAddr> findByAddrParentIdIsNull();

    List<ShmAddr> findByAddrParentIdOrderByAddressIdAsc(Long addrParentId);

    @Query(" SELECT adr.addressId FROM ShmAddr adr WHERE adr.addrParentId = :addressParentId ORDER BY adr.addressId ")
    List<Long> getListDistrictIdByProvinceId(@Param("addressParentId")Long addressParentId);
}
