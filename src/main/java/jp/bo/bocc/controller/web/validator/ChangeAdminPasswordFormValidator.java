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
 * @author DonBach on 28/8/2017.
 */
@Component
public class ChangeAdminPasswordFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return ShmAdmin.class.equals(aClass);
    }

    @Override
    public void validate(Object object, Errors errors) {

        ShmAdmin shmAdmin =(ShmAdmin) object;
        final String passwordRefresh = shmAdmin.getPwdFresh();
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(passwordRefresh)) {
            final String oldPassword = shmAdmin.getAdminPwd();
            if (!oldPassword.equalsIgnoreCase(passwordRefresh)) {
                if (passwordRefresh.length() < 8 || passwordRefresh.length() > 20) {
                    errors.rejectValue("pwdFresh", "SH_E100153");
                }else {
                    PasswordEncoder encoder = new BCryptPasswordEncoder();
                    shmAdmin.setPwdFresh(encoder.encode(passwordRefresh));
                }
            }
        }
    }
}
