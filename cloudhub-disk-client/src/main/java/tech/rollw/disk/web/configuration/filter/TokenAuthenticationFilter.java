/*
 * Copyright (C) 2023 RollW
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tech.rollw.disk.web.configuration.filter;

import tech.rollw.disk.web.common.ApiContextHolder;
import tech.rollw.disk.web.domain.authentication.AuthenticationException;
import tech.rollw.disk.web.domain.authentication.token.AuthenticationTokenService;
import tech.rollw.disk.web.domain.authentication.token.TokenAuthResult;
import tech.rollw.disk.web.domain.user.UserDetailsService;
import tech.rollw.disk.web.domain.user.dto.UserInfo;
import tech.rollw.disk.web.domain.user.service.UserSignatureProvider;
import tech.rollw.disk.common.AuthErrorCode;
import tech.rollw.disk.common.BusinessRuntimeException;
import tech.rollw.disk.common.RequestUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import space.lingu.NonNull;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author RollW
 */
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final AuthenticationTokenService authenticationTokenService;
    private final UserDetailsService userDetailsService;
    private final UserSignatureProvider userSignatureProvider;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public TokenAuthenticationFilter(AuthenticationTokenService authenticationTokenService,
                                     UserDetailsService userDetailsService,
                                     UserSignatureProvider userSignatureProvider) {
        this.authenticationTokenService = authenticationTokenService;
        this.userDetailsService = userDetailsService;
        this.userSignatureProvider = userSignatureProvider;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        ApiContextHolder.clearContext();
        String requestUri = request.getRequestURI();
        HttpMethod method = HttpMethod.resolve(request.getMethod());
        boolean isAdminApi = isAdminApi(requestUri);
        String remoteIp = RequestUtils.getRemoteIpAddress(request);
        long timestamp = System.currentTimeMillis();

        try {
            Authentication existAuthentication =
                    SecurityContextHolder.getContext().getAuthentication();
            if (existAuthentication != null) {
                UserDetails userDetails = (UserDetails)
                        existAuthentication.getPrincipal();
                UserInfo userInfo = UserInfo.from(userDetails);
                setApiContext(isAdminApi, remoteIp, method, userInfo, timestamp);
                filterChain.doFilter(request, response);
                return;
            }

            String token = loadToken(request);
            boolean tokenExists = token != null && !token.isEmpty();
            if (!tokenExists) {
                nullNextFilter(isAdminApi, remoteIp, method, timestamp,
                        request, response, filterChain);
                return;
            }

            Long userId = authenticationTokenService.getUserId(token);
            if (userId == null) {
                nullNextFilter(isAdminApi, remoteIp, method, timestamp,
                        request, response, filterChain);
                return;
            }
            UserDetails userDetails = tryGetUserDetails(userId);
            String signature = userSignatureProvider.getSignature(userId);
            TokenAuthResult result = authenticationTokenService.verifyToken(
                    token,
                    signature
            );
            if (!result.success()) {
                // although there is anonymous api access that doesn't need provides token,
                // but as long as it provides token here, we have to verify it.
                // And throw exception when failed.
                throw new BusinessRuntimeException(result.errorCode());
            }

            UserInfo userInfo = UserInfo.from(userDetails);
            Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                    userDetails,
                    userDetails.getPassword(),
                    userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            setApiContext(isAdminApi, remoteIp, method, userInfo, timestamp);
            filterChain.doFilter(request, response);
        } finally {
            ApiContextHolder.clearContext();
        }
    }

    private UserDetails tryGetUserDetails(long id) {
        try {
            return userDetailsService.loadUserByUserId(id);
        } catch (UsernameNotFoundException e) {
            throw new AuthenticationException(
                    AuthErrorCode.ERROR_INVALID_TOKEN
            );
        }
    }

    private static void setApiContext(boolean isAdminApi, String remoteIp,
                                      HttpMethod method, UserInfo userInfo,
                                      long timestamp) {
        ApiContextHolder.ApiContext apiContext = new ApiContextHolder.ApiContext(
                isAdminApi, remoteIp, LocaleContextHolder.getLocale(),
                method, userInfo, timestamp);
        ApiContextHolder.setContext(apiContext);
    }

    private boolean isAdminApi(String requestUri) {
        if (requestUri == null || requestUri.length() <= 10) {
            return false;
        }

        return antPathMatcher.match("/api/{version}/admin/**", requestUri);
    }

    private void nullNextFilter(boolean isAdminApi, String remoteIp, HttpMethod method, long timestamp,
                                HttpServletRequest request, HttpServletResponse response,
                                FilterChain filterChain) throws IOException, ServletException {
        setApiContext(isAdminApi, remoteIp, method, null, timestamp);
        filterChain.doFilter(request, response);
    }

    private String loadToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            return request.getParameter("token");
        }
        return token;
    }
}
