package jp.bo.bocc.entity.dto;


import lombok.Getter;
import lombok.Setter;

public class ShmCategoryDTO extends BaseDTO {

  @Getter @Setter
  private long categoryId;

  @Getter @Setter
  private String categoryName;

  @Getter @Setter
  private long categoryIcon;

  @Getter @Setter
  private long categoryParentId;
}
