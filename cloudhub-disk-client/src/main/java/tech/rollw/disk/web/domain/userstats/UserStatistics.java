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

package tech.rollw.disk.web.domain.userstats;

import tech.rollw.disk.web.database.DataItem;
import tech.rollw.disk.web.domain.user.LegalUserType;
import space.lingu.light.*;

import java.util.Map;

/**
 * @author RollW
 */
@DataTable(name = "user_statistics", indices = {
        @Index(value = {"user_id", "user_type"}, unique = true)
})
public class UserStatistics implements DataItem {
    @DataColumn(name = "id")
    @PrimaryKey(autoGenerate = true)
    private final Long id;

    @DataColumn(name = "user_id")
    private final long userId;

    @DataColumn(name = "user_type")
    private final LegalUserType userType;

    @DataColumn(name = "statistics", dataType = SQLDataType.LONGTEXT)
    private final Map<String, Long> statistics;

    public UserStatistics(Long id, long userId,
                          LegalUserType userType,
                          Map<String, Long> statistics) {
        this.id = id;
        this.userId = userId;
        this.userType = userType;
        this.statistics = statistics;
    }

    @Override
    public Long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public LegalUserType getUserType() {
        return userType;
    }

    public Map<String, Long> getStatistics() {
        return statistics;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    public static final class Builder {
        private Long id;
        private long userId;
        private LegalUserType userType;
        private Map<String, Long> statistics;

        private Builder() {
        }

        private Builder(UserStatistics userstatistics) {
            this.id = userstatistics.id;
            this.userId = userstatistics.userId;
            this.userType = userstatistics.userType;
            this.statistics = userstatistics.statistics;
        }

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setUserId(long userId) {
            this.userId = userId;
            return this;
        }

        public Builder setUserType(LegalUserType userType) {
            this.userType = userType;
            return this;
        }

        public Builder setStatistics(Map<String, Long> statistics) {
            this.statistics = statistics;
            return this;
        }

        public UserStatistics build() {
            return new UserStatistics(id, userId, userType, statistics);
        }
    }
}
