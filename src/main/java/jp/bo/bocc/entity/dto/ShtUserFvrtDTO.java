package jp.bo.bocc.entity.dto;


import lombok.Getter;
import lombok.Setter;

public class ShtUserFvrtDTO extends BaseDTO{

  @Getter @Setter
  private long userFvrtId;

  @Getter @Setter
  private long postId;

  @Getter @Setter
  private long userId;

  @Getter @Setter
  private boolean userFvrtStatus;
}
