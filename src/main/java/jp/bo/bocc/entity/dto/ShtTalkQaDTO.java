package jp.bo.bocc.entity.dto;


import lombok.Getter;
import lombok.Setter;

public class ShtTalkQaDTO extends BaseDTO {

  @Getter @Setter
  private long talkQaId;

  @Getter @Setter
  private long qaId;

  @Getter @Setter
  private String talkQaMsg;

  @Getter @Setter
  private long adminId;
}
