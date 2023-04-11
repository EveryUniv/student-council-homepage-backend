package com.dku.council.global.auth;

import com.dku.council.global.auth.jwt.AuthenticationTokenProvider;
import com.dku.council.global.auth.jwt.GuestAuthentication;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationTokenProvider authenticationTokenProvider;
    private final GuestAuthentication guestAuthentication = new GuestAuthentication();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        String accessToken = authenticationTokenProvider.getAccessTokenFromHeader(request);
        Authentication authentication;
        if (accessToken != null) {
            authentication = authenticationTokenProvider.getAuthentication(accessToken);
        } else {
            authentication = guestAuthentication;
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(requestWrapper, responseWrapper);
        responseWrapper.copyBodyToResponse();
    }
}
