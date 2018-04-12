package jp.bo.bocc.controller.api.request;

import jp.bo.bocc.entity.ShmAddr;
import jp.bo.bocc.entity.ShmPost;
import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShrFile;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by haipv on 3/14/2017.
 */
public class PostBodyRequest {

    @Getter @Setter
    private String postName;

    @Getter @Setter
    private String postDescription;

    @Getter @Setter
    private Long postCategoryId;

    @Getter @Setter
    private Long postPrice;

    @Getter @Setter
    private ShmPost.PostType postType;

    @Getter @Setter
    private Long postAddr;

    @Getter @Setter
    private  String postAddrTxt;

    @Getter @Setter
    private  List<String> imagesList;

    @Getter @Setter
    private  String postHashTagVal;

    @Getter @Setter
    private  ShmPost.PostSellSatus postSellSatus;

    @Getter @Setter
    private Long postId;

    @Getter @Setter
    private ShmAddr shmAddr;

    @Getter @Setter
    private ShmUser shmUser;

    @Getter @Setter
    private List<ShrFile> images;

    @Getter @Setter
    private Long postImportNo;

    @Getter @Setter
    private Long postUserId;

    @Getter @Setter
    private String postImageJson;

    @Getter @Setter
    private String destinationPublishType;
}
