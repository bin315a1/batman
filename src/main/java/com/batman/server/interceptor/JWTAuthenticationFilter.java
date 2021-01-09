package com.batman.server.interceptor;

import com.auth0.jwt.JWT;
import com.batman.server.config.SecurityConfig;
import com.batman.server.exception.HTTPUnauthorizedException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;

/**
 * JWTAuthenticationFilter
 *
 * Intercepts the request coming into /login endpoint
 */
@Builder
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    String jwtSecret;

    private AuthenticationManager authenticationManager;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse resp) throws AuthenticationException {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> reqMap = mapper.readValue(req.getInputStream(), Map.class);

            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            reqMap.get("email"),
                            reqMap.get("password"),
                            new ArrayList<>()
                    )
            );
        } catch (AuthenticationException e) {
            throw new HTTPUnauthorizedException();
        } catch (IOException e) {
            throw new RuntimeException ("Error parsing request");
        }
    }

    @Override
    protected void successfulAuthentication(
            HttpServletRequest req,
            HttpServletResponse resp,
            FilterChain chain,
            Authentication authentication) throws IOException, ServletException {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, SecurityConfig.JWT_EXPIRATION_DAYS);
        Date expires = cal.getTime();


        String token = JWT.create()
                .withSubject(((User) authentication.getPrincipal()).getUsername())
                .withExpiresAt(expires)
                .sign(HMAC512(jwtSecret.getBytes()));

        resp.addHeader(SecurityConfig.HEADER_STRING, SecurityConfig.TOKEN_PREFIX + token);
    }
}
