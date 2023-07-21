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

package tech.rollw.disk.web.controller.tag;

import tech.rollw.disk.web.controller.AdminApi;
import tech.rollw.disk.web.controller.OneParameterRequest;
import tech.rollw.disk.web.controller.tag.vo.*;
import tech.rollw.disk.web.domain.operatelog.BuiltinOperationType;
import tech.rollw.disk.web.domain.operatelog.context.BuiltinOperate;
import tech.rollw.disk.web.domain.systembased.SimpleSystemResource;
import tech.rollw.disk.web.domain.systembased.SystemResourceKind;
import tech.rollw.disk.web.domain.systembased.SystemResourceOperatorProvider;
import tech.rollw.disk.web.domain.tag.*;
import tech.rollw.disk.web.domain.tag.dto.ContentTagInfo;
import tech.rollw.disk.web.domain.tag.dto.TagGroupDto;
import tech.rollw.disk.web.system.pages.PageableInterceptor;
import tech.rollw.disk.common.HttpResponseEntity;
import tech.rollw.disk.common.data.page.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author RollW
 */
@AdminApi
public class ContentTagController {
    private final ContentTagService contentTagService;
    private final ContentTagProvider contentTagProvider;
    private final PageableInterceptor pageableInterceptor;
    private final SystemResourceOperatorProvider systemResourceOperatorProvider;

    public ContentTagController(ContentTagService contentTagService,
                                ContentTagProvider contentTagProvider,
                                PageableInterceptor pageableInterceptor, SystemResourceOperatorProvider systemResourceOperatorProvider) {
        this.contentTagService = contentTagService;
        this.contentTagProvider = contentTagProvider;
        this.pageableInterceptor = pageableInterceptor;
        this.systemResourceOperatorProvider = systemResourceOperatorProvider;
    }

    @GetMapping("/tags")
    public HttpResponseEntity<List<ContentTagInfo>> getTags(
            Pageable pageable) {
        List<ContentTagInfo> contentTagInfos =
                contentTagService.getTags(pageable);
        return HttpResponseEntity.success(
                pageableInterceptor.interceptPageable(
                        contentTagInfos,
                        pageable,
                        ContentTag.class
                )
        );
    }

    @GetMapping("/tags/{tagId}")
    public HttpResponseEntity<ContentTagInfo> getTag(
            @PathVariable("tagId") Long id) {
        return HttpResponseEntity.success(
                contentTagProvider.getTagById(id)
        );
    }

    @GetMapping("/tags/groups")
    public HttpResponseEntity<List<TagGroupVo>> getTagGroups(
            Pageable pageable) {
        List<TagGroupDto> tagGroupDtos =
                contentTagService.getTagGroups(pageable);

        return HttpResponseEntity.success(
                pageableInterceptor.interceptPageable(
                        tagGroupDtos.stream()
                                .map(TagGroupVo::from)
                                .toList(),
                        pageable,
                        ContentTag.class
                )
        );
    }

    @GetMapping("/tags/groups/{groupId}")
    public HttpResponseEntity<TagGroupDto> getTagGroup(
            @PathVariable("groupId") Long groupId) {

        return HttpResponseEntity.success(
                contentTagProvider.getTagGroupById(groupId)
        );
    }

    @PostMapping("/tags/groups")
    @BuiltinOperate(BuiltinOperationType.CREATE_TAG_GROUP)
    public HttpResponseEntity<Void> createTagGroup(
            @RequestBody TagGroupCreateRequest request) {
        contentTagService.createContentTagGroup(
                request.name(),
                request.description(),
                request.keywordSearchScope()
        );
        return HttpResponseEntity.success();
    }

    @PutMapping("/tags/groups/{groupId}")
    @BuiltinOperate(BuiltinOperationType.UPDATE_TAG_GROUP)
    public HttpResponseEntity<Void> updateTagGroup(
            @PathVariable("groupId") Long groupId,
            @RequestBody TagGroupUpdateRequest request) {
        TagGroupOperator tagGroupOperator = systemResourceOperatorProvider.getSystemResourceOperator(
                new SimpleSystemResource(
                        groupId,
                        SystemResourceKind.TAG_GROUP
                ),
                false
        );
        tagGroupOperator.disableAutoUpdate()
                .rename(request.name())
                .setDescription(request.description())
                .update();
        return HttpResponseEntity.success();
    }

    @PutMapping("/tags/groups/{groupId}/tags/{tagId}")
    public HttpResponseEntity<Void> addTagToGroup(
            @PathVariable("groupId") Long groupId,
            @PathVariable("tagId") Long tagId) {
        TagGroupOperator tagGroupOperator = systemResourceOperatorProvider.getSystemResourceOperator(
                new SimpleSystemResource(
                        groupId,
                        SystemResourceKind.TAG_GROUP
                ),
                false
        );
        tagGroupOperator.disableAutoUpdate()
                .addTag(tagId)
                .update();
        return HttpResponseEntity.success();
    }

    @PostMapping("/tags/groups/{groupId}/tags")
    public HttpResponseEntity<Void> addTagToGroup(
            @PathVariable("groupId") Long groupId,
            @RequestBody OneParameterRequest<String> request) {
        TagGroupOperator tagGroupOperator = systemResourceOperatorProvider.getSystemResourceOperator(
                new SimpleSystemResource(
                        groupId,
                        SystemResourceKind.TAG_GROUP
                ),
                false
        );
        ContentTagInfo contentTagInfo =
                contentTagProvider.getByName(request.value());
        tagGroupOperator.disableAutoUpdate()
                .addTag(contentTagInfo.id())
                .update();
        return HttpResponseEntity.success();
    }

    @DeleteMapping("/tags/groups/{groupId}/tags/{tagId}")
    public HttpResponseEntity<Void> removeTagFromGroup(
            @PathVariable("groupId") Long groupId,
            @PathVariable("tagId") Long tagId) {
        TagGroupOperator tagGroupOperator = systemResourceOperatorProvider.getSystemResourceOperator(
                new SimpleSystemResource(
                        groupId,
                        SystemResourceKind.TAG_GROUP
                ),
                false
        );
        tagGroupOperator.disableAutoUpdate()
                .removeTag(tagId)
                .update();
        return HttpResponseEntity.success();
    }


    @PostMapping("/tags/groups/{groupId}/infile")
    @BuiltinOperate(BuiltinOperationType.CREATE_TAG)
    public HttpResponseEntity<Void> importTags(
            @PathVariable("groupId") Long groupId,
            @RequestPart(name = "file") MultipartFile file) throws IOException {
        contentTagService.importFromKeywordsFile(file.getInputStream(), groupId);

        return HttpResponseEntity.success();
    }

    @GetMapping("/tags/groups/{groupId}/infile")
    public void exportTags(HttpServletResponse servletResponse) {
        // exports
    }

    @PostMapping("/tags")
    @BuiltinOperate(BuiltinOperationType.CREATE_TAG)
    public HttpResponseEntity<Void> createTag(
            @RequestBody TagCreateRequest request) {
        contentTagService.createContentTag(
                request.name(),
                request.description(),
                request.keywords()
        );
        return HttpResponseEntity.success();
    }

    @PutMapping("/tags/{tagId}")
    @BuiltinOperate(BuiltinOperationType.UPDATE_TAG)
    public HttpResponseEntity<Void> updateTag(
            @PathVariable("tagId") Long tagId,
            @RequestBody TagUpdateRequest request) {
        TagOperator tagOperator = systemResourceOperatorProvider.getSystemResourceOperator(
                new SimpleSystemResource(tagId, SystemResourceKind.TAG),
                false
        );
        tagOperator.disableAutoUpdate()
                .rename(request.name())
                .setDescription(request.description())
                .update();
        return HttpResponseEntity.success();
    }

    @PutMapping("/tags/{tagId}/keywords")
    @BuiltinOperate(BuiltinOperationType.UPDATE_TAG)
    public HttpResponseEntity<Void> setKeyword(
            @PathVariable("tagId") Long tagId,
            @RequestBody TagKeyword tagKeyword) {
        TagOperator tagOperator = systemResourceOperatorProvider.getSystemResourceOperator(
                new SimpleSystemResource(tagId, SystemResourceKind.TAG),
                false
        );
        tagOperator.enableAutoUpdate()
                .addKeyword(tagKeyword);
        return HttpResponseEntity.success();
    }

    @DeleteMapping("/tags/{tagId}/keywords")
    @BuiltinOperate(BuiltinOperationType.UPDATE_TAG)
    public HttpResponseEntity<Void> deleteKeyword(
            @PathVariable("tagId") Long tagId,
            @RequestParam("name") String keywordName) {
        TagOperator tagOperator = systemResourceOperatorProvider.getSystemResourceOperator(
                new SimpleSystemResource(tagId, SystemResourceKind.TAG),
                false
        );
        tagOperator.enableAutoUpdate()
                .removeKeyword(TagKeyword.of(keywordName));
        return HttpResponseEntity.success();
    }

    @DeleteMapping("/tags/{tagId}")
    @BuiltinOperate(BuiltinOperationType.DELETE_TAG)
    public HttpResponseEntity<Void> deleteTag(
            @PathVariable("tagId") Long tagId) {
        TagOperator tagOperator = systemResourceOperatorProvider.getSystemResourceOperator(
                new SimpleSystemResource(tagId, SystemResourceKind.TAG),
                false
        );
        tagOperator.enableAutoUpdate()
                .delete();
        return HttpResponseEntity.success();
    }
}
