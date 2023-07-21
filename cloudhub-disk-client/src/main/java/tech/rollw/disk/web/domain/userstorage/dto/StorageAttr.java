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

package tech.rollw.disk.web.domain.userstorage.dto;

import tech.rollw.disk.web.domain.operatelog.Operator;
import tech.rollw.disk.web.domain.userstorage.FileType;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author RollW
 */
public record StorageAttr(
        String fileName,
        byte[] content,
        String suffix,
        FileType parsedFileType,
        String fileId,
        long size,
        Operator operator
) {
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StorageAttr that)) return false;
        return Objects.equals(fileName, that.fileName) && Arrays.equals(content, that.content) && Objects.equals(suffix, that.suffix) && parsedFileType == that.parsedFileType && Objects.equals(fileId, that.fileId);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(fileName, suffix, parsedFileType, fileId);
        result = 31 * result + Arrays.hashCode(content);
        return result;
    }

    @Override
    public String toString() {
        return "StorageAttr{" +
                "fileName='" + fileName + '\'' +
                ", content=" + Arrays.toString(content) +
                ", suffix='" + suffix + '\'' +
                ", parsedFileType=" + parsedFileType +
                ", fileId='" + fileId + '\'' +
                '}';
    }


}
