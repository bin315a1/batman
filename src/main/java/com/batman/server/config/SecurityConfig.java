package com.batman.server.config;

import com.batman.server.interceptor.JWTAuthenticationFilter;
import com.batman.server.interceptor.JWTAuthorizationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    static final int BCRYPT_STRENGTH = 10;
    static final SecureRandom BCRYPT_SALT = new SecureRandom();

    public static final String HEADER_STRING = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final int JWT_EXPIRATION_DAYS = 10;

    @Autowired
    UserDetailsService userService;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    RuntimeProperties runtimeProperties;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        String SIGN_UP_URL = "/signup";
        http.cors().and().csrf().disable().authorizeRequests()
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilter(new JWTAuthorizationFilter(authenticationManager(), runtimeProperties.getJWTSecret()))
                .addFilter(JWTAuthenticationFilter.builder()
                        .authenticationManager(authenticationManager())
                        .jwtSecret(runtimeProperties.getJWTSecret())
                        .build())
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(BCRYPT_STRENGTH, BCRYPT_SALT);
    }

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    public static String getJWTToken(String tokenToParse) {
        return tokenToParse.replace(TOKEN_PREFIX, "");
    }
}
