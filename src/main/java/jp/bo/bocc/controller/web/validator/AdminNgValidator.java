package jp.bo.bocc.controller.web.validator;

import jp.bo.bocc.entity.ShmAdminNg;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Created by HaiTH on 3/28/2017.
 */

@Component
public class AdminNgValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return ShmAdminNg.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        ShmAdminNg shmAdminNg = (ShmAdminNg) o;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "adminNgContent", "SH_E100044");

        if(shmAdminNg.getAdminNgContent().length() > 100) {
            errors.rejectValue("adminNgContent", "SH_E100004");
        }
    }
}
