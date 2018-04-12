package jp.bo.bocc.helper;

import jp.bo.bocc.entity.ShmUser;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author manhnt
 */
public class BaseEntityTest {

	@Test
	public void testMerge() throws Exception {
		ShmUser user1 = new ShmUser();
		user1.setFirstName("Tom");
		user1.setLastName("Hank");

		ShmUser user2 = new ShmUser();
		user1.setLastName("McLean");
		user2.setGender(ShmUser.Gender.MALE);

		user1.merge(user2);

		assertEquals("McLean", user1.getLastName());
		assertEquals(ShmUser.Gender.MALE, user1.getGender());
	}
}
