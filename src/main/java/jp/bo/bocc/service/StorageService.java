package jp.bo.bocc.service;

import jp.bo.bocc.entity.ShrFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author manhnt
 */
public interface StorageService {

	InputStream getInputStream(ShrFile file);

	/**
	 * save file and set permission
	 * @param file File metadata
	 * @param in InputStream that contains the actual data
	 * @return file size in byte
	 */
	long setPermissionForFile(ShrFile file, InputStream in);

	long setPermissionForFileGIF(ShrFile file, MultipartFile mfile);

	boolean deleteFile(ShrFile file);

	String unzipFile(MultipartFile file, String destDirectory) throws IOException;

	List<InputStream> getImgImportInputStreams(Long postImportNo, String unzipDirName) throws FileNotFoundException;

	void deleteImgTempDirectory() throws IOException;

    List<File> getImgImportListFile(Long postImportNo, String unzipDirName);
}
