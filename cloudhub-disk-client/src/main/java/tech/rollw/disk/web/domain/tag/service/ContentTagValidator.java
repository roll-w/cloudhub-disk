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

package tech.rollw.disk.web.domain.tag.service;

import com.google.common.base.Strings;
import tech.rollw.disk.web.domain.systembased.SystemResourceKind;
import tech.rollw.disk.web.domain.systembased.validate.FieldType;
import tech.rollw.disk.web.domain.systembased.validate.UnsupportedFieldException;
import tech.rollw.disk.web.domain.systembased.validate.Validator;
import tech.rollw.disk.web.domain.tag.common.ContentTagErrorCode;
import tech.rollw.disk.common.CommonErrorCode;
import tech.rollw.disk.common.ErrorCode;
import tech.rollw.disk.common.WebCommonErrorCode;
import org.springframework.stereotype.Component;
import space.lingu.NonNull;

/**
 * @author RollW
 */
@Component
public class ContentTagValidator implements Validator {
    @Override
    public boolean supports(SystemResourceKind systemResourceKind) {
        return systemResourceKind == SystemResourceKind.TAG;
    }

    @NonNull
    @Override
    public ErrorCode validate(String value, @NonNull FieldType fieldType) {
        return switch (fieldType) {
            case NAME -> validateName(value);
            case DESCRIPTION -> validateDescription(value);
            default -> throw new UnsupportedFieldException(fieldType);
        };
    }

    private ErrorCode validateName(String name) {
        if (Strings.isNullOrEmpty(name)) {
            return WebCommonErrorCode.ERROR_PARAM_MISSING;
        }
        if (name.length() > 20 || name.length() < 2) {
            return ContentTagErrorCode.ERROR_NAME_INVALID;
        }
        return CommonErrorCode.SUCCESS;
    }

    private ErrorCode validateDescription(String description) {
        if (Strings.isNullOrEmpty(description)) {
            return WebCommonErrorCode.ERROR_PARAM_MISSING;
        }
        if (description.length() > 100) {
            return ContentTagErrorCode.ERROR_DESCRIPTION_TOO_LONG;
        }
        return CommonErrorCode.SUCCESS;
    }
}
