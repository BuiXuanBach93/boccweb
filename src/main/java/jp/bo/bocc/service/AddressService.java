package jp.bo.bocc.service;

import jp.bo.bocc.entity.ShmAddr;

import java.util.List;

/**
 * @author manhnt
 */
public interface AddressService {

	List<ShmAddr> getProvinces();

	ShmAddr getAddress(Long id);

	ShmAddr save(ShmAddr address);

	void delete(Long id);

	List<ShmAddr> getAddresses(Long addrParentId);

	String getFullAddress(ShmAddr shmAddr);

    List<Long> getListDistrictIdByProvinceId(Long addrProvinceId);
}
