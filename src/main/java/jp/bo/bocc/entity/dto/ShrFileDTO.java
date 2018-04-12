package jp.bo.bocc.entity.dto;


import lombok.Getter;
import lombok.Setter;

public class ShrFileDTO extends BaseDTO{

  @Getter @Setter
  private Long fileId;

  @Getter @Setter
  private String fileProvider;

  @Getter @Setter
  private String fileOrgName;

  @Getter @Setter
  private String fileName;

  @Getter @Setter
  private Integer fileWidth;

  @Getter @Setter
  private Integer fileHeight;

  @Getter @Setter
  private Long fileSize;

  @Getter @Setter
  private String fileExt;

  @Getter @Setter
  private String fileDir;
}
