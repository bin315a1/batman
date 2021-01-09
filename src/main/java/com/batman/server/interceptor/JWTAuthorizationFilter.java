package com.batman.server.interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.batman.server.config.SecurityConfig;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    String JWTSecret;

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager, String JWTSecret) {
        super (authenticationManager);
        this.JWTSecret = JWTSecret;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse resp,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(SecurityConfig.HEADER_STRING);

        if (header == null || !header.startsWith(SecurityConfig.TOKEN_PREFIX)) {
            chain.doFilter(req, resp);
            return;
        }

        UsernamePasswordAuthenticationToken authentication = getJWTToken(req);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, resp);
    }

    private UsernamePasswordAuthenticationToken getJWTToken(HttpServletRequest req) {
        String token = req.getHeader(SecurityConfig.HEADER_STRING);
        if (token != null) {
            String user = JWT.require(Algorithm.HMAC512(JWTSecret.getBytes()))
                    .build()
                    .verify(token.replace(SecurityConfig.TOKEN_PREFIX, ""))
                    .getSubject();

            if (user != null) {
                return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
            }
        }
        return null;
    }
}
