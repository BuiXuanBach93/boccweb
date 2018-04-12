package jp.bo.bocc.controller.api.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by DonBach on 3/27/2017.
 */
public class PostBodyEdit {
    @Getter @Setter
    private String postName;

    @Getter @Setter
    private String postDescription;

    @Getter @Setter
    private String postHashTagVal;

    @Getter @Setter
    private Long postPrice;

    @Getter @Setter
    private Long postCategoryId;

    @Getter @Setter
    private Long postAddr;

    @Getter @Setter
    private List<String> postRemoveImagesUrl;

    @Getter @Setter
    private List<String> postAddImagesBase64;

    @Getter @Setter
    private String destinationPublishType;
}
