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

package tech.rollw.disk.web.domain.storageprocess.service;

import tech.rollw.disk.web.domain.storageprocess.StorageProcessingEventRegistry;
import tech.rollw.disk.web.domain.storageprocess.StorageProcessingEventTypes;
import tech.rollw.disk.web.domain.storageprocess.StorageProcessingEvent;
import tech.rollw.disk.web.event.EventCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author RollW
 */
@Service
public class StorageProcessingRegistryService
        implements StorageProcessingEventRegistry, StorageProcessingCallback {
    private static final Logger logger = LoggerFactory.getLogger(StorageProcessingRegistryService.class);

    private final Map<String, Integer> idToIndex = new HashMap<>();
    private final Map<String, Callback> callbacks
            = new HashMap<>();

    public StorageProcessingRegistryService() {
    }

    @Override
    public String register(EventCallback<StorageProcessingEvent> eventCallback,
                           StorageProcessingEventTypes messagePattern) {
        String id = toId(eventCallback);
        if (callbacks.containsKey(id)) {
            logger.error("EventCallback {} already registered, it should not happen.", id);
        }
        callbacks.put(id, new Callback(eventCallback, messagePattern));
        return id;
    }

    private String toId(EventCallback<StorageProcessingEvent> eventCallback) {
        String name = eventCallback.getClass().getSimpleName();
        Integer index = idToIndex.getOrDefault(name, 0) + 1;
        idToIndex.put(name, index);
        return name + "-" + index;
    }

    @Override
    public void unregister(String eventId) {
        callbacks.remove(eventId);
    }

    @Override
    public void onProcessed(StorageProcessingEvent storageProcessingEvent) {
        callbacks.forEach((id, callback) -> {
            if (callback.messagePattern().contains(
                    storageProcessingEvent.getStorageProcessingEventType())) {
                callback.eventCallback().onEvent(storageProcessingEvent);
            }
        });
    }

    private record Callback(
            EventCallback<StorageProcessingEvent> eventCallback,
            StorageProcessingEventTypes messagePattern
    ) {
    }
}
