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

package tech.rollw.disk.web.domain.systembased.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import tech.rollw.disk.web.common.ApiContextHolder;
import tech.rollw.disk.web.domain.authentication.AuthenticationException;
import tech.rollw.disk.web.domain.operatelog.Action;
import tech.rollw.disk.web.domain.operatelog.context.BuiltinOperate;
import tech.rollw.disk.web.domain.systembased.*;
import tech.rollw.disk.web.domain.user.UserIdentity;
import tech.rollw.disk.common.AuthErrorCode;
import org.springframework.stereotype.Component;
import space.lingu.NonNull;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author RollW
 */
@Aspect
@Component
public class SystemResourceAuthInterceptor {
    private final SystemResourceAuthenticationProviderFactory systemResourceAuthenticationProviderFactory;

    public SystemResourceAuthInterceptor(SystemResourceAuthenticationProviderFactory systemResourceAuthenticationProviderFactory) {
        this.systemResourceAuthenticationProviderFactory = systemResourceAuthenticationProviderFactory;
    }


    @Before("@annotation(tech.rollw.disk.web.domain.systembased.SystemResourceAuthenticate)")
    public void beforeAuthentication(@NonNull JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        UserIdentity userIdentity = ApiContextHolder.getContext().userInfo();
        SystemResourceAuthenticate systemResourceAuthenticate =
                method.getAnnotation(SystemResourceAuthenticate.class);
        if (systemResourceAuthenticate == null) {
            return;
        }
        BuiltinOperate builtinOperate = method.getAnnotation(BuiltinOperate.class);
        Action action = findAction(systemResourceAuthenticate, builtinOperate);
        SystemResourceKind systemResourceKind = findResourceKind(joinPoint,
                method, systemResourceAuthenticate, builtinOperate);
        long resourceId = findResourceId(joinPoint, method, systemResourceAuthenticate);

        SystemResourceAuthenticationProvider authenticationProvider = systemResourceAuthenticationProviderFactory
                .getSystemResourceAuthenticationProvider(systemResourceKind);
        SystemResource systemResource = new SimpleSystemResource(resourceId, systemResourceKind);
        SystemAuthentication systemAuthentication = authenticationProvider.authenticate(
                systemResource,
                userIdentity, action
        );

        systemAuthentication.throwAuthenticationException();
    }

    private Action findAction(SystemResourceAuthenticate systemResourceAuthenticate,
                              BuiltinOperate builtinOperate) {
        if (!systemResourceAuthenticate.inferredAction()) {
            return systemResourceAuthenticate.action();
        }
        return checkNull(builtinOperate).value().getAction();
    }

    private SystemResourceKind findResourceKind(JoinPoint joinPoint,
                                                Method method,
                                                SystemResourceAuthenticate systemResourceAuthenticate,
                                                BuiltinOperate builtinOperate) {
        if (!systemResourceAuthenticate.inferredKind() &&
                systemResourceAuthenticate.kindParam().isEmpty()) {
            return checkKind(systemResourceAuthenticate.kind());
        }
        if (systemResourceAuthenticate.inferredKind()) {
            return checkKind(
                    checkNull(builtinOperate).value().getSystemResourceKind()
            );
        }
        Object obj = findParamValueOf(joinPoint, method, systemResourceAuthenticate.kindParam());
        if (obj instanceof SystemResourceKind kind) {
            return checkKind(kind);
        }
        if (obj instanceof String kind) {
            return checkKind(SystemResourceKind.from(kind));
        }

        throw new IllegalArgumentException("Cannot cast param value to SystemResourceKind: " + obj.getClass() +
                ", parameter name: " + systemResourceAuthenticate.kindParam() + ", in " + method);
    }

    private SystemResourceKind checkKind(SystemResourceKind kind) {
        if (kind == null) {
            throw new AuthenticationException(AuthErrorCode.ERROR_UNKNOWN_AUTH,
                    "SystemResourceKind is null or unknown");
        }
        return kind;
    }

    private long findResourceId(JoinPoint joinPoint,
                                Method method,
                                SystemResourceAuthenticate systemResourceAuthenticate) {
        if (systemResourceAuthenticate.idParam().isEmpty()) {
            if (systemResourceAuthenticate.id() == SystemResourceAuthenticate.INVALID_ID) {
                throw new IllegalArgumentException("resourceId and resourceIdParam cannot be both empty");
            }
            return systemResourceAuthenticate.id();
        }
        Object obj = findParamValueOf(joinPoint, method, systemResourceAuthenticate.idParam());
        if (obj instanceof Long id) {
            return id;
        }
        if (obj instanceof String id) {
            try {
                return Long.parseLong(id);
            } catch (NumberFormatException ignored) {
            }
        }
        throw new IllegalArgumentException("Cannot cast param value to long: " + obj.getClass() +
                ", parameter name: " + systemResourceAuthenticate.idParam() + ", in " + method);
    }

    private BuiltinOperate checkNull(BuiltinOperate builtinOperate) {
        if (builtinOperate == null) {
            throw new AuthenticationException(AuthErrorCode.ERROR_UNKNOWN_AUTH,
                    "BuiltinOperate is null");
        }
        return builtinOperate;
    }

    private Object findParamValueOf(JoinPoint joinPoint,
                                    Method method, String name) {
        Object[] args = joinPoint.getArgs();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getName().equals(name)) {
                return args[i];
            }
        }
        throw new IllegalArgumentException("No such param found in system resource authentication: " + name);
    }

}
