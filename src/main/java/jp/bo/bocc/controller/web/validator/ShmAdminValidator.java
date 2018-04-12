package jp.bo.bocc.controller.web.validator;

import jp.bo.bocc.entity.ShmAdmin;
import jp.bo.bocc.helper.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

/**
 * Created by buixu on 10/13/2017.
 */
@Component
public class ShmAdminValidator implements Validator {
    @Autowired
    EmailValidator emailValidator;

    @Override
    public boolean supports(Class<?> aClass) {
        return ShmAdmin.class.equals(aClass);
    }

    @Override
    public void validate(Object object, Errors errors) {

        ShmAdmin shmAdmin =(ShmAdmin) object;
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "adminRole", "SH_E100040");

        if(shmAdmin.getAdminRole() == null || shmAdmin.getAdminRole() < 0){
            errors.rejectValue("adminRole", "SH_E100040");
        }

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

        final String passwordRefresh = shmAdmin.getPwdFresh();
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(passwordRefresh)) {
            final String oldPassword = shmAdmin.getAdminPwd();
            if (!oldPassword.equalsIgnoreCase(passwordRefresh)) {
                if (passwordRefresh.length() < 8 || passwordRefresh.length() > 20) {
                    errors.rejectValue("pwdFresh", "SH_E100153");
                }
            }
        }
    }
}
