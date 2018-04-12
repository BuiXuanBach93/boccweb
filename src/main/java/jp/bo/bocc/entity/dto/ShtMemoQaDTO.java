package jp.bo.bocc.entity.dto;


import jp.bo.bocc.entity.ShmAdmin;
import jp.bo.bocc.entity.ShtQa;
import lombok.Getter;
import lombok.Setter;

public class ShtMemoQaDTO extends BaseDTO {

  @Getter @Setter
  private long memoId;

  @Getter @Setter
  private ShtQa qa;

  @Getter @Setter
  private String memoCont;

  @Getter @Setter
  private ShmAdmin admin;

}
