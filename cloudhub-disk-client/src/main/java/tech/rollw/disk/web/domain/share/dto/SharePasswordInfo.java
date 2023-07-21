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

package tech.rollw.disk.web.domain.share.dto;

import tech.rollw.disk.web.domain.share.UserShare;
import tech.rollw.disk.web.domain.userstorage.StorageType;

/**
 * @author RollW
 */
public record SharePasswordInfo(
        long id,
        long storageId,
        StorageType storageType,
        long creatorId,
        String shareCode,
        // as a utility field, can be removed if not needed
        boolean isPublic,
        long expireTime,
        long createTime,
        String password
) {

    public boolean isExpired(long time) {
        if (expireTime <= 0) {
            return false;
        }
        return time > expireTime;
    }

    public static SharePasswordInfo from(UserShare userShare) {
        if (userShare == null) {
            return null;
        }

        return new SharePasswordInfo(
                userShare.getId(),
                userShare.getStorageId(),
                userShare.getStorageType(),
                userShare.getUserId(),
                userShare.getShareId(),
                userShare.isPublic(),
                userShare.getExpireTime(),
                userShare.getCreateTime(),
                userShare.getPassword()
        );
    }
}
