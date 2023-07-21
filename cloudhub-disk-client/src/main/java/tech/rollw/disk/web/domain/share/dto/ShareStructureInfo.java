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
import tech.rollw.disk.web.domain.userstorage.AttributedStorage;
import tech.rollw.disk.web.domain.userstorage.dto.FolderInfo;

import java.util.List;

/**
 * @author RollW
 */
public record ShareStructureInfo(
        long id,
        long creatorId,
        String shareCode,
        String password,
        boolean isPublic,
        long expireTime,
        long createTime,
        List<FolderInfo> parents,
        FolderInfo current,
        List<? extends AttributedStorage> storages
) {

    public boolean isExpired(long time) {
        if (expireTime <= 0) {
            return false;
        }
        return time > expireTime;
    }

    public static ShareStructureInfo of(UserShare userShare,
                                        List<FolderInfo> parents,
                                        FolderInfo current,
                                        List<? extends AttributedStorage> storages) {
        return new ShareStructureInfo(
                userShare.getId(),
                userShare.getUserId(),
                userShare.getShareId(),
                userShare.getPassword(),
                userShare.isPublic(),
                userShare.getExpireTime(),
                userShare.getCreateTime(),
                parents,
                current,
                storages
        );
    }
}
