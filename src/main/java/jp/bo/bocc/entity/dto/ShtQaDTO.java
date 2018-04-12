package jp.bo.bocc.entity.dto;


import jp.bo.bocc.entity.ShtQa;
import lombok.Getter;
import lombok.Setter;

public class ShtQaDTO extends BaseDTO {

  @Getter @Setter
  private long qaId;

  @Getter @Setter
  private long userId;

  @Getter @Setter
  private ShtQa.QaTypeEnum qaType;

  @Getter @Setter
  private  ShtQa.QaStatusEnum qaStatus;
}
