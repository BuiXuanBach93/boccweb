package jp.bo.bocc.service.impl;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.jpeg.JpegDirectory;
import jp.bo.bocc.entity.ShrFile;
import jp.bo.bocc.entity.ShrImage;
import jp.bo.bocc.enums.ImageEnum;
import jp.bo.bocc.helper.FileUtils;
import jp.bo.bocc.helper.ImageBuilder;
import jp.bo.bocc.helper.ImageInformation;
import jp.bo.bocc.helper.StringUtils;
import jp.bo.bocc.service.FileService;
import jp.bo.bocc.service.ImageService;
import jp.bo.bocc.service.StorageService;
import jp.bo.bocc.system.exception.InternalServerErrorException;
import jp.bo.bocc.system.exception.ResourceNotFoundException;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * @author manhnt
 */

@Service("imageService")
@Transactional
public class ImageServiceImpl implements ImageService {

	private final static Logger LOGGER = Logger.getLogger(ImageServiceImpl.class.getName());

	public static final String BANNER_IMAGE_EXTENSION = "png";

	@Autowired
	ImageBuilder imageBuilder;

	@Autowired
	private FileService fileService;

	@Autowired
	private StorageService storageService;

	@Autowired
	private MessageSource messageSource;

	@Value("${image.server.url}")
	private String imgServer;

	@Override
	public ShrImage findImageFromFileId(Long id) {

		ShrFile imageFile = fileService.get(id);

		if (imageFile == null) throw new ResourceNotFoundException(messageSource.getMessage("SH_E100123", null, null));

		if (imageFile.getDir().contains(imageBuilder.getThumbDir())) {
			//Find original image
			ShrFile orgImageFile = fileService.findByPath(imageFile.getPath().replace(imageBuilder.getThumbDir(), ""));
			return new ShrImage(orgImageFile, imageFile);
		} else {
			//Find thumbnail image
			ShrFile thumbnailFile = fileService.findByPath(imageFile.getDir() + imageBuilder.getThumbDir() + imageFile.getName() + "." + imageFile.getExtension());
			return new ShrImage(imageFile, thumbnailFile);
		}
	}

	@Override
	public ShrImage saveImage(String dir, String originalFileName, String extension, InputStream in) {
		//Create Image
		try {
			ShrFile orgFile = new ShrFile();
			orgFile.setOriginalName(originalFileName);
			String fileName = StringUtils.generateUniqueToken();
			orgFile.setName(fileName);
			orgFile.setExtension(extension);
			orgFile.setDir(dir);

			ShrFile thumbFile = new ShrFile();
			thumbFile.setOriginalName(originalFileName);
			thumbFile.setName(fileName);
			thumbFile.setExtension(extension);
			thumbFile.setDir(dir + imageBuilder.getThumbDir());

			return saveImage(new ShrImage(orgFile, thumbFile), in);

		} catch (NoSuchAlgorithmException e) {
			throw new InternalServerErrorException("", e);
		}
	}

	@Override
	public ShrImage saveSampleImage(String dir, String originalFileName, String extension, File in) {
		try {
			ShrFile orgFile = new ShrFile();
			orgFile.setOriginalName(originalFileName);
			String fileName = StringUtils.generateUniqueToken();
			orgFile.setName(fileName);
			orgFile.setExtension(extension);
			orgFile.setDir(dir);

			ShrFile thumbFile = new ShrFile();
			thumbFile.setOriginalName(originalFileName);
			thumbFile.setName(fileName);
			thumbFile.setExtension(extension);
			thumbFile.setDir(dir + imageBuilder.getThumbDir());

			return saveSampleImage(new ShrImage(orgFile, thumbFile), in);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public ShrImage saveBannerImage(String dir, String bannerName, String extension, MultipartFile file){
		try {
			ShrFile orgFile = new ShrFile();
			orgFile.setOriginalName(bannerName);
			String fileName = StringUtils.generateUniqueToken();
			orgFile.setName(fileName);
			orgFile.setExtension(extension);
			orgFile.setDir(dir);

			ShrImage bannerImage = new ShrImage();
			bannerImage.setOriginal(orgFile);
			return saveBannerImage(bannerImage, file);

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	private ShrImage saveBannerImage(ShrImage image, MultipartFile file) {

		ShrFile inputOrg = image.getOriginal();
		try {

			BufferedImage orgImage = ImageIO.read(file.getInputStream());
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			//Store original
			ImageIO.write(orgImage, image.getOriginal().getExtension(), out);
			long orgSize = 0L;
			if(image.getOriginal().getExtension() != null && image.getOriginal().getExtension().equals(ImageEnum.GIF.value)){
				orgSize = storageService.setPermissionForFileGIF(inputOrg, file);
			}else {
				orgSize = storageService.setPermissionForFile(inputOrg, new ByteArrayInputStream(out.toByteArray()));
			}

			//Update file size, width, height+
			inputOrg.setSize(orgSize);
			inputOrg.setWidth(orgImage.getWidth());
			inputOrg.setHeight(orgImage.getHeight());

			ShrFile orgFile = fileService.save(inputOrg);

			return new ShrImage(orgFile, null);
		} catch (Exception e) {
			//Do rollback
			storageService.deleteFile(inputOrg);
			throw new InternalServerErrorException("", e);
		}
	}

	private ShrImage saveSampleImage(ShrImage image, File in) {

		ShrFile inputOrg = image.getOriginal();
		ShrFile inputThumbnail = image.getThumbnail();
		try {
			BufferedImage orgImage = ImageIO.read(in);
			BufferedImage thumbnailImage = Thumbnails.of(orgImage).size(imageBuilder.getThumbWidth(), imageBuilder.getThumbHeight()).asBufferedImage();

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			//Store original
			ImageIO.write(orgImage, image.getOriginal().getExtension(), out);
			long orgSize = storageService.setPermissionForFile(inputOrg, new ByteArrayInputStream(out.toByteArray()));
			//Update file size, width, height
			inputOrg.setSize(orgSize);
			inputOrg.setWidth(orgImage.getWidth());
			inputOrg.setHeight(orgImage.getHeight());

			//Reset the stream
			out.reset();

			//Store thumbnail
			ImageIO.write(thumbnailImage, image.getThumbnail().getExtension(), out);
			long thumbSize = storageService.setPermissionForFile(inputThumbnail, new ByteArrayInputStream(out.toByteArray()));
			//Update file size, width, height
			inputThumbnail.setSize(thumbSize);
			inputThumbnail.setWidth(thumbnailImage.getWidth());
			inputThumbnail.setHeight(thumbnailImage.getHeight());

			ShrFile orgFile = fileService.save(inputOrg);
			ShrFile thmbFile = fileService.save(inputThumbnail);

			return new ShrImage(orgFile, thmbFile);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private File buildImagePathByFileForPost(ShrFile inputOrg) throws IOException {
		String filePath = imageBuilder.getRootDir() + FileUtils.buildImagePathByFileForPost(inputOrg);
		File file = new File(filePath);
		return file;
	}

	private ImageInformation readImageInformation(File in) throws ImageProcessingException, IOException {
		int orientation = 0;
		int width = 0;
		int height = 0;
		try {
			final Metadata metadata = ImageMetadataReader.readMetadata(in);
			com.drew.metadata.Directory directory = metadata.getFirstDirectoryOfType(ExifIFD0Directory.class);
			JpegDirectory jpegDirectory = metadata.getFirstDirectoryOfType(JpegDirectory.class);

			orientation = 1;
			try {
                orientation = directory.getInt(ExifIFD0Directory.TAG_ORIENTATION);
            } catch (MetadataException me) {
                me.printStackTrace();
            }
			width = jpegDirectory.getImageWidth();
			height = jpegDirectory.getImageHeight();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new ImageInformation(orientation, width, height);
	}

	private BufferedImage transformImage(BufferedImage image, AffineTransform atf) {
		AffineTransformOp op = new AffineTransformOp(atf, AffineTransformOp.TYPE_BICUBIC);

		final ColorModel destCM = (image.getType() == BufferedImage.TYPE_BYTE_GRAY) ? image.getColorModel() : null;
		BufferedImage destinationImage = op.createCompatibleDestImage(image, destCM);
		Graphics2D g = destinationImage.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.00f));
		g.setBackground(Color.WHITE);
		g.clearRect(0, 0, destinationImage.getWidth(), destinationImage.getHeight());
		destinationImage = op.filter(image, destinationImage);
		return destinationImage;
	}

	private AffineTransform getExifTransformation(ImageInformation info) {
		AffineTransform t = new AffineTransform();

		switch (info.orientation) {
			case 1:
				break;
			case 2: // Flip X
				t.scale(-1.0, 1.0);
				t.translate(-info.width, 0);
				break;
			case 3: // PI rotation
				t.translate(info.width, info.height);
				t.rotate(Math.PI);
				break;
			case 4: // Flip Y
				t.scale(1.0, -1.0);
				t.translate(0, -info.height);
				break;
			case 5: // - PI/2 and Flip X
				t.rotate(-Math.PI / 2);
				t.scale(-1.0, 1.0);
				break;
			case 6: // -PI/2 and -width
				t.translate(info.height, 0);
				t.rotate(Math.PI / 2);
				break;
			case 7: // PI/2 and Flip
				t.scale(-1.0, 1.0);
				t.translate(-info.height, 0);
				t.translate(0, info.width);
				t.rotate(  3 * Math.PI / 2);
				break;
			case 8: // PI / 2
				t.translate(0, info.width);
				t.rotate(  3 * Math.PI / 2);
				break;
		}

		return t;
	}

	private ShrImage saveImage(ShrImage image, InputStream in) {

		ShrFile inputOrg = image.getOriginal();
		ShrFile inputThumbnail = image.getThumbnail();
		try {

			BufferedImage orgImage = ImageIO.read(in);
			BufferedImage thumbnailImage = Thumbnails.of(orgImage).size(imageBuilder.getThumbWidth(), imageBuilder.getThumbHeight()).asBufferedImage();

			ByteArrayOutputStream out = new ByteArrayOutputStream();

			//Store original
			ImageIO.write(orgImage, image.getOriginal().getExtension(), out);
			long orgSize = storageService.setPermissionForFile(inputOrg, new ByteArrayInputStream(out.toByteArray()));
			//Update file size, width, height
			inputOrg.setSize(orgSize);
			inputOrg.setWidth(orgImage.getWidth());
			inputOrg.setHeight(orgImage.getHeight());

			//Reset the stream
			out.reset();

			//Store thumbnail
			ImageIO.write(thumbnailImage, image.getThumbnail().getExtension(), out);
			long thumbSize = storageService.setPermissionForFile(inputThumbnail, new ByteArrayInputStream(out.toByteArray()));
			//Update file size, width, height
			inputThumbnail.setSize(thumbSize);
			inputThumbnail.setWidth(thumbnailImage.getWidth());
			inputThumbnail.setHeight(thumbnailImage.getHeight());

			ShrFile orgFile = fileService.save(inputOrg);
			ShrFile thmbFile = fileService.save(inputThumbnail);

			return new ShrImage(orgFile, thmbFile);
		} catch (Exception e) {
			//Do rollback
			storageService.deleteFile(inputOrg);
			storageService.deleteFile(inputThumbnail);
			e.printStackTrace();
			throw new InternalServerErrorException("画像をアップロードする時にエラーが発生していますので再度試してください", e);
		}
	}

	@Override
	public void deleteImage(ShrImage image) {
		fileService.delete(image.getOriginal().getId());
		fileService.delete(image.getThumbnail().getId());

		storageService.deleteFile(image.getOriginal());
		storageService.deleteFile(image.getThumbnail());
	}

	@Override
	public String getThumbDir() {
		return imageBuilder.getThumbDir();
	}

	@Override
	public int getThumbWidth() {
		return imageBuilder.getThumbWidth();
	}

	@Override
	public int getThumbHeight() {
		return imageBuilder.getThumbHeight();
	}

	@Override
	public String saveImageInChat(List<String> productImgList, Long talkPurcId) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < productImgList.size();i++) {
			String imageStrBase64 = productImgList.get(i);
			String imageDir = imageBuilder.buildPostImageDir(talkPurcId);
			String imageName = imageBuilder.getName();
			byte[] imgBytes = jp.bo.bocc.helper.StringUtils.base64Decode(imageStrBase64);
			InputStream inputStream = new ByteArrayInputStream(imgBytes);
			ShrImage image = saveImage(imageDir, imageName, imageBuilder.getImageFormat(), inputStream);
			String imageLink = imgServer + FileUtils.buildImagePathByFileForTalkPurcChat(image.getOriginal());
			if (i == productImgList.size()-1)
				sb.append(imageLink);
			else
				sb.append(imageLink+",");

		}
		return sb.toString();
	}
}
