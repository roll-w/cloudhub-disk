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

package tech.rollw.disk.web.domain.favorites.service;

import com.google.common.base.Strings;
import tech.rollw.disk.web.domain.favorites.common.FavoriteErrorCode;
import tech.rollw.disk.web.domain.systembased.SystemResourceKind;
import tech.rollw.disk.web.domain.systembased.validate.FieldType;
import tech.rollw.disk.web.domain.systembased.validate.UnsupportedFieldException;
import tech.rollw.disk.web.domain.systembased.validate.Validator;
import tech.rollw.disk.common.CommonErrorCode;
import tech.rollw.disk.common.ErrorCode;
import org.springframework.stereotype.Component;
import space.lingu.NonNull;

/**
 * @author RollW
 */
@Component
public class FavoriteValidator implements Validator {
    @Override
    public boolean supports(SystemResourceKind systemResourceKind) {
        return systemResourceKind == SystemResourceKind.FAVORITE_GROUP;
    }

    private static final String[] PRESERVE_NAMES = {"default", "recycle-bin"};

    @NonNull
    @Override
    public ErrorCode validate(String value, @NonNull FieldType fieldType) {
        if (fieldType != FieldType.NAME) {
            throw new UnsupportedFieldException(fieldType);
        }
        if (Strings.isNullOrEmpty(value)) {
            return FavoriteErrorCode.ERROR_FAVORITE_NAME_NON_COMPLIANCE;
        }
        for (String preserveName : PRESERVE_NAMES) {
            if (preserveName.equals(value)) {
                return FavoriteErrorCode.ERROR_FAVORITE_NAME_NON_COMPLIANCE;
            }
        }
        if (value.length() > 20) {
            return FavoriteErrorCode.ERROR_FAVORITE_NAME_NON_COMPLIANCE;
        }
        return CommonErrorCode.SUCCESS;
    }
}
