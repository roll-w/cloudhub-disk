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
import space.lingu.light.DataColumn;
import space.lingu.light.DataTable;
import space.lingu.light.Index;
import space.lingu.light.PrimaryKey;

import java.time.LocalDate;
import java.util.Map;

/**
 * @author RollW
 */
@DataTable(name = "dated_statistics", indices = {
     @Index(value = {"key", "date"}, unique = true)
})
public class DatedStatistics implements DataItem {
    @DataColumn(name = "id")
    @PrimaryKey(autoGenerate = true)
    private final Long id;

    @DataColumn(name = "key")
    private final String key;

    @DataColumn(name = "value")
    private final Map<String, String> value;

    @DataColumn(name = "date")
    private final LocalDate date;

    public DatedStatistics(Long id, String key,
                           Map<String, String> value, LocalDate date) {
        this.id = id;
        this.key = key;
        this.value = value;
        this.date = date;
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

    public LocalDate getDate() {
        return date;
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
        private LocalDate date;

        private Builder() {
        }

        private Builder(DatedStatistics datedstatistics) {
            this.id = datedstatistics.id;
            this.key = datedstatistics.key;
            this.value = datedstatistics.value;
            this.date = datedstatistics.date;
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

        public Builder setDate(LocalDate date) {
            this.date = date;
            return this;
        }

        public DatedStatistics build() {
            return new DatedStatistics(id, key, value, date);
        }
    }

}
