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

package tech.rollw.disk.web.controller.share;

import com.google.common.base.Strings;
import org.apache.commons.lang3.RandomStringUtils;
import tech.rollw.disk.web.common.ApiContextHolder;
import tech.rollw.disk.web.common.ParamValidate;
import tech.rollw.disk.web.controller.Api;
import tech.rollw.disk.web.controller.ParameterHelper;
import tech.rollw.disk.web.controller.storage.DownloadHelper;
import tech.rollw.disk.web.domain.operatelog.BuiltinOperationType;
import tech.rollw.disk.web.domain.operatelog.context.BuiltinOperate;
import tech.rollw.disk.web.domain.share.ShareSearchService;
import tech.rollw.disk.web.domain.share.ShareService;
import tech.rollw.disk.web.domain.share.common.UserShareErrorCode;
import tech.rollw.disk.web.domain.share.common.UserShareException;
import tech.rollw.disk.web.domain.share.dto.SharePasswordInfo;
import tech.rollw.disk.web.domain.share.dto.ShareStructureInfo;
import tech.rollw.disk.web.controller.share.vo.ShareInfoVo;
import tech.rollw.disk.web.controller.share.vo.ShareStorageVo;
import tech.rollw.disk.web.controller.share.vo.ShareStructureVo;
import tech.rollw.disk.web.domain.storage.StorageService;
import tech.rollw.disk.web.domain.user.AttributedUser;
import tech.rollw.disk.web.domain.user.LegalUserType;
import tech.rollw.disk.web.domain.user.UserIdentity;
import tech.rollw.disk.web.domain.user.service.UserSearchService;
import tech.rollw.disk.web.domain.userstorage.*;
import tech.rollw.disk.web.domain.userstorage.dto.FileInfo;
import tech.rollw.disk.web.domain.userstorage.dto.SimpleStorageIdentity;
import tech.rollw.disk.web.domain.userstorage.dto.SimpleStorageOwner;
import tech.rollw.disk.common.HttpResponseEntity;
import tech.rollw.disk.common.UserErrorCode;
import tech.rollw.disk.common.data.page.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author RollW
 */
@Api
public class ShareController {
    private final ShareService shareService;
    private final ShareSearchService shareSearchService;
    private final UserStorageSearchService userStorageSearchService;
    private final UserSearchService userSearchService;
    private final StorageService storageService;

    public ShareController(ShareService shareService,
                           ShareSearchService shareSearchService,
                           UserStorageSearchService userStorageSearchService,
                           UserSearchService userSearchService,
                           StorageService storageService) {
        this.shareService = shareService;
        this.shareSearchService = shareSearchService;
        this.userStorageSearchService = userStorageSearchService;
        this.userSearchService = userSearchService;
        this.storageService = storageService;
    }

    @BuiltinOperate(BuiltinOperationType.CREATE_STORAGE_SHARE)
    @PostMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/shares")
    public HttpResponseEntity<SharePasswordInfo> createShare(
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String ownerTypeParam,
            @PathVariable("storageType") String typeParam,
            @PathVariable("storageId") Long storageId,
            @RequestBody ShareCreateRequest createRequest) {
        UserIdentity userIdentity = ApiContextHolder.getContext().userInfo();
        LegalUserType ownerType = LegalUserType.from(ownerTypeParam);
        StorageOwner storageOwner =
                new SimpleStorageOwner(ownerId, ownerType);
        StorageType storageType = StorageType.from(typeParam);
        StorageIdentity storageIdentity =
                new SimpleStorageIdentity(storageId, storageType);
        String password = getPassword(createRequest);
        SharePasswordInfo sharePasswordInfo = shareService.share(
                storageIdentity, storageOwner,
                createRequest.toDuration(),
                userIdentity,
                password
        );
        return HttpResponseEntity.success(sharePasswordInfo);
    }

    private String getPassword(ShareCreateRequest createRequest) {
        if (createRequest.type() == ShareCreateRequest.PUBLIC) {
            return null;
        }
        if (Strings.isNullOrEmpty(createRequest.password())) {
            return RandomStringUtils.randomAlphanumeric(ShareService.DEFAULT_PASSWORD_LENGTH);
        }
        return createRequest.password();
    }

    @GetMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/shares")
    public HttpResponseEntity<List<SharePasswordInfo>> getStorageShares(
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String ownerTypeParam,
            @PathVariable("storageType") String typeParam,
            @PathVariable("storageId") Long storageId) {
        return HttpResponseEntity.success();
    }

    @GetMapping("/users/{userId}/shares")
    public HttpResponseEntity<List<ShareInfoVo>> getOwnerShares(
            @PathVariable("userId") Long ownerId) {
        UserIdentity userIdentity = ApiContextHolder.getContext().userInfo();
        List<SharePasswordInfo> sharePasswordInfos =
                shareSearchService.findByUserId(ownerId);
        boolean onlyPublic = isOnlyPublic(userIdentity, ownerId);

        return HttpResponseEntity.success(
                sharePasswordInfos.stream()
                        .filter(sharePasswordInfo ->
                                !sharePasswordInfo.isExpired(System.currentTimeMillis()))
                        .filter(sharePasswordInfo -> {
                            if (onlyPublic) {
                                return sharePasswordInfo.isPublic();
                            }
                            return true;
                        })
                        .map(ShareInfoVo::from).toList()
        );
    }

    private boolean isOnlyPublic(UserIdentity userIdentity, Long id) {
        if (userIdentity == null) {
            return true;
        }
        return userIdentity.getUserId() == id;
    }

    @GetMapping("/user/shares")
    public HttpResponseEntity<List<ShareStorageVo>> getUserShares(
            Pageable pageable) {
        UserIdentity userIdentity = ApiContextHolder.getContext().userInfo();

        List<SharePasswordInfo> sharePasswordInfos =
                shareSearchService.findByUserId(userIdentity.getUserId(), pageable);
        // TODO: paging result
        List<? extends StorageIdentity> storageIdentities = sharePasswordInfos.stream()
                .map(sharePasswordInfo -> new SimpleStorageIdentity(
                        sharePasswordInfo.storageId(),
                        sharePasswordInfo.storageType()))
                .toList();
        List<? extends AttributedStorage> attributedStorages =
                userStorageSearchService.findStorages(storageIdentities);

        List<ShareStorageVo> shareStorageVos = pairWith(
                sharePasswordInfos,
                attributedStorages
        );
        return HttpResponseEntity.success(shareStorageVos);
    }

    private List<ShareStorageVo> pairWith(List<SharePasswordInfo> sharePasswordInfos,
                                          List<? extends AttributedStorage> attributedStorages) {
        return sharePasswordInfos.stream()
                .map(sharePasswordInfo -> {
                    AttributedStorage attributedStorage = attributedStorages.stream()
                            .filter(storage -> storage.getStorageId() == sharePasswordInfo.storageId())
                            .findFirst()
                            .orElseThrow(() -> new UserShareException(UserShareErrorCode.ERROR_STORAGE_NOT_FOUND));
                    return ShareStorageVo.from(
                            sharePasswordInfo,
                            attributedStorage
                    );
                })
                .toList();
    }

    @GetMapping("/{ownerType}/{ownerId}/disk/{storageType}/{storageId}/shares/{shareId}")
    public HttpResponseEntity<SharePasswordInfo> getShare(
            @PathVariable("ownerId") Long ownerId,
            @PathVariable("ownerType") String ownerTypeParam,
            @PathVariable("storageType") String typeParam,
            @PathVariable("storageId") Long storageId,
            @PathVariable("shareId") Long shareId) {

        return HttpResponseEntity.success();
    }

    @DeleteMapping("/user/shares/{shareId}")
    public HttpResponseEntity<Void> cancelShare(
            @PathVariable("shareId") Long shareId) {
        shareService.cancelShare(shareId);
        return HttpResponseEntity.success();
    }

    @GetMapping("/shares/{shareToken}/metadata")
    public HttpResponseEntity<ShareInfoVo> getShareMetadataByLink(
            @PathVariable("shareToken") String shareToken) {
        ParamValidate.notEmpty(shareToken, "shareToken");

        SharePasswordInfo sharePasswordInfo =
                shareSearchService.search(shareToken);
        if (sharePasswordInfo.expireTime() == 1) {
            throw new UserShareException(UserShareErrorCode.ERROR_SHARE_CANCEL);
        }
        if (sharePasswordInfo.isExpired(System.currentTimeMillis())) {
            throw new UserShareException(UserShareErrorCode.ERROR_SHARE_EXPIRED);
        }
        AttributedUser attributedUser =
                userSearchService.findUser(sharePasswordInfo.creatorId());
        return HttpResponseEntity.success(
                ShareInfoVo.from(sharePasswordInfo, attributedUser)
        );
    }

    // with password
    @GetMapping("/shares/{shareToken}")
    public HttpResponseEntity<ShareStructureVo> getShareByLink(
            @PathVariable("shareToken") String shareToken,
            @RequestParam(value = "password", defaultValue = "") String password,
            @RequestParam(value = "parent", defaultValue = "0") Long parent) {
        ShareStructureInfo shareStructureInfo =
                shareSearchService.findStructureByShareCode(shareToken, parent);
        if (shareStructureInfo.expireTime() == 1) {
            throw new UserShareException(UserShareErrorCode.ERROR_SHARE_CANCEL);
        }

        if (shareStructureInfo.isExpired(System.currentTimeMillis())) {
            throw new UserShareException(UserShareErrorCode.ERROR_SHARE_EXPIRED);
        }
        if (!shareStructureInfo.isPublic() &&
                !password.equals(shareStructureInfo.password())) {
            throw new UserShareException(UserShareErrorCode.ERROR_PASSWORD);
        }
        AttributedUser attributedUser =
                userSearchService.findUser(shareStructureInfo.creatorId());
        return HttpResponseEntity.success(
                ShareStructureVo.from(shareStructureInfo, attributedUser)
        );
    }

    @PostMapping("/shares/{shareToken}/save/{storageType}/{storageId}")
    public HttpResponseEntity<Void> saveShare(
            @PathVariable("shareToken") String shareToken,
            @RequestParam(value = "password", defaultValue = "") String password,
            @PathVariable("storageId") Long storageId,
            @PathVariable("storageType") String storageType) {
        UserIdentity userIdentity = ApiContextHolder.getContext().userInfo();
        if (userIdentity == null) {
            throw new UserShareException(UserErrorCode.ERROR_USER_NOT_LOGIN);
        }

        return HttpResponseEntity.success();
    }


    @GetMapping("/shares/{shareToken}/save/{storageType}/{storageId}")
    public void getShareStorage(
            @PathVariable("shareToken") String shareToken,
            @RequestParam(value = "password", defaultValue = "") String password,
            @PathVariable("storageId") Long storageId,
            @PathVariable("storageType") String storageType,
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) throws IOException {
        StorageIdentity storageIdentity =
                ParameterHelper.buildStorageIdentity(storageId, storageType);
        if (!storageIdentity.isFile()) {
            throw new UserShareException(UserShareErrorCode.ERROR_STORAGE_NOT_FOUND);
        }
        SharePasswordInfo sharePasswordInfo =
                shareSearchService.search(shareToken);
        if (sharePasswordInfo.expireTime() == 1) {
            throw new UserShareException(UserShareErrorCode.ERROR_SHARE_CANCEL);
        }
        if (sharePasswordInfo.isExpired(System.currentTimeMillis())) {
            throw new UserShareException(UserShareErrorCode.ERROR_SHARE_EXPIRED);
        }
        if (!sharePasswordInfo.isPublic() &&
                !password.equals(sharePasswordInfo.password())) {
            throw new UserShareException(UserShareErrorCode.ERROR_PASSWORD);
        }
        if (!shareService.hasStorage(sharePasswordInfo.id(), storageIdentity)) {
            throw new UserShareException(UserShareErrorCode.ERROR_STORAGE_NOT_FOUND);
        }
        FileInfo fileInfo = userStorageSearchService
                .findFile(storageIdentity.getStorageId());
        DownloadHelper.downloadFile(fileInfo, httpServletRequest,
                httpServletResponse, storageService);
    }

}
