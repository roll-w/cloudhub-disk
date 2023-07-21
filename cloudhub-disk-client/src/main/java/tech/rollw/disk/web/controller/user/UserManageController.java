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

package tech.rollw.disk.web.controller.user;

import tech.rollw.disk.web.controller.AdminApi;
import tech.rollw.disk.web.controller.OneParameterRequest;
import tech.rollw.disk.web.controller.user.vo.UserCreateRequest;
import tech.rollw.disk.web.domain.systembased.ContextThreadAware;
import tech.rollw.disk.web.domain.systembased.SimpleSystemResource;
import tech.rollw.disk.web.domain.systembased.SystemResourceKind;
import tech.rollw.disk.web.domain.systembased.SystemResourceOperatorProvider;
import tech.rollw.disk.web.domain.systembased.paged.PageableContext;
import tech.rollw.disk.web.domain.user.AttributedUser;
import tech.rollw.disk.web.domain.user.LoginLogService;
import tech.rollw.disk.web.domain.user.UserOperator;
import tech.rollw.disk.web.domain.user.dto.LoginLog;
import tech.rollw.disk.web.domain.user.service.UserManageService;
import tech.rollw.disk.web.domain.user.vo.UserDetailsVo;
import tech.rollw.disk.common.HttpResponseEntity;
import tech.rollw.disk.common.data.page.Page;
import tech.rollw.disk.common.data.page.Pageable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author RollW
 */
@AdminApi
public class UserManageController {
    private final UserManageService userManageService;
    private final LoginLogService loginLogService;
    private final ContextThreadAware<PageableContext> pageableContextAware;
    private final SystemResourceOperatorProvider systemResourceOperatorProvider;

    public UserManageController(UserManageService userManageService,
                                LoginLogService loginLogService,
                                ContextThreadAware<PageableContext> pageableContextAware,
                                SystemResourceOperatorProvider systemResourceOperatorProvider) {
        this.userManageService = userManageService;
        this.loginLogService = loginLogService;
        this.pageableContextAware = pageableContextAware;
        this.systemResourceOperatorProvider = systemResourceOperatorProvider;
    }

    @GetMapping("/users")
    public HttpResponseEntity<List<UserDetailsVo>> getUserList(Pageable pageRequest) {
        List<? extends AttributedUser> userIdentities = userManageService.getUsers(
                pageRequest
        );
        return HttpResponseEntity.success(
                userIdentities.stream().map(
                        UserDetailsVo::of
                ).toList()
        );
    }

    @GetMapping("/users/{id}")
    public HttpResponseEntity<UserDetailsVo> getUserDetails(
            @PathVariable("id") Long userId) {
        AttributedUser user = userManageService.getUser(userId);
        UserDetailsVo userDetailsVo =
                UserDetailsVo.of(user);
        return HttpResponseEntity.success(userDetailsVo);
    }

    @DeleteMapping("/users/{id}")
    public HttpResponseEntity<Void> deleteUser(
            @PathVariable("id") Long userId) {
        UserOperator userOperator = systemResourceOperatorProvider.getSystemResourceOperator(
                new SimpleSystemResource(userId, SystemResourceKind.USER),
                false
        );
        userOperator
                .enableAutoUpdate()
                .delete();

        return HttpResponseEntity.success();
    }

    @PutMapping("/users/{id}/password")
    public HttpResponseEntity<Void> resetUserPassword(
            @PathVariable("id") Long userId,
            @RequestBody OneParameterRequest<String> request) {
        UserOperator userOperator = systemResourceOperatorProvider.getSystemResourceOperator(
                new SimpleSystemResource(userId, SystemResourceKind.USER),
                false
        );
        userOperator.enableAutoUpdate()
                .setPassword(request.value())
                .update();
        return HttpResponseEntity.success();
    }


    @PutMapping("/users/{id}")
    public HttpResponseEntity<Void> updateUser(
            @PathVariable("id") Long userId,
            @RequestBody UserUpdateRequest userUpdateRequest) {
        UserOperator userOperator = systemResourceOperatorProvider.getSystemResourceOperator(
                new SimpleSystemResource(userId, SystemResourceKind.USER),
                false
        );
        userOperator.disableAutoUpdate()
                .setNickname(userUpdateRequest.nickname())
                .setEmail(userUpdateRequest.email())
                .setRole(userUpdateRequest.role())
                .setEnabled(userUpdateRequest.enabled())
                .setCanceled(userUpdateRequest.canceled())
                .setLocked(userUpdateRequest.locked())
                .update();

        return HttpResponseEntity.success();
    }

    @PostMapping("/users")
    public HttpResponseEntity<Void> createUser(@RequestBody UserCreateRequest userCreateRequest) {
        userManageService.createUser(
                userCreateRequest.username(),
                userCreateRequest.password(),
                userCreateRequest.email(),
                userCreateRequest.role(),
                true
        );
        return HttpResponseEntity.success();
    }

    @GetMapping("/users/login/logs")
    public HttpResponseEntity<List<LoginLog>> getLoginLogs(
            Pageable pageable) {
        List<LoginLog> loginLogs = loginLogService.getLogs(pageable);
        long count = loginLogService.getLogsCount();

        return HttpResponseEntity.success(
                Page.of(pageable, count, loginLogs)
        );
    }

}
