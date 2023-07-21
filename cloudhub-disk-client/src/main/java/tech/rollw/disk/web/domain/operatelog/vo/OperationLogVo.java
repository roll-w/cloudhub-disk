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

package tech.rollw.disk.web.domain.operatelog.vo;

import tech.rollw.disk.web.domain.operatelog.Action;
import tech.rollw.disk.web.domain.operatelog.dto.OperationLogDto;
import tech.rollw.disk.web.domain.systembased.SystemResourceKind;
import tech.rollw.disk.web.domain.user.AttributedUser;

/**
 * @author RollW
 */
public record OperationLogVo(
        long id,
        long operatorId,
        String username,
        String nickname,
        long resourceId,
        SystemResourceKind resourceKind,
        long typeId,
        Action action,
        String name,
        String description,
        String address,
        long timestamp,
        String originContent,
        String changedContent,
        long associatedTo
) {



    public static OperationLogVo of(OperationLogDto operationLogDto) {
        return new OperationLogVo(
                operationLogDto.id(),
                operationLogDto.operatorId(),
                null,
                null,
                operationLogDto.resourceId(),
                operationLogDto.resourceKind(),
                operationLogDto.typeId(),
                operationLogDto.action(),
                operationLogDto.name(),
                operationLogDto.description(),
                operationLogDto.address(),
                operationLogDto.timestamp(),
                operationLogDto.originContent(),
                operationLogDto.changedContent(),
                operationLogDto.associatedTo()
        );
    }

    public static OperationLogVo of(OperationLogDto operationLogDto,
                                    AttributedUser attributedUser) {
        return new OperationLogVo(
                operationLogDto.id(),
                operationLogDto.operatorId(),
                attributedUser.getUsername(),
                attributedUser.getNickname(),
                operationLogDto.resourceId(),
                operationLogDto.resourceKind(),
                operationLogDto.typeId(),
                operationLogDto.action(),
                operationLogDto.name(),
                operationLogDto.description(),
                operationLogDto.address(),
                operationLogDto.timestamp(),
                operationLogDto.originContent(),
                operationLogDto.changedContent(),
                operationLogDto.associatedTo()
        );
    }
}
