package jp.bo.bocc.entity.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by Namlong on 6/23/2017.
 */
public class PostUploadCsvDTO {
    @Getter @Setter
    long postUserId;
    @Getter @Setter
    String postName;
    @Getter @Setter
    String postDescription;
    @Getter @Setter
    long postCategoryId;
    @Getter @Setter
    long postPrice;
    @Getter @Setter
    long postType;
    @Getter @Setter
    String postImages;
    @Getter @Setter
    long postAddr;
}
