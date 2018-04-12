package jp.bo.bocc.helper;

import com.opencsv.CSVReader;
import jp.bo.bocc.entity.ShrFile;
import jp.bo.bocc.entity.ShrImage;
import jp.bo.bocc.entity.dto.PostUploadCsvDTO;
import jp.bo.bocc.enums.ImageSavedEnum;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static jp.bo.bocc.enums.ImageSavedEnum.ORIGINAL;
import static jp.bo.bocc.enums.ImageSavedEnum.THUMBNAIL;

/**
 * Created by Namlong on 3/30/2017.
 */
public class FileUtils {

    /**
     * build to list imageId.
     *
     * @param shrImages
     * @return
     */
    public static String buildImageJson(List<ShrImage> shrImages) {
        if(shrImages == null || shrImages.size() == 0){
            return "[]";
        }
        JSONObject jsonObject = new JSONObject();
        List<String> stringList = new ArrayList<String>();
        shrImages.stream().forEach(item -> {
                    jsonObject.put(ORIGINAL.key(), item.getOriginal().getId());
                    jsonObject.put(THUMBNAIL.key(), item.getThumbnail().getId());
                    stringList.add(jsonObject.toString());
                }
        );

        return "[" + String.join(",", stringList) + "]";
    }

    /**
     * get id image.
     *
     * @param imagePaths
     * @param index
     * @param imageSavedEnum
     * @return
     */
    public static Long getImageId(String imagePaths, int index, ImageSavedEnum imageSavedEnum) {
        if(org.apache.commons.lang3.StringUtils.isEmpty(imagePaths)){
            return null;
        }
        JSONArray jsonArray = new JSONArray(imagePaths);
        jsonArray.getJSONObject(index).getLong(imageSavedEnum.key());
        return jsonArray.getJSONObject(index).getLong(imageSavedEnum.key());
    }

    /**
     * get path file for Post
     * @param shrFile
     * @return
     */
    public static String buildImagePathByFileForPost(ShrFile shrFile) {
        if (shrFile != null) {
            StringBuilder result = new StringBuilder();
            result.append(shrFile.getDir()).append(shrFile.getName()).append(".").append(shrFile.getExtension());
            return result.toString();
        }
        return null;
    }

    /**
     * get path file for Post
     * @param shrFile
     * @return
     */
    public static String buildImagePathByFileForUserAvatar(ShrFile shrFile) {
        if (shrFile != null) {
            StringBuilder result = new StringBuilder();
            result.append(shrFile.getDir()).append(shrFile.getName()).append(".").append(shrFile.getExtension());
            return result.toString();
        }
        return null;
    }



    /**
     * get image ids.
     * @param imagePaths
     * @param imgType
     * @return
     */
    public static List<Long> getAllImageIdByType(String imagePaths, ImageSavedEnum imgType) {
        if(imagePaths == null || imagePaths.equals("null")){
            return new ArrayList<>();
        }
        List<Long> result = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(imagePaths);
        for (int i = 0; i < jsonArray.length(); i++) {
            if (imgType == ImageSavedEnum.ORIGINAL) {
                result.add(jsonArray.getJSONObject(i).getLong(ImageSavedEnum.ORIGINAL.key()));
            } else {
                result.add(jsonArray.getJSONObject(i).getLong(ImageSavedEnum.THUMBNAIL.key()));
            }
        }
        return result;
    }

    /**
     * get all parent folder's file
     * @param javaFile
     * @param result
     * @return
     */
    public static List<String> getAllParentFolder(File javaFile, List<String> result, String configFolder) {
        File parent = javaFile.getParentFile();
        if (parent != null && parent.isDirectory() && configFolder.equals(parent.getName())) {
            return result;
        }else if (parent != null){
            result.add(parent.getPath());
            getAllParentFolder(parent, result, configFolder);
        }
        return result;
    }


    public static List<PostUploadCsvDTO> readXLSX(MultipartFile csvFile) throws IOException {
        List<PostUploadCsvDTO> result = new ArrayList<PostUploadCsvDTO>();
        File file = convertMultipartFileToFile(csvFile);
        FileInputStream fis = new FileInputStream(file);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fis));
        CSVReader csvReader = null;
        try {
            csvReader = new CSVReader(bufferedReader);
            String[] line;
            PostUploadCsvDTO postCsvDTO = null;
            while ((line = csvReader.readNext()) != null) {
                postCsvDTO = buildPostDtoFromCsv(line);
                result.add(postCsvDTO);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static PostUploadCsvDTO buildPostDtoFromCsv(String[] line) {
        PostUploadCsvDTO result = new PostUploadCsvDTO();
        result.setPostUserId(ConverterUtils.convertLongFromString(line[1]));
        result.setPostName(line[2]);
        result.setPostDescription(line[3]);
        result.setPostCategoryId(ConverterUtils.convertLongFromString(line[4]));
        result.setPostPrice(ConverterUtils.convertLongFromString(line[5]));
        result.setPostType(ConverterUtils.convertLongFromString(line[6]));
        result.setPostImages(line[7]);
        result.setPostAddr(ConverterUtils.convertLongFromString(line[8]));
        return null;
    }

    private static File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fos = new FileOutputStream(convFile);
        fos.write(file.getBytes());
        fos.close();
        return convFile;
    }

    public static String buildImagePathByFileForTalkPurcChat(ShrFile shrFile) {
        if (shrFile != null) {
            StringBuilder result = new StringBuilder();
            result.append(shrFile.getDir()).append(shrFile.getName()).append(".").append(shrFile.getExtension());
            return result.toString();
        }
        return null;
    }
}
