package jp.bo.bocc.service.impl;

import jp.bo.bocc.entity.ShrFile;
import jp.bo.bocc.entity.ShrImage;
import jp.bo.bocc.helper.ImageBuilder;
import jp.bo.bocc.service.ImageService;
import jp.bo.bocc.system.config.AppConfig;
import jp.bo.bocc.system.config.Profiles;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

/**
 * @author manhnt
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = AppConfig.class)
@ActiveProfiles(Profiles.APP)
@Transactional
public class ImageServiceImplTest {

	@Value("${image.bs64.test}")
	private String bs64;

	@Autowired
	private ImageBuilder imageBuilder;

	@Autowired
	private ImageService imageService;

	@Test
	public void testFindImageFromFileId() throws Exception {

	}

	@Test
	public void testSaveImage() throws Exception {
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("cart.png");
		ShrImage image = imageService.saveImage("a/01/images/", "cart", "png", in);

		ShrFile original = image.getOriginal();
		ShrFile thumbnail = image.getThumbnail();
		assertEquals("a/01/images/", original.getDir());
		assertEquals("cart", original.getOriginalName());
		assertEquals("png", original.getExtension());
		assertEquals(512, original.getWidth().intValue());
		assertEquals(512, original.getHeight().intValue());

		assertEquals("a/01/images/" + imageService.getThumbDir(), thumbnail.getDir());
		assertEquals("cart", thumbnail.getOriginalName());
		assertEquals("png", thumbnail.getExtension());

		List<Integer> thumbDimension = Arrays.asList(imageService.getThumbWidth(), imageService.getThumbHeight());
		assertThat(thumbDimension, anyOf(hasItem(thumbnail.getWidth()), hasItem(thumbnail.getHeight())));
	}

	@Test
	public void testSavePngImage() throws Exception {
		byte[] imgBytes = jp.bo.bocc.helper.StringUtils.base64Decode(bs64);
		InputStream inputStream = new ByteArrayInputStream(imgBytes);
		String imageDir = imageBuilder.buildPostImageDir(2L);
		String imageName = imageBuilder.getName();
		ShrImage image = imageService.saveImage(imageDir, imageName, imageBuilder.getImageFormat(), inputStream);

		ShrFile original = image.getOriginal();
		ShrFile thumbnail = image.getThumbnail();
		assertEquals("png", thumbnail.getExtension());
	}

	@Test
	public void testDeleteImage() throws Exception {

	}

	@Test
	public void saveSampleFile() {
		imageService.saveSampleImage("post/", "test", "jpg", new File("E:/2.png"));
	}

}