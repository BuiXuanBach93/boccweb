package jp.bo.bocc.entity.dto;


import lombok.Getter;
import lombok.Setter;

public class ShtUserRprtDTO extends BaseDTO {

  @Getter @Setter
  private long userRprtId;

  @Getter @Setter
  private long postId;

  @Getter @Setter
  private long userId;

  @Getter @Setter
  private long userRprtType;

  @Getter @Setter
  private String userRprtCont;
}
