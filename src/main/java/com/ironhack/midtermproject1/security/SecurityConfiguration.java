package com.ironhack.midtermproject1.security;

import com.ironhack.midtermproject1.service.impl.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder);
                /*.inMemoryAuthentication()
                .withUser("accountHolder1").password(passwordEncoder.encode("123456")).roles("ACCOUNTHOLDER")
                .and()
                .withUser("user").password(passwordEncoder.encode("123456")).roles("USER");*/
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic();
        http.csrf().disable();
        http.authorizeRequests()
                //mvcMatcher is not working, so to allow any tests I authenticated the entire path
                .mvcMatchers(HttpMethod.GET, "/api/v1/checkingAccounts", "/api/v1/checkingAccounts/**").authenticated()
                .mvcMatchers(HttpMethod.GET, "/api/v1/creditCards", "/api/v1/creditCards/**").authenticated()
                .mvcMatchers(HttpMethod.GET, "/api/v1/savingAccounts", "/api/v1/savingAccounts/**").authenticated()
                .mvcMatchers(HttpMethod.GET, "/api/v1/studentCheckingAccounts", "/api/v1/studentCheckingAccounts/**").authenticated()
                .mvcMatchers(HttpMethod.GET, "/api/v1/transactions", "/api/v1/transactions/**").authenticated()
                .mvcMatchers(HttpMethod.GET, "/api/v1/accountHolders", "/api/v1/accountHolders/**").authenticated()
                .mvcMatchers(HttpMethod.PATCH, "/api/v1/checkingAccounts", "/api/v1/checkingAccounts/**").authenticated()
                .mvcMatchers(HttpMethod.PATCH, "/api/v1/creditCards", "/api/v1/creditCards/**").authenticated()
                .mvcMatchers(HttpMethod.PATCH, "/api/v1/savingAccounts", "/api/v1/savingAccounts/**").authenticated()
                .mvcMatchers(HttpMethod.PATCH, "/api/v1/studentCheckingAccounts", "/api/v1/studentCheckingAccounts/**").authenticated()
                .mvcMatchers(HttpMethod.PATCH, "/api/v1/transactions", "/api/v1/transactions/**").authenticated()
                .mvcMatchers(HttpMethod.PATCH, "/api/v1/accountHolders", "/api/v1/accountHolders/**").authenticated()
                .mvcMatchers(HttpMethod.POST, "/api/v1/checkingAccounts", "/api/v1/checkingAccounts/**").authenticated()
                .mvcMatchers(HttpMethod.POST, "/api/v1/creditCards", "/api/v1/creditCards/**").authenticated()
                .mvcMatchers(HttpMethod.POST, "/api/v1/savingAccounts", "/api/v1/savingAccounts/**").authenticated()
                .mvcMatchers(HttpMethod.POST, "/api/v1/studentCheckingAccounts", "/api/v1/studentCheckingAccounts/**").authenticated()
                .mvcMatchers(HttpMethod.POST, "/api/v1/transactions", "/api/v1/transactions/**").authenticated()
                .mvcMatchers(HttpMethod.POST, "/api/v1/accountHolders", "/api/v1/accountHolders/**").authenticated()
                .mvcMatchers(HttpMethod.PUT, "/api/v1/checkingAccounts", "/api/v1/checkingAccounts/**").authenticated()
                .mvcMatchers(HttpMethod.PUT, "/api/v1/creditCards", "/api/v1/creditCards/**").authenticated()
                .mvcMatchers(HttpMethod.PUT, "/api/v1/savingAccounts", "/api/v1/savingAccounts/**").authenticated()
                .mvcMatchers(HttpMethod.PUT, "/api/v1/studentCheckingAccounts", "/api/v1/studentCheckingAccounts/**").authenticated()
                .mvcMatchers(HttpMethod.PUT, "/api/v1/transactions", "/api/v1/transactions/**").authenticated()
                .mvcMatchers(HttpMethod.PUT, "/api/v1/accountHolders", "/api/v1/accountHolders/**").authenticated()
                .mvcMatchers(HttpMethod.DELETE, "/api/v1/checkingAccounts", "/api/v1/checkingAccounts/**").authenticated()
                .mvcMatchers(HttpMethod.DELETE, "/api/v1/creditCards", "/api/v1/creditCards/**").authenticated()
                .mvcMatchers(HttpMethod.DELETE, "/api/v1/savingAccounts", "/api/v1/savingAccounts/**").authenticated()
                .mvcMatchers(HttpMethod.DELETE, "/api/v1/studentCheckingAccounts", "/api/v1/studentCheckingAccounts/**").authenticated()
                .mvcMatchers(HttpMethod.DELETE, "/api/v1/transactions", "/api/v1/transactions/**").authenticated()
                .mvcMatchers(HttpMethod.DELETE, "/api/v1/accountHolders", "/api/v1/accountHolders/**").authenticated()
                /*.mvcMatchers(HttpMethod.GET, "/api/v1/checkingAccounts", "/api/v1/checkingAccounts/{id}").hasAnyRole("ROLE_ADMIN")
                .mvcMatchers(HttpMethod.GET, "/api/v1/creditCards", "/api/v1/creditCards/{id}").hasAnyRole("ROLE_ADMIN")
                .mvcMatchers(HttpMethod.GET, "/api/v1/savingAccounts", "/api/v1/savingAccounts/{id}").hasAnyRole("ROLE_ADMIN")
                .mvcMatchers(HttpMethod.GET, "/api/v1/studentCheckingAccounts", "/api/v1/studentCheckingAccounts/{id}").hasAnyRole("ROLE_ADMIN")
                .mvcMatchers(HttpMethod.GET, "/api/v1/checkingAccounts/loggedUserAccounts").hasAnyRole("ROLE_ACCOUNTHOLDER")
                .mvcMatchers(HttpMethod.GET, "/api/v1/creditCards/loggedUserAccounts").hasAnyRole("ROLE_ACCOUNTHOLDER")
                .mvcMatchers(HttpMethod.GET, "/api/v1/savingAccounts/loggedUserAccounts").hasAnyRole("ROLE_ACCOUNTHOLDER")
                .mvcMatchers(HttpMethod.GET, "/api/v1/studentCheckingAccounts/loggedUserAccounts").hasAnyRole("ROLE_ACCOUNTHOLDER")
                .mvcMatchers(HttpMethod.GET, "/api/v1/studentCheckingAccounts/loggedUserAccounts").hasAnyRole("ROLE_ACCOUNTHOLDER")
                .mvcMatchers(HttpMethod.POST, "/api/v1/checkingAccounts").hasAnyRole("ROLE_ACCOUNTHOLDER")
                .mvcMatchers(HttpMethod.POST, "/api/v1/creditCards").hasAnyRole("ROLE_ACCOUNTHOLDER")
                .mvcMatchers(HttpMethod.POST, "/api/v1/savingAccounts").hasAnyRole("ROLE_ACCOUNTHOLDER")
                .mvcMatchers(HttpMethod.POST, "/api/v1/studentCheckingAccounts").hasAnyRole("ROLE_ACCOUNTHOLDER")
                */
                .anyRequest().permitAll();
    }
}