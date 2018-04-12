package jp.bo.bocc.entity.dto;


import lombok.Getter;
import lombok.Setter;

public class ShmAddrDTO extends BaseDTO {

  @Getter @Setter
  private long addrId;

  @Getter @Setter
  private String addrAreaCode;

  @Getter @Setter
  private String addrAreaName;

  @Getter @Setter
  private long addrParentId;
}
