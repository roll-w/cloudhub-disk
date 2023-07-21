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

package tech.rollw.disk.web.domain.tag;

/**
 * @author RollW
 */
public record TagKeyword(
        String name,
        int weight
) {
    public TagKeyword {
        if (weight < 0) {
            throw new IllegalArgumentException("weight must be positive");
        }
    }

    public TagKeyword(String name) {
        this(name, 0);
    }

    public static TagKeyword of(String name) {
        return new TagKeyword(name);
    }

    public static TagKeyword of(String name, int weight) {
        return new TagKeyword(name, weight);
    }
}
