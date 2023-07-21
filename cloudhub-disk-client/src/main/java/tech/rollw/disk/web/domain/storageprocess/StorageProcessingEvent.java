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

package tech.rollw.disk.web.domain.storageprocess;

import tech.rollw.disk.web.domain.tag.TaggedValue;
import tech.rollw.disk.web.domain.userstorage.AttributedStorage;

import java.util.List;

/**
 * @author RollW
 */
public class StorageProcessingEvent {
    private final StorageProcessingEventType storageProcessingEventType;
    private final AttributedStorage storage;
    private final long size;
    private final List<TaggedValue> taggedValues;

    public StorageProcessingEvent(StorageProcessingEventType storageProcessingEventType,
                                  AttributedStorage storage,
                                  long size,
                                  List<TaggedValue> taggedValues) {
        this.storageProcessingEventType = storageProcessingEventType;
        this.storage = storage;
        this.size = size;
        this.taggedValues = taggedValues;
    }

    public StorageProcessingEventType getStorageProcessingEventType() {
        return storageProcessingEventType;
    }

    public AttributedStorage getStorage() {
        return storage;
    }

    public long getSize() {
        return size;
    }

    public List<TaggedValue> getTaggedValues() {
        return taggedValues;
    }
}
