package jp.bo.bocc.entity.dto;


import lombok.Getter;
import lombok.Setter;

public class ShmAdminNgDTO extends BaseDTO {

  @Getter @Setter
  private long adminNgId;

  @Getter @Setter
  private String adminNgContent;

  @Getter @Setter
  private Long adminId;

  @Getter @Setter
  private String adminName;

  @Getter @Setter
  private String adminAction;
}
