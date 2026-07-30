package com.lhind.AnnualLeaveApp.security;

import com.lhind.AnnualLeaveApp.service.impl.UserServiceImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserServiceImpl userServiceImpl;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final LoginSuccessHandler loginSuccessHandler;

    public WebSecurityConfiguration(UserServiceImpl userServiceImpl,
                                    BCryptPasswordEncoder bCryptPasswordEncoder,
                                    LoginSuccessHandler loginSuccessHandler) {
        this.userServiceImpl = userServiceImpl;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.loginSuccessHandler = loginSuccessHandler;
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userServiceImpl)
                .passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "/api/login", "/api/login-error", "/api/logged-out", "/login", "/login-error", "/h2-console/**")
                .permitAll()
                .antMatchers("/api/user/**")
                .hasAnyAuthority(ApplicationRoles.USER.name())
                .antMatchers("/api/supervisor/**")
                .hasAnyAuthority(ApplicationRoles.SUPERVISOR.name())
                .antMatchers("/api/admin/**")
                .hasAnyAuthority(ApplicationRoles.ADMIN.name())
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/api/login")
                .loginProcessingUrl("/api/login")
                .passwordParameter("password")
                .usernameParameter("username")
                .successHandler(loginSuccessHandler)
                .failureUrl("/api/login-error")
                .permitAll()
                .and()
                .logout()
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .logoutSuccessUrl("/api/logged-out")
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
                .clearAuthentication(true).and()
                .exceptionHandling()
                .accessDeniedPage("/api/access-denied");
    }

}
