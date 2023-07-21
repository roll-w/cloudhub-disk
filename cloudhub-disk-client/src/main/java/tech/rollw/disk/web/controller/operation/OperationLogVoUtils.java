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

import tech.rollw.disk.web.common.CloudhubBizRuntimeException;
import tech.rollw.disk.web.domain.operatelog.dto.OperationLogDto;
import tech.rollw.disk.web.domain.operatelog.vo.OperationLogVo;
import tech.rollw.disk.web.domain.user.AttributedUser;
import tech.rollw.disk.web.domain.user.service.UserSearchService;
import tech.rollw.disk.common.DataErrorCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RollW
 */
public final class OperationLogVoUtils {

    public static List<OperationLogVo> convertToVo(List<OperationLogDto> operationLogDtos,
                                                   List<? extends AttributedUser> attributedUsers) {
        List<OperationLogVo> results = new ArrayList<>();
        for (OperationLogDto operationLogDto : operationLogDtos) {
            AttributedUser attributedUser = binarySearch(operationLogDto.operatorId(), attributedUsers);
            results.add(OperationLogVo.of(operationLogDto, attributedUser));
        }
        return results;
    }


    private static AttributedUser binarySearch(long userId,
                                               List<? extends AttributedUser> attributedUsers) {
        AttributedUser attributedUser =
                UserSearchService.binarySearch(userId, attributedUsers);
        if (attributedUser == null) {
            throw new CloudhubBizRuntimeException(DataErrorCode.ERROR_DATA_NOT_EXIST,
                    "Not found user: " + userId);
        }
        return attributedUser;
    }


    private OperationLogVoUtils() {
    }
}
