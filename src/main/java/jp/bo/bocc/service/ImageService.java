package jp.bo.bocc.service;

import jp.bo.bocc.entity.ShrImage;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * Save image by creating 2 separated files (one for the original and another for thumbnail)
 * @author manhnt
 */
public interface ImageService {

	ShrImage findImageFromFileId(Long id);

	ShrImage saveImage(String dir, String originalFileName, String extension, InputStream in);

	ShrImage saveBannerImage(String dir, String originalFileName, String extension, MultipartFile file);

	ShrImage saveSampleImage(String dir, String originalFileName, String extension, File in);

	void deleteImage(ShrImage image);

	String getThumbDir();

	int getThumbWidth();

	int getThumbHeight();

	/**
	 * return list image link delimited by comma.
	 * @param productImgList
	 * @return
	 */
    String saveImageInChat(List<String> productImgList, Long talkPurcId);
}
