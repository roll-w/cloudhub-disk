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

package tech.rollw.disk.web.domain.storagepermission.common;

import tech.rollw.disk.common.BusinessRuntimeException;
import tech.rollw.disk.common.ErrorCode;
import tech.rollw.disk.common.ErrorCodeFinder;
import tech.rollw.disk.common.ErrorCodeMessageProvider;
import space.lingu.NonNull;

import java.util.List;

/**
 *
 * @author RollW
 */
public enum StoragePermissionErrorCode implements ErrorCode, ErrorCodeFinder, ErrorCodeMessageProvider {
    ERROR_STORAGE_PERMISSION("B3300", 400),

    ERROR_PERMISSION_NOT_FOUND("B3301", 404),
    ERROR_PERMISSION_ALREADY_EXIST("B3302", 400),
    ERROR_PERMISSION_NOT_ALLOWED("B3303", 403),
    ERROR_PERMISSION_NOT_ALLOWED_TO_UPDATE("B3305", 403),
    ERROR_PERMISSION_NOT_ALLOWED_TO_CREATE("B3306", 403),
    ERROR_PERMISSION_NOT_ALLOWED_TO_READ("B3307", 403),
    ERROR_PERMISSION_NOT_ALLOWED_TO_WRITE("B3308", 403),

    ERROR_PERMISSION_ASSIGN_NOT_ALLOWED("B3315", 403),
    ERROR_PERMISSION_NOT_ALLOW_USER("B3316", 403),

    ERROR_PERMISSION_TYPE_EMPTY("B3320", 400),
    ;

    private final String value;
    private final int status;

    StoragePermissionErrorCode(String value, int status) {
        this.value = value;
        this.status = status;
    }

    @Override
    public String toString() {
        return "StoragePermissionError: %s, code: %s".formatted(name(), getCode());
    }

    @NonNull
    @Override
    public String getCode() {
        return value;
    }

    @NonNull
    @Override
    public String getName() {
        return name();
    }

    @Override
    public boolean success() {
        return false;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public ErrorCode fromThrowable(Throwable e, ErrorCode defaultErrorCode) {
        if (e instanceof BusinessRuntimeException sys) {
            return sys.getErrorCode();
        }
        return null;
    }

    @Override
    public ErrorCode findErrorCode(String codeValue) {
        return ErrorCodeFinder.from(values(), codeValue);
    }

    private static final List<ErrorCode> CODES = List.of(values());

    @Override
    public List<ErrorCode> listErrorCodes() {
        return CODES;
    }

    public static ErrorCodeFinder getFinderInstance() {
        return ERROR_STORAGE_PERMISSION;
    }
}
