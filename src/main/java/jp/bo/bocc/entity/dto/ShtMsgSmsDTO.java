package jp.bo.bocc.entity.dto;


import lombok.Getter;
import lombok.Setter;

public class ShtMsgSmsDTO extends BaseDTO {

  @Getter @Setter
  private long shtMsgSmsId;

  @Getter @Setter
  private long shtMsgSmsPhone;

  @Getter @Setter
  private String shtMsgSmsContent;

  @Getter @Setter
  private long shtMsgSmsStatus;

  @Getter @Setter
  private long shtMsgSmsType;
}
