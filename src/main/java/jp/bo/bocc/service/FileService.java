package jp.bo.bocc.service;

import jp.bo.bocc.entity.ShrFile;
import jp.bo.bocc.enums.ImageSavedEnum;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author manhnt
 */
public interface FileService {

	ShrFile save(ShrFile file);

	ShrFile get(Long id);

	void delete(Long id);

	ShrFile findByPath(String path);

	/**
	 * Find lít ShrFile by list Id.
	 * @param postImages
	 * @param imgType
	 * @return
	 */
	List<ShrFile> buildListShrFile(String postImages, ImageSavedEnum imgType);

    void processImportSamplePost(MultipartFile multipartFile, String imgZipFileName) throws Exception;

    String processUnzipImportPostImage(MultipartFile imgZipFile);

}
