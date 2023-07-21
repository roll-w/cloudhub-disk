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

package tech.rollw.disk.web.controller.share.vo;

import tech.rollw.disk.web.domain.share.dto.SharePasswordInfo;
import tech.rollw.disk.web.domain.user.AttributedUser;
import tech.rollw.disk.web.domain.userstorage.StorageType;

import java.util.Objects;

/**
 * @author RollW
 */
public record ShareInfoVo(
        long id,
        long storageId,
        StorageType storageType,
        long creatorId,
        String username,
        String nickname,
        String shareCode,
        boolean isPublic,
        long expireTime,
        long createTime
) {
    public static ShareInfoVo from(SharePasswordInfo sharePasswordInfo) {
        return new ShareInfoVo(
                sharePasswordInfo.id(),
                sharePasswordInfo.storageId(),
                sharePasswordInfo.storageType(),
                sharePasswordInfo.creatorId(),
                null,
                null,
                sharePasswordInfo.shareCode(),
                sharePasswordInfo.isPublic(),
                sharePasswordInfo.expireTime(),
                sharePasswordInfo.createTime()
        );
    }

    public static ShareInfoVo from(SharePasswordInfo sharePasswordInfo,
                                   AttributedUser attributedUser) {
        if (sharePasswordInfo == null) {
            return null;
        }

        return new ShareInfoVo(
                sharePasswordInfo.id(),
                sharePasswordInfo.storageId(),
                sharePasswordInfo.storageType(),
                sharePasswordInfo.creatorId(),
                attributedUser.getUsername(),
                Objects.requireNonNullElse(
                        attributedUser.getNickname(),
                        attributedUser.getUsername()
                ),
                sharePasswordInfo.shareCode(),
                sharePasswordInfo.isPublic(),
                sharePasswordInfo.expireTime(),
                sharePasswordInfo.createTime()
        );
    }

}
