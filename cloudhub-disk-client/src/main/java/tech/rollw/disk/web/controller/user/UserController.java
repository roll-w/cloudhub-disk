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

import tech.rollw.disk.web.common.ApiContextHolder;
import tech.rollw.disk.web.common.ParamValidate;
import tech.rollw.disk.web.controller.Api;
import tech.rollw.disk.web.controller.PairParameterRequest;
import tech.rollw.disk.web.domain.authentication.AuthenticationException;
import tech.rollw.disk.web.domain.systembased.ContextThread;
import tech.rollw.disk.web.domain.systembased.ContextThreadAware;
import tech.rollw.disk.web.domain.systembased.SystemResourceOperatorProvider;
import tech.rollw.disk.web.domain.systembased.paged.PageableContext;
import tech.rollw.disk.web.domain.user.AttributedUser;
import tech.rollw.disk.web.domain.user.LoginLogService;
import tech.rollw.disk.web.domain.user.UserIdentity;
import tech.rollw.disk.web.domain.user.UserOperator;
import tech.rollw.disk.web.domain.user.dto.LoginLog;
import tech.rollw.disk.web.domain.user.service.UserSearchService;
import tech.rollw.disk.web.domain.user.vo.UserCommonDetailsVo;
import tech.rollw.disk.common.AuthErrorCode;
import tech.rollw.disk.common.BusinessRuntimeException;
import tech.rollw.disk.common.HttpResponseEntity;
import tech.rollw.disk.common.data.page.Page;
import tech.rollw.disk.common.data.page.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author RollW
 */
@Api
public class UserController {
    private final UserSearchService userSearchService;
    private final LoginLogService loginLogService;
    private final SystemResourceOperatorProvider systemResourceOperatorProvider;
    private final ContextThreadAware<PageableContext> pageableContextThreadAware;

    public UserController(UserSearchService userSearchService,
                          LoginLogService loginLogService,
                          SystemResourceOperatorProvider systemResourceOperatorProvider,
                          ContextThreadAware<PageableContext> pageableContextThreadAware) {
        this.userSearchService = userSearchService;
        this.loginLogService = loginLogService;
        this.systemResourceOperatorProvider = systemResourceOperatorProvider;
        this.pageableContextThreadAware = pageableContextThreadAware;
    }

    @GetMapping("/user")
    public HttpResponseEntity<UserCommonDetailsVo> getAuthenticatedUser() {
        ApiContextHolder.ApiContext context = ApiContextHolder.getContext();
        UserIdentity userInfo = context.userInfo();
        if (userInfo == null) {
            throw new BusinessRuntimeException(AuthErrorCode.ERROR_UNAUTHORIZED_USE);
        }
        AttributedUser attributedUser =
                userSearchService.findUser(userInfo.getUserId());
        return HttpResponseEntity.success(
                UserCommonDetailsVo.of(attributedUser)
        );
    }

    @PutMapping("/user")
    public HttpResponseEntity<Void> updateUserInfo(
            @RequestBody UserUpdateRequest request
    ) {
        UserIdentity userIdentity = ApiContextHolder.getContext().userInfo();

        UserOperator userOperator = systemResourceOperatorProvider
                .getSystemResourceOperator(userIdentity, true);
        userOperator.disableAutoUpdate()
                .setNickname(request.nickname())
                .setEmail(request.email())
                .update();
        return HttpResponseEntity.success();
    }

    @PutMapping("/user/password")
    public HttpResponseEntity<Void> resetUserPassword(
            @RequestBody PairParameterRequest<String, String> request
    ) {
        UserIdentity userIdentity = ApiContextHolder.getContext().userInfo();
        UserOperator userOperator = systemResourceOperatorProvider
                .getSystemResourceOperator(userIdentity, true);
        userOperator.disableAutoUpdate()
                .setPassword(request.first(), request.second())
                .update();
        return HttpResponseEntity.success();
    }

    @GetMapping("/users/{userId}")
    public HttpResponseEntity<UserCommonDetailsVo> getUserInfo(
            @PathVariable("userId") Long userId) {
        AttributedUser attributedUser = userSearchService.findUser(userId);
        return HttpResponseEntity.success(
                UserCommonDetailsVo.of(attributedUser)
        );
    }

    @GetMapping("/users/search")
    public HttpResponseEntity<List<UserCommonDetailsVo>> searchUsers(
            @RequestParam("keyword") String keyword) {
        ParamValidate.notEmpty(keyword, "keyword");
        ContextThread<PageableContext> contextThread =
                pageableContextThreadAware.getContextThread();
        PageableContext pageableContext = contextThread.getContext();
        pageableContext.setIncludeDeleted(false);

        List<AttributedUser> attributedUsers =
                userSearchService.findUsers(keyword);

        List<UserCommonDetailsVo> userCommonDetailsVos =
                attributedUsers.stream()
                        .map(UserCommonDetailsVo::of)
                        .toList();
        return HttpResponseEntity.success(
                pageableContext.toPage(userCommonDetailsVos)
        );
    }

    @GetMapping("/user/login/logs")
    public HttpResponseEntity<List<LoginLog>> getUserLoginLogs(Pageable pageable) {
        UserIdentity userIdentity =
                ApiContextHolder.getContext().userInfo();
        if (userIdentity == null) {
            throw new AuthenticationException(AuthErrorCode.ERROR_INVALID_TOKEN);
        }
        List<LoginLog> loginLogs =
                loginLogService.getUserLogs(userIdentity.getUserId(), pageable);
        return HttpResponseEntity.success(Page.of(
                pageable,
                loginLogService.getUserLogsCount(userIdentity.getUserId()),
                loginLogs
        ));
    }
}
