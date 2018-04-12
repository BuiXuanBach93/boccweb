package jp.bo.bocc.entity.dto;


import jp.bo.bocc.entity.ShmAddr;
import jp.bo.bocc.entity.ShmUser;
import jp.bo.bocc.entity.ShrFile;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

public class ShmUserForViewDTO extends BaseDTO {

  @Getter @Setter
  private Long userId;

  @Getter @Setter
  private String userFirstName;

  @Getter @Setter
  private String userLastName;

  @Getter @Setter
  private String userNickName;

  @Getter @Setter
  private String userPhone;

  @Getter @Setter
  private ShmUser.Gender userGender;

  @Getter @Setter
  private LocalDate userDateOfBirth;

  @Getter @Setter
  private Long userAddressId;

  @Getter @Setter
  private String userDescr;

  @Getter @Setter
  private Long userAvtr;

  @Getter @Setter
  private Long userStatus;

  @Getter @Setter
  private Long userCtrlStatus;

  @Getter @Setter
  private Long userPtrlStatus;

  @Getter @Setter
  private Long goodReviewNumber;

  @Getter @Setter
  private Long badReviewNumber;

  @Getter @Setter
  private Long normalReviewNumber;

  @Getter @Setter
  private Long postNumber;

  @Getter @Setter
  private String userJob;

  @Getter @Setter
  private String career;

  @Getter @Setter
  private ShmAddr province;

  @Getter @Setter
  private ShmAddr district;

  @Getter @Setter
  private ShrFile avatar;

  @Getter @Setter
  private long totalItems;

  @Getter @Setter
  private String userAvatarPath;

  @Getter @Setter
  private Long totalPost;

  @Getter @Setter
  private String addressTxt;

  @Getter @Setter
  private int age;

}
