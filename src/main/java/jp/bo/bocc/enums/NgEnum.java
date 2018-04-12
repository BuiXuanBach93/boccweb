package jp.bo.bocc.enums;

/**
 * @author NguyenThuong on 4/21/2017.
 */
public enum NgEnum {

    NOT_SUITABLE ("画像・商品名が不適切である"),
    SENSITIVE_INF ("個人を特定できる内容が含まれている"),
    SLANDER ("誹謗中傷の内容が含まれている"),
    CHEAT ("詐欺の疑いがある"),
    OTHER ("その他利用規約に反する内容がある");

    private String value;

    NgEnum(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

}
