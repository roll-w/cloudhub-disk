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

package tech.rollw.disk.web.domain.operatelog.dto;

import tech.rollw.disk.web.domain.operatelog.Action;
import tech.rollw.disk.web.domain.operatelog.OperateType;
import tech.rollw.disk.web.domain.operatelog.OperationLog;
import tech.rollw.disk.web.domain.operatelog.OperationLogAssociation;
import tech.rollw.disk.web.domain.systembased.SystemResourceKind;

/**
 * @author RollW
 */
public record OperationLogDto(
        long id,
        long operatorId,
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

    public static OperationLogDto from(OperationLog operationLog,
                                       OperateType operateType) {
        if (operationLog == null || operateType == null) {
            return null;
        }
        return new OperationLogDto(
                operationLog.getId(),
                operationLog.getOperator(),
                operationLog.getOperateResourceId(),
                operationLog.getSystemResourceKind(),
                operationLog.getOperateType(),
                operationLog.getAction(),
                operateType.getName(),
                operateType.getDescription(),
                operationLog.getAddress(),
                operationLog.getOperateTime(),
                operationLog.getOriginContent(),
                operationLog.getChangedContent(),
                0
        );
    }

    public static OperationLogDto from(OperationLog operationLog,
                                       OperationLogAssociation operationLogAssociation,
                                       OperateType operateType) {
        if (operationLog == null || operationLogAssociation == null) {
            return null;
        }
        return new OperationLogDto(
                operationLog.getId(),
                operationLog.getOperator(),
                operationLogAssociation.getResourceId(),
                operationLogAssociation.getResourceKind(),
                operationLog.getOperateType(),
                operationLog.getAction(),
                operateType.getName(),
                operateType.getDescription(),
                operationLog.getAddress(),
                operationLog.getOperateTime(),
                operationLog.getOriginContent(),
                operationLog.getChangedContent(),
                operationLog.getId()
        );
    }
}
