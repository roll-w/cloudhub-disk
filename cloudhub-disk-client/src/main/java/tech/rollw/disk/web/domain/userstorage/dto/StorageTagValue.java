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

import tech.rollw.disk.web.domain.tag.NameValue;
import tech.rollw.disk.web.domain.tag.TaggedValue;
import tech.rollw.disk.web.domain.userstorage.StorageMetadata;

/**
 * @author RollW
 */
public record StorageTagValue(
        long id,
        long tagGroupId,
        long tagId,
        String name,
        String value
) implements TaggedValue {

    @Override
    public long groupId() {
        return tagGroupId;
    }
    public static final long INVALID_ID = -1;

    public static StorageTagValue of(NameValue nameValue) {
        return new StorageTagValue(
                INVALID_ID,
                INVALID_ID,
                INVALID_ID,
                nameValue.name(),
                nameValue.value()
        );
    }

    public static StorageTagValue of(String name, String value) {
        return new StorageTagValue(
                INVALID_ID,
                INVALID_ID,
                INVALID_ID,
                name, value
        );
    }

    public static StorageTagValue of(StorageMetadata storageMetadata,
                                 String name, String value) {
        if (storageMetadata == null) {
            return null;
        }
        return new StorageTagValue(
                storageMetadata.getId(),
                storageMetadata.getTagGroupId(),
                storageMetadata.getTagId(),
                name, value
        );
    }

}
