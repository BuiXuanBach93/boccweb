package jp.bo.bocc.entity.dto;


import lombok.Getter;
import lombok.Setter;

public class ShmAdminDTO extends BaseDTO {

  @Getter @Setter
  private long adminId;

  @Getter @Setter
  private String adminName;

  @Getter @Setter
  private String adminPwd;

  @Getter @Setter
  private String adminEmail;

  @Getter @Setter
  private Integer adminRole;
}
