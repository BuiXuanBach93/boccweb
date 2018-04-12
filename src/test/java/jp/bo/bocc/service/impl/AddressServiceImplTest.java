package jp.bo.bocc.service.impl;

import com.amazonaws.services.dynamodbv2.xspec.L;
import jp.bo.bocc.entity.ShmAddr;
import jp.bo.bocc.service.AddressService;
import jp.bo.bocc.repository.SyncExcRepository;
import jp.bo.bocc.system.config.AppConfig;
import jp.bo.bocc.system.config.Profiles;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 * @author manhnt
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@Transactional
@ActiveProfiles(Profiles.APP)
public class AddressServiceImplTest {

	@Autowired
	SyncExcRepository syncExcRepository;

    @Test
    public void getProvinces() throws Exception {
		List<Long> userIds = syncExcRepository.getUserDataFromBSSystem("2018-03-01");
		if(userIds.size() > 0){
			return;
		}
	}

    @Autowired
	private AddressService addressService;

	@Test
	public void getAddress() throws Exception {
		LocalDateTime local2 = LocalDateTime.of(2018,1,31,0,0,0);
		LocalDateTime nextMonth = local2.plusMonths(1);
		int currentDay = nextMonth.getDayOfMonth();
		int count = nextMonth.plusDays(-currentDay).getDayOfMonth();
		int count11 = count + currentDay;
	}

	@Test
	public void save() throws Exception {
		AddressSet addressSet = new AddressSet();
		ShmAddr hanoi = addressSet.hanoi;
		ShmAddr hoankiem = addressSet.hoankiem;
		ShmAddr caugiay = addressSet.caugiay;

		assertEquals("10", hanoi.getAreaCode());
		assertEquals("Hanoi", hanoi.getAreaName());
	}

	private class AddressSet {
		ShmAddr hoankiem;
		ShmAddr caugiay;
		ShmAddr hanoi;

		public AddressSet() {
			ShmAddr creatingAddress = new ShmAddr();
			creatingAddress.setAreaCode("10");
			creatingAddress.setAreaName("Hanoi");

			hoankiem = new ShmAddr();
			hoankiem.setAreaCode("11");
			hoankiem.setAreaName("Hoan Kiem");


			caugiay = new ShmAddr();
			caugiay.setAreaCode("12");
			caugiay.setAreaName("Cau Giay");

			ShmAddr savedAddress = addressService.save(creatingAddress);
			hanoi = addressService.getAddress(savedAddress.getAddressId());
		}
	}
}