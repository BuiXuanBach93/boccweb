package jp.bo.bocc.controller.web.validator;

import jp.bo.bocc.entity.ShmAdmin;
import jp.bo.bocc.helper.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Created by HaiTH on 3/24/2017.
 */
@Component
public class AdminFormValidator implements Validator {

    @Autowired
    EmailValidator emailValidator;

    @Override
    public boolean supports(Class<?> aClass) {
        return ShmAdmin.class.equals(aClass);
    }

    @Override
    public void validate(Object object, Errors errors) {

        ShmAdmin shmAdmin =(ShmAdmin) object;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "adminPwd", "SH_E100041");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "adminRole", "SH_E100040");

        final String adminName = shmAdmin.getAdminName();
        if (org.apache.commons.lang3.StringUtils.isEmpty(adminName)) {
            errors.rejectValue("adminName", "SH_E100039");
        }

        final String adminEmail = shmAdmin.getAdminEmail();
        if (org.apache.commons.lang3.StringUtils.isEmpty(adminEmail)){
            errors.rejectValue("adminEmail", "SH_E100038");
        } else if(!StringUtils.isEmailValid(adminEmail)){
            errors.rejectValue("adminEmail", "SH_E100003");
        }
    }
}
