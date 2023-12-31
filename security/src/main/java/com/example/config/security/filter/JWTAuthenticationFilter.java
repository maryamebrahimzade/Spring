package com.example.config.security.filter;

import com.example.config.security.jwt.JwtUtil;
import com.example.config.security.token.EmailPasswordAuthenticationToken;
import com.example.user.models.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
   private ObjectMapper objectMapper=new ObjectMapper();
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtTokenUtil) {
       super.setFilterProcessesUrl("/api/users/login");
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtTokenUtil;
    }
    @Override //
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {

        try {
            Map<?, ?> creds = objectMapper.readValue(request.getInputStream(), Map.class);
            Authentication authentication = new EmailPasswordAuthenticationToken(creds.get("email"), creds.get("password"));
            return authenticationManager.authenticate(authentication);
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new BadCredentialsException("Authentication failed");
    }

    @Override //
    public void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {
        Map<String, Object> body = new HashMap<>();
        body.put("token", jwtUtil.generateAccessToken((User) authentication.getPrincipal()));
        body.put("refreshToken", jwtUtil.generateAccessToken((User) authentication.getPrincipal()));
        body.put("expiration", jwtUtil.expiration().getEpochSecond());
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getWriter(), body);
    }
}
