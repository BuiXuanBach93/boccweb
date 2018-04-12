package jp.bo.bocc.entity.dto;

import jp.bo.bocc.entity.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ShmPostDTO extends BaseDTO {

  @Getter @Setter
  private Long postId;

  @Getter @Setter
  private Long shmUserIdDTO;

  @Getter @Setter
  private String postName;

  @Getter @Setter
  private String postDescription;

  @Getter @Setter
  private ShmCategory postCategory;

  @Getter @Setter
  private Long postCategoryId;

  @Getter @Setter
  private Long postPrice;

  @Getter @Setter
  private Long postLikeTimes;

  @Getter @Setter
  private Long postReportTimes;

  @Getter @Setter
  private ShmPost.PostType postType;

  @Getter @Setter
  private List<ShrFile> postOriginalImages = new ArrayList<ShrFile>();

  @Getter @Setter
  private List<ShrFile> postThumbnailImages = new ArrayList<ShrFile>();

  @Getter @Setter
  private List<String> postOriginalImagePaths = new ArrayList<>();

  @Getter @Setter
  private List<String> postThumbnailImagePaths = new ArrayList<>();

  @Getter @Setter
  private String postImages;

  @Getter @Setter
  private Long shmAddrIdDTO;

  @Getter @Setter
  private String postAddrTxt;

  @Getter @Setter
  private ShmPost.PostSellSatus postSellStatus;

  @Getter @Setter
  private ShmPost.PostCtrlStatus postCtrlStatus;

  @Getter @Setter
  private ShmPost.PostPtrlStatusEnum postPtrlStatus;

  @Getter @Setter
  private String ctrlStatus;

  @Getter @Setter
  private String ptrlStatus;

  @Getter @Setter
  private String adminName;

  @Getter @Setter
  private long maxRecord;

  @Getter @Setter
  private String userNickName;

  @Getter @Setter
  private ShtUserFvrt.UserFavoriteEnum currentUserFavoriteStatus;

  @Getter @Setter
  private String postHashTagVal;

  @Getter @Setter
  private Boolean ownedByCurrentUser;

  @Getter @Setter
  private int totalNewMsgInTalkPurcForPost;

  @Getter @Setter
  private Long talkPurcIdOfCurrentUser;

  @Getter @Setter
  private ShmUser.Status ownerStatus;

  @Getter @Setter
  private ShmUser.CtrlStatus ownerCtrlStatus;

  @Getter @Setter
  private String lastReportUser;

  @Getter @Setter
  private LocalDateTime timePatrol;

  @Getter @Setter
  private LocalDateTime userUpdatedAt;

  @Getter @Setter
  private Long postAddrId;

  @Getter @Setter
  private String pendingInPast;

  @Getter @Setter
  private String suspendInPast;

  @Getter @Setter
  private Long ownerPostAddressDistrictId;

  @Getter @Setter
  private String ownerPostAddressDistrictTxt;

  @Getter @Setter
  private String ownerPostAddressDistrictCode;

  @Getter @Setter
  private Long ownerPostAddressProvinceId;

  @Getter @Setter
  private String ownerPostAddressProvinceTxt;

  @Getter @Setter
  private String ownerPostAddressProvinceCode;

  @Getter @Setter
  private String destinationPublishType;
}
