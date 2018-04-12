package jp.bo.bocc.entity.dto;


import jp.bo.bocc.entity.ShmAdmin;
import jp.bo.bocc.entity.ShtAdminCsvHst;
import lombok.Getter;
import lombok.Setter;

public class ShtAdminCsvHstDTO extends BaseDTO {

  @Getter @Setter
  private Long adminCsvHstId;

  @Getter @Setter
  private ShmAdmin admin;

  @Getter @Setter
  private ShtAdminCsvHst.CSV_TYPE adminCsvHstType;
}
