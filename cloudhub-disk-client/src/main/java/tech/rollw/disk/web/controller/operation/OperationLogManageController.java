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

package tech.rollw.disk.web.controller.operation;

import tech.rollw.disk.web.controller.AdminApi;
import tech.rollw.disk.web.domain.operatelog.OperationLog;
import tech.rollw.disk.web.domain.operatelog.OperationLogCountProvider;
import tech.rollw.disk.web.domain.operatelog.OperationService;
import tech.rollw.disk.web.domain.operatelog.dto.OperationLogDto;
import tech.rollw.disk.web.domain.operatelog.vo.OperationLogVo;
import tech.rollw.disk.web.domain.systembased.SimpleSystemResource;
import tech.rollw.disk.web.domain.systembased.SystemResourceException;
import tech.rollw.disk.web.domain.systembased.SystemResourceKind;
import tech.rollw.disk.web.domain.user.AttributedUser;
import tech.rollw.disk.web.domain.user.service.UserManageService;
import tech.rollw.disk.web.domain.user.service.UserSearchService;
import tech.rollw.disk.web.system.pages.PageableInterceptor;
import tech.rollw.disk.common.DataErrorCode;
import tech.rollw.disk.common.HttpResponseEntity;
import tech.rollw.disk.common.data.page.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author RollW
 */
@AdminApi
public class OperationLogManageController {
    private final OperationService operationService;
    private final OperationLogCountProvider operationLogCountProvider;
    private final UserSearchService userSearchService;
    private final UserManageService userManageService;
    private final PageableInterceptor pageableInterceptor;

    public OperationLogManageController(OperationService operationService,
                                        OperationLogCountProvider operationLogCountProvider,
                                        UserSearchService userSearchService,
                                        UserManageService userManageService,
                                        PageableInterceptor pageableInterceptor) {
        this.operationService = operationService;
        this.operationLogCountProvider = operationLogCountProvider;
        this.userSearchService = userSearchService;
        this.userManageService = userManageService;
        this.pageableInterceptor = pageableInterceptor;
    }


    @GetMapping("/{systemResourceKind}/{systemResourceId}/operations/logs")
    public HttpResponseEntity<List<OperationLogVo>> getOperationLogs(
            @PathVariable("systemResourceKind") String kind,
            @PathVariable("systemResourceId") Long systemResourceId,
            Pageable pageable) {
        SystemResourceKind systemResourceKind =
                SystemResourceKind.from(kind);
        if (systemResourceKind == null) {
            throw new SystemResourceException(DataErrorCode.ERROR_DATA_NOT_EXIST,
                    "Not found system resource kind: " + kind);
        }
        List<OperationLogDto> operationLogDtos = operationService.getOperationsByResource(
                new SimpleSystemResource(systemResourceId, systemResourceKind)
        );
        List<Long> userIds = operationLogDtos.stream()
                .map(OperationLogDto::operatorId)
                .distinct()
                .toList();
        List<? extends AttributedUser> attributedUsers =
                userSearchService.findUsers(userIds);
        List<OperationLogVo> results = OperationLogVoUtils.convertToVo(
                operationLogDtos, attributedUsers);
        return HttpResponseEntity.success(
                pageableInterceptor.interceptPageable(
                        results,
                        pageable,
                        () -> operationLogCountProvider.getOperationLogCount(
                                systemResourceId,
                                systemResourceKind
                        )
                )
        );
    }

    @GetMapping("/operations/logs")
    public HttpResponseEntity<List<OperationLogVo>> getOperationLogs(
            Pageable pageable) {
        List<OperationLogDto> operationLogDtos =
                operationService.getOperations(pageable);
        List<Long> userIds = operationLogDtos.stream()
                .map(OperationLogDto::operatorId)
                .distinct()
                .toList();
        List<? extends AttributedUser> attributedUsers =
                userSearchService.findUsers(userIds);
        List<OperationLogVo> results = OperationLogVoUtils.convertToVo(
                operationLogDtos, attributedUsers);
        return HttpResponseEntity.success(
                pageableInterceptor.interceptPageable(
                        results,
                        pageable,
                        OperationLog.class
                )
        );
    }


    @GetMapping("/user/{userId}/operations/logs")
    public HttpResponseEntity<List<OperationLogVo>> getOperationLogsByUser(
            @PathVariable("userId") Long userId,
            Pageable pageable) {
        // current user
        AttributedUser attributedUser = userManageService.getUser(userId);
        List<OperationLogDto> operationLogDtos = operationService.getOperationsByUserId(
                userId,
                pageable
        );
        List<? extends AttributedUser> attributedUsers =
                List.of(attributedUser);
        List<OperationLogVo> results = OperationLogVoUtils.convertToVo(
                operationLogDtos, attributedUsers);

        return HttpResponseEntity.success(
                pageableInterceptor.interceptPageable(
                        results, pageable,
                        () -> operationLogCountProvider.getOperationLogCount(
                                attributedUser.getOperatorId()
                        )
                )
        );
    }
}
