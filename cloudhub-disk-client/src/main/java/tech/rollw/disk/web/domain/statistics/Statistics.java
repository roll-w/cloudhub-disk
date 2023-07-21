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

package tech.rollw.disk.web.domain.statistics;

import tech.rollw.disk.web.database.DataItem;
import space.lingu.light.*;

import java.util.Map;

/**
 * @author RollW
 */
@DataTable(name = "statistics", indices = {
        @Index(value = "key", unique = true)
})
public class Statistics implements DataItem {
    @DataColumn(name = "id")
    @PrimaryKey(autoGenerate = true)
    private final Long id;

    @DataColumn(name = "key")
    private final String key;

    @DataColumn(name = "value", dataType = SQLDataType.LONGTEXT)
    private final Map<String, String> value;

    public Statistics(Long id, String key, Map<String, String> value) {
        this.id = id;
        this.key = key;
        this.value = value;
    }

    @Override
    public Long getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public Map<String, String> getValue() {
        return value;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static final class Builder {
        private Long id;
        private String key;
        private Map<String, String> value;

        private Builder() {
        }

        private Builder(Statistics statistics) {
            this.id = statistics.id;
            this.key = statistics.key;
            this.value = statistics.value;
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setKey(String key) {
            this.key = key;
            return this;
        }

        public Builder setValue(Map<String, String> value) {
            this.value = value;
            return this;
        }

        public Statistics build() {
            return new Statistics(id, key, value);
        }
    }
}
