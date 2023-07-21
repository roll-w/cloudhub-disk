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

import tech.rollw.disk.web.common.ApiContextHolder;
import tech.rollw.disk.web.controller.Api;
import tech.rollw.disk.web.domain.operatelog.OperationLogCountProvider;
import tech.rollw.disk.web.domain.operatelog.OperationService;
import tech.rollw.disk.web.domain.operatelog.dto.OperationLogDto;
import tech.rollw.disk.web.domain.operatelog.vo.OperationLogVo;
import tech.rollw.disk.web.domain.systembased.SimpleSystemResource;
import tech.rollw.disk.web.domain.systembased.SystemResourceException;
import tech.rollw.disk.web.domain.systembased.SystemResourceKind;
import tech.rollw.disk.web.domain.user.AttributedUser;
import tech.rollw.disk.web.domain.user.UserIdentity;
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
@Api
public class OperationLogController {
    private final OperationService operationService;
    private final OperationLogCountProvider operationLogCountProvider;
    private final UserSearchService userSearchService;
    private final PageableInterceptor pageableInterceptor;

    public OperationLogController(OperationService operationService,
                                  OperationLogCountProvider operationLogCountProvider,
                                  UserSearchService userSearchService,
                                  PageableInterceptor pageableInterceptor) {
        this.operationService = operationService;
        this.operationLogCountProvider = operationLogCountProvider;
        this.userSearchService = userSearchService;
        this.pageableInterceptor = pageableInterceptor;
    }

    @GetMapping("/{systemResourceKind}/{systemResourceId}/operations/logs")
    public HttpResponseEntity<List<OperationLogVo>> getOperationLogs(
            @PathVariable("systemResourceKind") String kind,
            @PathVariable("systemResourceId") Long systemResourceId,
            Pageable pageable) {
        SystemResourceKind systemResourceKind =
                SystemResourceKind.from(kind);
        // TODO: auth system resource
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


    @GetMapping("/user/operations/logs")
    public HttpResponseEntity<List<OperationLogVo>> getOperationLogsByUser(Pageable pageable) {
        // current user
        UserIdentity userIdentity = ApiContextHolder.getContext().userInfo();
        List<OperationLogDto> operationLogDtos = operationService.getOperationsByUserId(
                userIdentity.getUserId(),
                pageable
        );

        List<? extends AttributedUser> attributedUsers =
                List.of(userSearchService.findUser(userIdentity));
        List<OperationLogVo> results = OperationLogVoUtils.convertToVo(
                operationLogDtos, attributedUsers);

        return HttpResponseEntity.success(
                pageableInterceptor.interceptPageable(
                        results, pageable,
                        () -> operationLogCountProvider.getOperationLogCount(
                                userIdentity.getOperatorId()
                        )
                )
        );
    }

}
