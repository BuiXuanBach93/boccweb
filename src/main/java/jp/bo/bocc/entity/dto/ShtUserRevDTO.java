package jp.bo.bocc.entity.dto;


import lombok.Getter;
import lombok.Setter;

public class ShtUserRevDTO extends BaseDTO {

  @Getter @Setter
  private long userRevId;

  @Getter @Setter
  private long talkPurcId;

  @Getter @Setter
  private long userRevType;

  @Getter @Setter
  private long userRevRate;

  @Getter @Setter
  private String userRevCont;
}
