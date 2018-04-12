package jp.bo.bocc.helper;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 * @author manhnt
 */
public class StringUtilsTest {

	@Test
	public void testGenerateUniqueToken() throws Exception {
		String token1 = StringUtils.generateUniqueToken();
		String token2 = StringUtils.generateUniqueToken();
		System.out.println(token1);
		System.out.println(token2);
		assertNotEquals(token1, token2);
	}
}
