package jp.bo.bocc.service.impl;

import jp.bo.bocc.entity.ShrFile;
import jp.bo.bocc.service.FileService;
import jp.bo.bocc.system.config.AppConfig;
import jp.bo.bocc.system.config.Profiles;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * @author manhnt
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@ActiveProfiles(Profiles.APP)
@Transactional
public class FileServiceImplTest {

	@Autowired
	private FileService fileService;

	private ShrFile createSampleFile() {
		ShrFile file = new ShrFile();
		file.setName("Hello");
		file.setDir("a/b/c/d/");
		file.setExtension("txt");

		ShrFile savedFile = fileService.save(file);

		return fileService.get(savedFile.getId());
	}

	@Test
	public void testSave() throws Exception {
		ShrFile file = createSampleFile();

		assertEquals("Hello", file.getName());
		assertEquals("a/b/c/d/", file.getDir());
		assertEquals("txt", file.getExtension());
	}

	@Test
	public void testGet() throws Exception {
		ShrFile file = createSampleFile();

		ShrFile returnFile = fileService.get(file.getId());
		assertEquals("Hello", returnFile.getName());
		assertEquals("a/b/c/d/", returnFile.getDir());
		assertEquals("txt", returnFile.getExtension());
	}

	@Test
	public void delete() throws Exception {
		ShrFile file = createSampleFile();

		fileService.delete(file.getId());
		ShrFile returnFile = fileService.get(file.getId());

		assertThat(returnFile, nullValue());
	}

	@Test
	public void testFindByPath() throws Exception {
		createSampleFile();
		ShrFile returnFile = fileService.findByPath("a/b/c/d/Hello.txt");

		assertThat(returnFile, notNullValue());
		assertEquals("Hello", returnFile.getName());
		assertEquals("a/b/c/d/", returnFile.getDir());
		assertEquals("txt", returnFile.getExtension());
	}
}