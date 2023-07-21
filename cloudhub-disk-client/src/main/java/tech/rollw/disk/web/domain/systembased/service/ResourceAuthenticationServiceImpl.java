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

package tech.rollw.disk.web.domain.systembased.service;

import tech.rollw.disk.web.domain.authentication.AuthenticationException;
import tech.rollw.disk.web.domain.operatelog.Action;
import tech.rollw.disk.web.domain.operatelog.Operator;
import tech.rollw.disk.web.domain.systembased.SimpleSystemAuthentication;
import tech.rollw.disk.web.domain.systembased.SystemAuthentication;
import tech.rollw.disk.web.domain.systembased.SystemResource;
import tech.rollw.disk.web.domain.systembased.SystemResourceAuthenticationProvider;
import tech.rollw.disk.web.domain.systembased.SystemResourceAuthenticationProviderFactory;
import tech.rollw.disk.web.domain.systembased.SystemResourceKind;
import tech.rollw.disk.common.AuthErrorCode;
import org.springframework.stereotype.Service;
import space.lingu.NonNull;

import java.util.List;

/**
 * @author RollW
 */
@Service
public class ResourceAuthenticationServiceImpl implements SystemResourceAuthenticationProviderFactory {
    private final List<SystemResourceAuthenticationProvider> authenticationProviders;
    private SystemResourceAuthenticationProvider defaultSystemResourceAuthenticationProvider;

    public ResourceAuthenticationServiceImpl(List<SystemResourceAuthenticationProvider> authenticationProviders) {
        this.authenticationProviders = authenticationProviders;
        defaultSystemResourceAuthenticationProvider = DefaultProvider.INSTANCE;
    }

    @Override
    public SystemResourceAuthenticationProvider getSystemResourceAuthenticationProvider(
            SystemResourceKind resourceKind) {
        SystemResourceAuthenticationProvider systemResourceAuthenticationProvider =
                findFirstAuthenticationProvider(resourceKind);
        if (systemResourceAuthenticationProvider == null) {
            throw new AuthenticationException(AuthErrorCode.ERROR_NO_HANDLER,
                    "No authentication provider found for resource kind: " + resourceKind +
                            ", or set a default authentication provider.");
        }
        return systemResourceAuthenticationProvider;
    }

    @Override
    public void setDefaultSystemResourceAuthenticationProvider(
            SystemResourceAuthenticationProvider systemResourceAuthenticationProvider) {
        this.defaultSystemResourceAuthenticationProvider = systemResourceAuthenticationProvider;
    }

    private SystemResourceAuthenticationProvider findFirstAuthenticationProvider(
            SystemResourceKind resourceKind) {
        return authenticationProviders.stream()
                .filter(authenticationProvider -> authenticationProvider.isAuthentication(resourceKind))
                .findFirst()
                .orElse(defaultSystemResourceAuthenticationProvider);
    }

    private static class DefaultProvider implements SystemResourceAuthenticationProvider {
        private DefaultProvider() {
        }

        @Override
        public boolean isAuthentication(SystemResourceKind resourceKind) {
            return true;
        }

        @NonNull
        @Override
        public SystemAuthentication authenticate(SystemResource systemResource,
                                                 Operator operator, Action action) {
            return new SimpleSystemAuthentication(systemResource, operator, true);
        }

        static final DefaultProvider INSTANCE = new DefaultProvider();
    }


    public static SystemResourceAuthenticationProvider getDefaultProvider() {
        return DefaultProvider.INSTANCE;
    }
}
