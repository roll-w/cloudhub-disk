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

import tech.rollw.disk.web.domain.share.UserShare;
import tech.rollw.disk.web.domain.share.dto.SharePasswordInfo;
import tech.rollw.disk.web.domain.userstorage.AttributedStorage;
import tech.rollw.disk.web.controller.storage.vo.StorageVo;

/**
 * @author RollW
 */
public record ShareStorageVo(
        long id,
        long creatorId,
        String shareCode,
        boolean isPublic,
        long expireTime,
        long createTime,
        String password,
        StorageVo storage
) {

    public static ShareStorageVo from(UserShare userShare,
                                      AttributedStorage storage) {
        return new ShareStorageVo(
                userShare.getId(),
                userShare.getStorageId(),
                userShare.getShareId(),
                userShare.isPublic(),
                userShare.getExpireTime(),
                userShare.getCreateTime(),
                userShare.getPassword(),
                StorageVo.from(storage)
        );
    }

    public static ShareStorageVo from(SharePasswordInfo sharePasswordInfo,
                                      AttributedStorage storage) {
        return new ShareStorageVo(
                sharePasswordInfo.id(),
                sharePasswordInfo.creatorId(),
                sharePasswordInfo.shareCode(),
                sharePasswordInfo.isPublic(),
                sharePasswordInfo.expireTime(),
                sharePasswordInfo.createTime(),
                sharePasswordInfo.password(),
                StorageVo.from(storage)
        );
    }
}
