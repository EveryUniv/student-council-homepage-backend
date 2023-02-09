package com.dku.council.global.auth;

import com.dku.council.global.auth.jwt.AuthenticationToken;
import com.dku.council.global.auth.jwt.JwtAuthentication;
import com.dku.council.global.auth.jwt.JwtAuthenticationTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtAuthenticationTokenProvider jwtAuthenticationTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        AuthenticationToken tokenFromHeader = jwtAuthenticationTokenProvider.getTokenFromHeader(request);
        //인증 시작
        if(tokenFromHeader.getAccessToken() != null){
            //Authentication 등록
            JwtAuthentication authentication = jwtAuthenticationTokenProvider.getAuthentication(tokenFromHeader.getAccessToken());
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }
}
