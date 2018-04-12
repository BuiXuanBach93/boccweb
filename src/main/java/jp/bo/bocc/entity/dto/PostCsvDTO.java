package jp.bo.bocc.entity.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * @author NguyenThuong on 6/5/2017.
 */
public class PostCsvDTO {

    @Getter @Setter
    private long postId;

    @Getter @Setter
    private String postName;

    @Getter @Setter
    private long userId;

    @Getter @Setter
    private String userName;

    @Getter @Setter
    private String postType;

    @Getter @Setter
    private String catChild;

    @Getter @Setter
    private String catParent;

    @Getter @Setter
    private String status;

    @Getter @Setter
    private LocalDateTime createdAt;

    @Getter @Setter
    private LocalDateTime updatedAt;

    @Getter @Setter
    private String csvCreatedAt;

    @Getter @Setter
    private String csvUpdatedAt;

    @Getter @Setter
    private long price;

    @Getter @Setter
    private long reportTime;

    public enum POST_SELL_TYPE {
        公開中,
        取引中,
        取引意思確認中,
        取引完了,
        削除済
    }
}
