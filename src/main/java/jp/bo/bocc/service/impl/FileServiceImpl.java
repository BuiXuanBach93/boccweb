package jp.bo.bocc.service.impl;

import jp.bo.bocc.controller.api.request.PostBodyRequest;
import jp.bo.bocc.entity.ShmPost;
import jp.bo.bocc.entity.ShrFile;
import jp.bo.bocc.enums.ImageSavedEnum;
import jp.bo.bocc.helper.ImageBuilder;
import jp.bo.bocc.repository.FileRepository;
import jp.bo.bocc.service.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static jp.bo.bocc.helper.FileUtils.getAllImageIdByType;

/**
 * @author manhnt
 */

@Service("fileService")
@Transactional
@Repository
public class FileServiceImpl implements FileService {

	@Autowired
	private FileRepository repository;

	@Autowired
	UserService userService;

	@Autowired
	PostService postService;

	@Autowired
	ImageBuilder imageBuilder;

	@Autowired
	StorageService storageService;

	@Autowired
	TalkPurcMsgService talkPurcMsgService;

	@Autowired
	TalkQaService talkQaService;

	@Override
	public ShrFile save(ShrFile file) {
		return repository.save(file);
	}

	@Transactional(readOnly = true)
	@Override
	public ShrFile get(Long id) {
		return repository.findOne(id);
	}

	@Override
	public void delete(Long id) {
		repository.delete(id);
	}

	@Transactional(readOnly = true)
	@Override
	public ShrFile findByPath(String path) {

		File file = new File(path);
		String dir = file.getParentFile().getPath().replace('\\', '/') + "/";
		int index = file.getName().lastIndexOf('.');
		String fileName;
		String extension;

		if (index > -1) {
			fileName = file.getName().substring(0, index);
			extension = file.getName().substring(index + 1);
		} else {
			fileName = file.getName();
			extension = "";
		}

		return repository.findByDirAndNameAndExtension(dir, fileName, extension);
	}

	@Transactional(readOnly = true)
	@Override
	public List<ShrFile> buildListShrFile(String postImages, ImageSavedEnum imgType) {
		List<ShrFile> result = new ArrayList<>();
		final List<Long> allOriginalImageId = getAllImageIdByType(postImages, imgType);
		for (Long id : allOriginalImageId) {
			ShrFile file = repository.getOne(id);
			result.add(file);
		}
		return result;
	}

    @Override
    public void processImportSamplePost(MultipartFile multipartFile, String unzipDirName) throws Exception {
        try {
        	List<PostBodyRequest> shmPosts = new ArrayList<>();
			InputStream inputStream = multipartFile.getInputStream();
			Workbook workbook = WorkbookFactory.create(inputStream);
			Sheet worksheet = workbook.getSheetAt(0);
			int rowIndex = 1;
			while (rowIndex <= worksheet.getLastRowNum()) {
				PostBodyRequest shmPost = new PostBodyRequest();
				Row row = worksheet.getRow(rowIndex++);
				if (row.getCell(0) != null) {
					String value = getStringValueOfCell(row.getCell(0));
					shmPost.setPostImportNo(getLongVal(value));
				}
				if (row.getCell(1) != null) {
					String value = getStringValueOfCell(row.getCell(1));
					if(StringUtils.isEmpty(value)){
						continue;
					}
					shmPost.setPostUserId(getLongVal(value));
				}
				if(row.getCell(2) != null){
					String value = getStringValueOfCell(row.getCell(2));
					shmPost.setPostName(value);
				}
				if(row.getCell(3) != null){
					String value = getStringValueOfCell(row.getCell(3));
					shmPost.setPostDescription(value);
				}
				if (row.getCell(4) != null) {
					String value = getStringValueOfCell(row.getCell(4));
					shmPost.setPostCategoryId(getLongVal(value));
				}
				if (row.getCell(5) != null) {
					String value = getStringValueOfCell(row.getCell(5));
					shmPost.setPostPrice(getLongVal(value));
				}
				if (row.getCell(6) != null) {
					String value = getStringValueOfCell(row.getCell(6));
					if(ShmPost.PostType.BUY.ordinal() == getLongVal(value).intValue()){
						shmPost.setPostType(ShmPost.PostType.BUY);
					}else{
						shmPost.setPostType(ShmPost.PostType.SELL);
					}
				}
				if(row.getCell(7) != null){
					String value = getStringValueOfCell(row.getCell(7));
					shmPost.setPostImageJson(value);
				}
				if (row.getCell(8) != null) {
					String value = getStringValueOfCell(row.getCell(8));
					shmPost.setPostAddr(getLongVal(value));
				}
				shmPosts.add(shmPost);
			}

			// read done
			workbook.close();

			// insert post
			insertSamplePost(shmPosts,unzipDirName);

        } catch (IOException e) {
            throw e;
        }

    }

    @Override
    public String processUnzipImportPostImage(MultipartFile imgZipFile) {
		String unzipDirName =  "";
		String imageTmpDir = imageBuilder.getRootDir() + imageBuilder.getImgImportTempDir();
		try {
			unzipDirName = storageService.unzipFile(imgZipFile,imageTmpDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return unzipDirName;
	}

    private void insertSamplePost(List<PostBodyRequest> postBodyRequests, String unzipDirName) throws Exception {
		if(CollectionUtils.isEmpty(postBodyRequests)){
			return;
		}
		for (PostBodyRequest postBodyRequest: postBodyRequests) {
			try {
				if(!postService.createSamplePost(postBodyRequest,unzipDirName)){
					continue;
				}
			} catch (Exception e) {
				throw e;
			}
		}
	}

    private String getStringValueOfCell(Cell cell){
		String value = null;
		if(cell == null){
			return  value;
		}
		try{
			value = cell.getStringCellValue();
		}catch (Exception ex){
			try{
				value = (int)cell.getNumericCellValue() + "";
			}catch (Exception e){
				value = null;
			}
		}
		return value;
	}

	private Long getLongVal(String value){
    	Long longVal = 0L;
    	if(value != null){
    		try{
    			longVal = Long.parseLong(value);
			}catch (Exception ex){
    			return 0L;
			}
		}
		return longVal;
	}

}
