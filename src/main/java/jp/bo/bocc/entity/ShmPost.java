package jp.bo.bocc.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jp.bo.bocc.controller.api.response.UserReviewCount;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.*;

import javax.persistence.*;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Namlong on 3/14/2017.
 */
@Entity
@DynamicInsert @DynamicUpdate
@NoArgsConstructor @AllArgsConstructor
@Table(name = "SHM_POST")
@Where(clause = "CMN_DELETE_FLAG=0")
public class ShmPost extends BaseEntity {

    @Id
    @Column(name = "POST_ID")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SHM_POST_SEQ")
    @SequenceGenerator(name = "SHM_POST_SEQ", sequenceName = "SHM_POST_SEQ", allocationSize = 1, initialValue = 1)
    @Getter @Setter
    @Access(AccessType.PROPERTY)
    private Long postId;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "POST_USER_ID")
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter @Setter
    private ShmUser shmUser;

    @Getter @Setter
    @Column(name = "POST_NAME")
    private String postName;

    @Getter @Setter
    @Column(name = "POST_DESCRIPTION")
    private String postDescription;

    @Getter @Setter
    @JoinColumn(name = "POST_CATEGORY_ID")
    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action = NotFoundAction.IGNORE)
    private ShmCategory postCategory;

    @Getter @Setter
    @Transient
    private ShmCategory postCategoryParent;

    @Getter @Setter
    @Column(name = "POST_PRICE")
    private Long postPrice;

    @Getter @Setter
    @Column(name = "POST_LIKE_TIMES")
    private Long postLikeTimes;

    @Getter @Setter
    @Column(name = "POST_REPORT_TIMES")
    private Long postReportTimes;

    @Getter @Setter
    @Column(name = "POST_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private PostType postType;

    @Getter @Setter
    @Column(name = "POST_PARTNER_ID")
    private Long partnerId;

    @Getter @Setter
    @JsonIgnore
    @Column(name = "POST_IMAGES")
    private String postImages;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "POST_ADDR", nullable = false)
    @NotFound(action = NotFoundAction.IGNORE)
    @Getter @Setter
    private ShmAddr shmAddr;

    @Getter @Setter
    @Transient
    private ShmAddr shmAddrParent;

    @Getter @Setter
    @Column(name = "POST_ADDR_TXT")
    private String postAddrTxt;

    /**
     * 0 - Public, 1 - In-Conversation, 2 - Tend To Sell, 3 - Sold, 4 - Deleted
     */
    @Getter @Setter
    @Column(name = "POST_SELL_STATUS")
    @Enumerated(EnumType.ORDINAL)
    private PostSellSatus postSellStatus;

    /**
     * 0 - Active, 1 - Suspend, 2 - Reserved, 3-Update after censoring
     */
    @Getter @Setter
    @Column(name = "POST_CTRL_STATUS")
    @Enumerated(EnumType.ORDINAL)
    private PostCtrlStatus postCtrlStatus;

    /**
     * 0 - Uncensored, 1 - Censoring, 2 - Censored
     */
    @Getter @Setter
    @Column(name = "POST_PTRL_STATUS")
    @Enumerated(EnumType.ORDINAL)
    private PostPtrlStatusEnum postPtrlStatus;

    @Transient
    @Getter @Setter
    private ShtUserFvrt.UserFavoriteEnum currentUserFavoriteStatus;

    @Getter @Setter
    @Column(name = "GROUP_PUBLISH_ID")
    private Long groupId;

    public enum PostCtrlStatus {
        ACTIVE,
        SUSPENDED,
        RESERVED,
        UPDATE_AFTER_CENSORING
    }

    public enum PostPtrlStatusEnum {
        UNCENSORED,
        CENSORING,
        CENSORED
    }

    @Column(name = "POST_HASH_TAG_VAL")
    private String postHashTagVal;

    public String getPostHashTagVal() {
        if (postHashTagVal != null) {
            return postHashTagVal.replace(',', ' ');
        } return null;
    }

    public void setPostHashTagVal(String postHashTagVal) {
        this.postHashTagVal = postHashTagVal;
    }

    @Transient
    private Boolean isFirstTalkPurc;
    public Boolean isFirstTalkPurc(){return this.isFirstTalkPurc;}
    public void setIsFirstTalkPurc(Boolean isFirstTalkPurc){this.isFirstTalkPurc = isFirstTalkPurc;}

    @Transient
    private List<ShrFile> images;

    public List<ShrFile> getImages(){ return images;}
    public void setImages(List<ShrFile> images){
        this.images = images;
    }

    @Transient
    @Getter @Setter
    private List<ShrFile> postOriginalImages = new ArrayList<>();

    @Transient
    @Getter @Setter
    private List<ShrFile> postThumbnailImages = new ArrayList<>();

    @Transient
    @Getter @Setter
    private List<String> postOriginalImagePaths = new ArrayList<>();

    @Transient
    @Getter @Setter
    private List<String> postThumbnailImagePaths = new ArrayList<>();

    @Transient
    @Getter @Setter
    private Long talkPurcsNumber;

    @Transient
    @Getter @Setter
    private Boolean reportedByCurrentUser;

    @Transient
    @Getter @Setter
    private Boolean ownedByCurrentUser;

    @Getter @Setter
    @Transient
    private UserReviewCount userReviewCount;

    @Getter @Setter
    @Transient
    private Long talkPurcId;

    @Getter @Setter
    @Transient
    private int sequentNumber;

    @Getter @Setter
    @Transient
    private Long newMsgNumber;

    @Getter @Setter
    @Transient
    private ShmUser.CtrlStatus currentUserCtrlStatus;

    @Getter @Setter
    @Transient
    private ShmUser.Status currentUserStatus;

    public enum PostSellSatus{
        PUBLIC, IN_CONVERSATION, TEND_TO_SELL, SOLD, DELETED
    }

    public enum PostType{
        SELL, BUY
    }

    public enum DestinationPublishType {
        ALL, PRIVATE, GROUP
    }

    @Column(name = "POST_IS_IN_PATROL")
    @Getter @Setter
    private Boolean isInPatrol;

    @Column(name = "POST_TIME_PATROL")
    @Getter @Setter
    private LocalDateTime timePatrol;

    @Override
    public void onPrePersist() {
        setPostReportTimes(0L);
        setPostLikeTimes(0L);
        setPostSellStatus(PostSellSatus.PUBLIC);
        setPostCtrlStatus(PostCtrlStatus.ACTIVE);
        setPostPtrlStatus(PostPtrlStatusEnum.UNCENSORED);
        setUserUpdateAt(LocalDateTime.now());
    }

    @Getter @Setter
    @Column(name = "DESTINATION_PUBLISH_TYPE")
    @Enumerated(EnumType.ORDINAL)
    private DestinationPublishType destinationPublishType;

    @Column(name = "USER_UPDATE_AT")
    @Getter @Setter
    private LocalDateTime userUpdateAt;
}
