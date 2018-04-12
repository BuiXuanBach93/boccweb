package jp.bo.bocc.entity.dto;


import lombok.Getter;
import lombok.Setter;

public class ShtUserTokenDTO extends BaseDTO {

  @Getter @Setter
  private long userId;

  @Getter @Setter
  private String tokenId;

  @Getter @Setter
  private long tokenType;

  @Getter @Setter
  private long tokenExpireIn;
}
