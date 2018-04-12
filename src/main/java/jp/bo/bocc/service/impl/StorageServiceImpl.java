package jp.bo.bocc.service.impl;

import jp.bo.bocc.entity.ShrFile;
import jp.bo.bocc.enums.ImageEnum;
import jp.bo.bocc.helper.ImageBuilder;
import jp.bo.bocc.service.StorageService;
import jp.bo.bocc.system.exception.InternalServerErrorException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static jp.bo.bocc.helper.FileUtils.getAllParentFolder;

/**
 * @author manhnt
 */
@Service("storageService")
@Transactional
public class StorageServiceImpl implements StorageService {

	private final static Logger LOGGER = Logger.getLogger(StorageServiceImpl.class.getName());

	@Autowired
	ImageBuilder imageBuilder;

	@Autowired
	MessageSource messageSource;

	private static final int BUFFER_SIZE = 4096;

	@Value("${image.root}")
	String configFolder;

	private static final String STORAGE_ROOT = "target/storage/"; //FIXME: Use configuration file instead

	@Override
	public InputStream getInputStream(ShrFile file) {
		try {
			return new FileInputStream(getRealPath(file));
		} catch (FileNotFoundException e) {
			throw new InternalServerErrorException("File not found", e);
		}
	}

	@Override
	public long setPermissionForFile(ShrFile file, InputStream in) {
		try {

			LOGGER.info("### TEST_PNG : StorageServiceImpl line 68");
			File javaFile = new File(getRealPath(file));

			if (!javaFile.exists() && !javaFile.getParentFile().mkdirs() && !javaFile.createNewFile()) {
				LOGGER.info("### TEST_PNG : StorageServiceImpl line 72");
				throw new InternalServerErrorException(messageSource.getMessage("SH_E100121", null, null));
			}

			LOGGER.info("### TEST_PNG : StorageServiceImpl line 74");

			FileOutputStream fileOutputStream = new FileOutputStream(javaFile);

			FileChannel fileChannel = fileOutputStream.getChannel();
			IOUtils.copy(in, fileOutputStream);
			long fileSize = fileChannel.position();

			LOGGER.info("### TEST_PNG : StorageServiceImpl line 82");

			List<String> listParentFolder = new ArrayList<>();
			final String[] split = configFolder.split("/");
			String folderName = "";
			if (split != null && split.length > 0)
				folderName = split[split.length - 1];
			listParentFolder = getAllParentFolder(javaFile, listParentFolder, folderName);

			LOGGER.info("### TEST_PNG : StorageServiceImpl line 91");

			Set<PosixFilePermission> permsFolder = new HashSet<PosixFilePermission>();
			permsFolder.add(PosixFilePermission.OWNER_READ);
			permsFolder.add(PosixFilePermission.OWNER_WRITE);
			permsFolder.add(PosixFilePermission.OWNER_EXECUTE);
			permsFolder.add(PosixFilePermission.GROUP_READ);
			permsFolder.add(PosixFilePermission.GROUP_EXECUTE);
			permsFolder.add(PosixFilePermission.OTHERS_READ);
			permsFolder.add(PosixFilePermission.OTHERS_EXECUTE);

			LOGGER.info("### TEST_PNG : StorageServiceImpl line 102");

			listParentFolder.stream().forEach(item -> {
				try {
					Files.setPosixFilePermissions(Paths.get(item), permsFolder);
				} catch (Exception e) {
					LOGGER.info("### TEST_PNG : StorageServiceImpl line 108");
					e.printStackTrace();
				}
			});

			LOGGER.info("### TEST_PNG : StorageServiceImpl line 113");

			Set<PosixFilePermission> permsFile = new HashSet<PosixFilePermission>();
			permsFile.add(PosixFilePermission.OWNER_READ);
			permsFile.add(PosixFilePermission.OWNER_WRITE);
			permsFile.add(PosixFilePermission.GROUP_READ);
			permsFile.add(PosixFilePermission.OTHERS_READ);
			Files.setPosixFilePermissions(Paths.get(javaFile.getPath()), permsFile);

			LOGGER.info("### TEST_PNG : StorageServiceImpl line 122");

			fileOutputStream.close();

			return fileSize;

		} catch (Exception e) {
			LOGGER.info("### TEST_PNG : StorageServiceImpl line 129");
			throw new InternalServerErrorException(messageSource.getMessage("SH_E100122", null, null), e);
		}
	}

	@Override
	public long setPermissionForFileGIF(ShrFile file, MultipartFile mfile) {
		try {

			File javaFile = new File(getRealPath(file));

			if (!javaFile.exists() && !javaFile.getParentFile().mkdirs() && !javaFile.createNewFile()) {
				throw new InternalServerErrorException(messageSource.getMessage("SH_E100121", null, null));
			}

			FileOutputStream fileOutputStream = new FileOutputStream(javaFile);
			FileChannel fileChannel = fileOutputStream.getChannel();
			fileOutputStream.write(mfile.getBytes());
			long fileSize = fileChannel.position();

			List<String> listParentFolder = new ArrayList<>();
			final String[] split = configFolder.split("/");
			String folderName = "";
			if (split != null && split.length > 0)
				folderName = split[split.length - 1];
			listParentFolder = getAllParentFolder(javaFile, listParentFolder, folderName);

			Set<PosixFilePermission> permsFolder = new HashSet<PosixFilePermission>();
			permsFolder.add(PosixFilePermission.OWNER_READ);
			permsFolder.add(PosixFilePermission.OWNER_WRITE);
			permsFolder.add(PosixFilePermission.OWNER_EXECUTE);
			permsFolder.add(PosixFilePermission.GROUP_READ);
			permsFolder.add(PosixFilePermission.GROUP_EXECUTE);
			permsFolder.add(PosixFilePermission.OTHERS_READ);
			permsFolder.add(PosixFilePermission.OTHERS_EXECUTE);

			listParentFolder.stream().forEach(item -> {
				try {
					Files.setPosixFilePermissions(Paths.get(item), permsFolder);
				} catch (IOException e) {
					e.printStackTrace();
				}
			});

			Set<PosixFilePermission> permsFile = new HashSet<PosixFilePermission>();
			permsFile.add(PosixFilePermission.OWNER_READ);
			permsFile.add(PosixFilePermission.OWNER_WRITE);
			permsFile.add(PosixFilePermission.GROUP_READ);
			permsFile.add(PosixFilePermission.OTHERS_READ);
			Files.setPosixFilePermissions(Paths.get(javaFile.getPath()), permsFile);

			fileOutputStream.close();

			return fileSize;

		} catch (IOException e) {
			throw new InternalServerErrorException(messageSource.getMessage("SH_E100122", null, null), e);
		}
	}

	@Override
	public boolean deleteFile(ShrFile file) {
		java.io.File javaFile = new java.io.File(getRealPath(file));
		return javaFile.delete();
	}

	private String getRealPath(ShrFile file) {
		return imageBuilder.getRootDir() + file.getPath();
	}


	public List<InputStream> getImgImportInputStreams(Long postImportNo, String unzipDirName) throws FileNotFoundException {
		List<InputStream> imgInputStreams = new ArrayList<>();
		String postImgPath = imageBuilder.getRootDir()+imageBuilder.getImgImportTempDir()+"/"+unzipDirName;
		File unzipDir = new File(postImgPath);
		File[] directoryListing = unzipDir.listFiles();
		if (directoryListing != null) {
			for (File child : directoryListing) {
				if(child.isDirectory() && child.getName().equals(postImportNo.toString())){
					File postDir = new File(child.getPath());
					File[] postFileListing = postDir.listFiles();
					if (postFileListing != null) {
						for (File imgFile : postFileListing) {
							if(imgFile.isFile()){
								InputStream targetStream = new FileInputStream(imgFile);
								imgInputStreams.add(targetStream);
								if(CollectionUtils.isNotEmpty(imgInputStreams) && imgInputStreams.size() >= 4){
									break;
								}
							}
						}
					}
				}
			}
		}
		return imgInputStreams;
	}

	public String unzipFile(MultipartFile file, String destDirectory) throws IOException {
		String unzipDirName = "";
		File destDir = new File(destDirectory);
		destDir.setWritable(true);
		if (!destDir.exists()) {
			destDir.mkdir();
		}
		ZipInputStream zipIn = new ZipInputStream(file.getInputStream());
		ZipEntry entry = zipIn.getNextEntry();
		unzipDirName = entry.getName();
		while (entry != null) {
			String filePath = destDirectory + File.separator + entry.getName();
			if (!entry.isDirectory()) {
				extractFile(zipIn, filePath);
			} else {
				File dir = new File(filePath);
				dir.setWritable(true);
				dir.mkdir();
			}
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		zipIn.close();
		return unzipDirName;
	}

	private void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
		byte[] bytesIn = new byte[BUFFER_SIZE];
		int read = 0;
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();
	}

	public void deleteImgTempDirectory() throws IOException {
		String postImgPath = imageBuilder.getRootDir()+imageBuilder.getImgImportTempDir();
		File dir = new File(postImgPath);
		if(dir.exists()){
			File[] directoryListing = dir.listFiles();
			if (directoryListing != null) {
				for (File child : directoryListing) {
					if(child.isDirectory())
					FileUtils.deleteDirectory(child);
				}
			}
		}
	}

	@Override
	public List<File> getImgImportListFile(Long postImportNo, String unzipDirName) {
		List<File> imgInputStreams = new ArrayList<>();
		String postImgPath = imageBuilder.getRootDir()+imageBuilder.getImgImportTempDir()+"/"+unzipDirName;
		File unzipDir = new File(postImgPath);
		File[] directoryListing = unzipDir.listFiles();
		if (directoryListing != null) {
			for (File child : directoryListing) {
				if(child.isDirectory() && child.getName().equals(postImportNo.toString())){
					File postDir = new File(child.getPath());
					File[] postFileListing = postDir.listFiles();
					if (postFileListing != null) {
						for (File imgFile : postFileListing) {
							if(imgFile.isFile()){
								imgInputStreams.add(imgFile);
								if(CollectionUtils.isNotEmpty(imgInputStreams) && imgInputStreams.size() >= 4){
									break;
								}
							}
						}
					}
				}
			}
		}
		return imgInputStreams;
	}
}
