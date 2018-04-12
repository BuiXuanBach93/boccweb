package jp.bo.bocc.service.impl;

import jp.bo.bocc.entity.ShrFile;
import jp.bo.bocc.service.StorageService;
import jp.bo.bocc.system.config.AppConfig;
import jp.bo.bocc.system.config.Profiles;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;

/**
 * @author manhnt
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@ActiveProfiles(Profiles.APP)
@Transactional
public class StorageServiceImplTest {

	@Autowired
	private StorageService storageService;

	@Test
	public void testStoreAndGet() throws Exception {
		ShrFile file = new ShrFile();
		file.setName("Hello");
		file.setDir("a/b/c/d/");
		file.setExtension("txt");
		storageService.setPermissionForFile(file, new ByteArrayInputStream("Hello world".getBytes()));

		InputStream inputStream = storageService.getInputStream(file);

		assertEquals("Hello world", IOUtils.toString(inputStream));

		inputStream.close();
	}

	@Test
	public void testFileSize() throws IOException {
		ShrFile file = new ShrFile();
		file.setName("cart_01");
		file.setDir("a/b/c/d/");
		file.setExtension("png");
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("cart.png");
		long fileSize = storageService.setPermissionForFile(file, in);

		assertEquals(41558L, fileSize);

		in.close();
	}
}