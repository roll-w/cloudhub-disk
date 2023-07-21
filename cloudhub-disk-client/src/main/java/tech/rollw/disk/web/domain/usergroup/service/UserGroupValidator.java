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

package tech.rollw.disk.web.domain.usergroup.service;

import com.google.common.base.Strings;
import tech.rollw.disk.web.domain.systembased.SystemResourceKind;
import tech.rollw.disk.web.domain.systembased.validate.FieldType;
import tech.rollw.disk.web.domain.systembased.validate.UnsupportedFieldException;
import tech.rollw.disk.web.domain.systembased.validate.Validator;
import tech.rollw.disk.web.domain.usergroup.common.UserGroupErrorCode;
import tech.rollw.disk.web.domain.usergroup.common.UserGroupException;
import tech.rollw.disk.common.BusinessRuntimeException;
import tech.rollw.disk.common.CommonErrorCode;
import tech.rollw.disk.common.ErrorCode;
import org.springframework.stereotype.Component;
import space.lingu.NonNull;

/**
 * @author RollW
 */
@Component
public class UserGroupValidator implements Validator {
    @Override
    public boolean supports(SystemResourceKind systemResourceKind) {
        return systemResourceKind == SystemResourceKind.USER_GROUP;
    }

    @NonNull
    @Override
    public ErrorCode validate(String value, @NonNull FieldType fieldType) {
        return switch (fieldType) {
            case NAME -> {
                if (Strings.isNullOrEmpty(value) || value.length() < 2 || value.length() > 32) {
                    yield UserGroupErrorCode.ERROR_GROUP_NAME_INVALID;
                }
                if (value.equals("default")) {
                    yield UserGroupErrorCode.ERROR_GROUP_NAME_EXIST;
                }

                yield CommonErrorCode.SUCCESS;
            }
            case DESCRIPTION -> {
                if (Strings.isNullOrEmpty(value)) {
                    yield CommonErrorCode.SUCCESS;
                }
                if (value.length() > 100) {
                    yield UserGroupErrorCode.ERROR_GROUP_DESCRIPTION_INVALID;
                }
                yield CommonErrorCode.SUCCESS;
            }
            default -> throw new UnsupportedFieldException(fieldType);
        };
    }

    @Override
    public void validateThrows(String value, @NonNull FieldType fieldType)
            throws BusinessRuntimeException {
        ErrorCode errorCode = validate(value, fieldType);
        if (errorCode.failed()) {
            throw new UserGroupException(errorCode);
        }
    }
}
