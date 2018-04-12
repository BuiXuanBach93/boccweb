package jp.bo.bocc.enums;

import java.io.UnsupportedEncodingException;

/**
 * Created by HaiTH on 3/23/2017.
 */
public enum  AdminRoleEnum {

    SUPPER_ADMIN("スーパーユーザー"),
    ADMIN ("通常管理者"),
    SITE_PATROL ("サイトパトローラ"),
    CUSTOMER_SUPPORT ("カスタマー対応");

    private String value;

    AdminRoleEnum(String value) {
        this.value=value;
    }

    public String value() {
        return value;
    }

    public static String getRoleAdmin(long i) throws UnsupportedEncodingException {
        if ( i ==0) {
            String result = new String( AdminRoleEnum.SUPPER_ADMIN.value().getBytes("UTF-8"),"UTF-8");
            return result;
        }else if (i ==1) {
            String result = new String( AdminRoleEnum.ADMIN.value().getBytes("UTF-8"),"UTF-8");
            return result;
        }else if (i ==2) {
            String result = new String( AdminRoleEnum.SITE_PATROL.value().getBytes("UTF-8"),"UTF-8");
            return result;
        }else {
            String result = new String( AdminRoleEnum.CUSTOMER_SUPPORT.value().getBytes("UTF-8"),"UTF-8");
            return result;
        }
    }


}
