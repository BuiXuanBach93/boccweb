package jp.bo.bocc.entity.dto;

import lombok.Getter;
import lombok.Setter;

public class ShtAdminLogDTO extends BaseDTO {

  @Getter @Setter
  private long adminLogId;

  @Getter @Setter
  private String adminId;

  @Getter @Setter
  private long adminLogType;

  @Getter @Setter
  private long adminLogTitle;

  @Getter @Setter
  private String adminLogCont;
}
