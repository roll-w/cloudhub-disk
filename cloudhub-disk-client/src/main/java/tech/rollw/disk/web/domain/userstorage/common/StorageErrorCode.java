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

package tech.rollw.disk.web.domain.userstorage.common;

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
public enum StorageErrorCode implements ErrorCode, ErrorCodeFinder, ErrorCodeMessageProvider {
    ERROR_STORAGE("B3100", 400),
    ERROR_DIRECTORY_NOT_EXIST("B3101", 404),
    ERROR_DIRECTORY_EXISTED("B3102", 400),
    ERROR_DIRECTORY_EMPTY("B3103", 400),
    ERROR_DIRECTORY_NOT_EMPTY("B3104", 400),

    ERROR_NAME_EMPTY("B3110", 400),
    ERROR_NAME_TOO_LONG("B3111", 400),
    ERROR_NAME_TOO_SHORT("B3112", 400),
    ERROR_NAME_ILLEGAL("B3113", 400),


    ERROR_FILE_NOT_EXIST("B3151", 404),
    ERROR_FILE_EXISTED("B3152", 400),
    ERROR_FILE_EMPTY("B3153", 400),
    ERROR_FILE_NOT_EMPTY("B3154", 400),
    ERROR_SAME_FILE("B3155", 400),
    ERROR_TARGET_DIRECTORY_NOT_EXIST("B3156", 400),
    ERROR_SAME_DIRECTORY("B3157", 400),

    ERROR_FILE_NOT_DELETED("B3160", 400),
    ERROR_FILE_ALREADY_DELETED("B3161", 400),
    ERROR_DIRECTORY_NOT_DELETED("B3162", 400),
    ERROR_DIRECTORY_ALREADY_DELETED("B3163", 400),

    ERROR_STORAGE_SIZE_LIMIT("B3164", 400),
    ERROR_STORAGE_COUNT_LIMIT("B3165", 400),

    ;


    private final String value;
    private final int status;

    StorageErrorCode(String value, int status) {
        this.value = value;
        this.status = status;
    }

    @Override
    public String toString() {
        return "StorageError: %s, code: %s".formatted(name(), getCode());
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
        return ERROR_STORAGE;
    }
}
