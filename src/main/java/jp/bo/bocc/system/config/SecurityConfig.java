/**
 *
 */
package jp.bo.bocc.system.config;

import jp.bo.bocc.service.impl.AdminDetailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;

import javax.sql.DataSource;

/**
 * @author haipv
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private DataSource dataSource;

    CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    AdminDetailServiceImpl adminDetailServiceImpl;

    @Override
    protected void configure(AuthenticationManagerBuilder registry) throws Exception {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        registry.userDetailsService(adminDetailServiceImpl).passwordEncoder(encoder);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .antMatchers("/resources/**");
        web
                .ignoring()
                .antMatchers("/backend/img/**");
        web
                .ignoring()
                .antMatchers("/backend/activation**");
        web
                .ignoring()
                .antMatchers("/backend/banner-page**");
    }

    @Bean
    public RememberMeServices rememberMeServices() {
        TokenBasedRememberMeServices rememberMeServices = new TokenBasedRememberMeServices("KEY", adminDetailServiceImpl);
        rememberMeServices.setTokenValiditySeconds(86400);

        return rememberMeServices;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().and()
                .authorizeRequests()
                .antMatchers("/backend/login", "/backend/login**", "/backend/register", "/backend/logout").permitAll() // #4
                .anyRequest().authenticated() // 7
                .and().formLogin().loginPage("/backend/login")
                .defaultSuccessUrl("/backend")
                .failureUrl("/backend/login?error").successHandler(new CustomAuthenticationSuccessHandler()).permitAll()
                .and()
                .rememberMe().key("KEY").rememberMeServices(rememberMeServices())
                .and()
                .exceptionHandling().accessDeniedPage("/backend");

        http.headers().xssProtection().block(true);


        http.logout().logoutSuccessHandler(new CustomLogoutSuccessHandler())
                .logoutUrl("/backend/logout")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true);
    }
}
