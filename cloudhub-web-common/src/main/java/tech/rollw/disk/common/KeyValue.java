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

package tech.rollw.disk.common;

import space.lingu.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author RollW
 */
public record KeyValue(
        String key,
        String value
) {

    public static List<KeyValue> from(@NonNull Map<String, String> map) {
        return map.entrySet().stream()
                .map(entry -> new KeyValue(entry.getKey(), entry.getValue()))
                .toList();
    }

    public static Map<String, String> to(@NonNull List<KeyValue> keyValues) {
        return keyValues.stream()
                .collect(HashMap::new,
                        (map, keyValue) ->
                                map.put(keyValue.key(), keyValue.value()),
                        Map::putAll
                );
    }

}
