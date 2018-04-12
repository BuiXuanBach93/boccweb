package jp.bo.bocc.enums;

/**
 * Created by Namlong on 4/12/2017.
 */
public enum SortFieldPostEnum {
    CREATED_AT_DESC("createdAtDesc"), PRICE_ASC("priceAsc"), PRICE_DESC("priceDesc"), LIKE_TIMES_DESC("likeTimesDesc"), LIKE_AT_DESC("likedAtDesc");
    private String value;

    SortFieldPostEnum(String value) {
        this.value = value;
    }
    public String getValue(){
        return value;
    }
}
