package jp.bo.bocc.service.impl;

import jp.bo.bocc.entity.ShmAdmin;
import jp.bo.bocc.repository.AdminRepository;
import jp.bo.bocc.service.UserService;
import jp.bo.bocc.system.exception.UnauthorizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author NguyenThuong on 3/22/2017.
 */
@Service(value = "adminDetailService")
public class AdminDetailServiceImpl implements UserDetailsService {

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    MessageSource messageSource;

    @Autowired
    UserService userService;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        ShmAdmin admin = adminRepository.findByAdminEmailAndDeleteFlag(email, false);
        if (admin == null)
            throw new UnauthorizedException("Email is incorrect");

        userService.cleanDataNotProcessedByAdmin(admin.getAdminId());
        List<GrantedAuthority> authorities = buildUserAuthority(ShmAdmin.ADMIN_ROLE.values()[admin.getAdminRole()]);

        return buildUserForAuthentication(admin, authorities);
    }

    private User buildUserForAuthentication(ShmAdmin user, List<GrantedAuthority> authorities) {
        return new User(user.getAdminEmail(), user.getAdminPwd(),
                true, true, true, true, authorities);
    }

    private List<GrantedAuthority> buildUserAuthority(ShmAdmin.ADMIN_ROLE userRoles) {

        Set<GrantedAuthority> setAuths = new HashSet<>();

        setAuths.add(new SimpleGrantedAuthority(String.valueOf(userRoles)));

        List<GrantedAuthority> Result = new ArrayList<>(setAuths);

        return Result;
    }

}
