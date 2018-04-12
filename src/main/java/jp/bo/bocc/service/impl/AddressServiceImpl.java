package jp.bo.bocc.service.impl;

import jp.bo.bocc.entity.ShmAddr;
import jp.bo.bocc.repository.AddressRepository;
import jp.bo.bocc.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author manhnt
 */
@Service("addressService")
public class AddressServiceImpl implements AddressService {

	@Autowired
	private AddressRepository repository;

	@Override
	@Transactional(readOnly = true)
	public List<ShmAddr> getProvinces() {
		return repository.findByAddrParentIdIsNull();
	}

	@Override
	@Transactional(readOnly = true)
	public ShmAddr getAddress(Long id) {
		return repository.findOne(id);
	}

	@Override
	public ShmAddr save(ShmAddr address) {
		return repository.save(address);
	}

	@Override
	public void delete(Long id) {
		repository.delete(id);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ShmAddr> getAddresses(Long addrParentId) {
		if(addrParentId == null) {
			return repository.findByAddrParentIdIsNull();
		} else {
			return repository.findByAddrParentIdOrderByAddressIdAsc(addrParentId);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public String getFullAddress(ShmAddr shmAddr) {
		String address = "";
		if(shmAddr == null){
			return null;
		}
		Long addrParentId = shmAddr.getAddrParentId();
		if(addrParentId != null && addrParentId > 0){
			ShmAddr parentAddr = repository.getOne(addrParentId);
			if(parentAddr != null){
				address = parentAddr.getAreaName() + shmAddr.getAreaName();
			}
		}else{
			address = shmAddr.getAreaName();
		}
		return address;
	}

	@Override
	public List<Long> getListDistrictIdByProvinceId(Long parentId) {
		return repository.getListDistrictIdByProvinceId(parentId);
	}
}
