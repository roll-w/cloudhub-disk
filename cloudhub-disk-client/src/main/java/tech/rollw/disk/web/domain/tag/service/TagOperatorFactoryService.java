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

package tech.rollw.disk.web.domain.tag.service;

import tech.rollw.disk.web.domain.systembased.SystemResource;
import tech.rollw.disk.web.domain.systembased.SystemResourceKind;
import tech.rollw.disk.web.domain.systembased.SystemResourceOperator;
import tech.rollw.disk.web.domain.systembased.SystemResourceOperatorFactory;
import tech.rollw.disk.web.domain.systembased.validate.Validator;
import tech.rollw.disk.web.domain.systembased.validate.ValidatorProvider;
import tech.rollw.disk.web.domain.tag.ContentTag;
import tech.rollw.disk.web.domain.tag.TagGroup;
import tech.rollw.disk.web.domain.tag.TagGroupOperator;
import tech.rollw.disk.web.domain.tag.TagOperator;
import tech.rollw.disk.web.domain.tag.common.ContentTagErrorCode;
import tech.rollw.disk.web.domain.tag.common.ContentTagException;
import tech.rollw.disk.web.domain.tag.repository.ContentTagRepository;
import tech.rollw.disk.web.domain.tag.repository.TagGroupRepository;
import org.springframework.stereotype.Service;

/**
 * @author RollW
 */
@Service
public class TagOperatorFactoryService implements SystemResourceOperatorFactory,
        TagGroupOperatorDelegate, TagOperatorDelegate {
    private final TagGroupRepository tagGroupRepository;
    private final ContentTagRepository contentTagRepository;
    private final Validator tagValidator;
    private final Validator tagGroupValidator;

    public TagOperatorFactoryService(TagGroupRepository tagGroupRepository,
                                     ContentTagRepository contentTagRepository,
                                     ValidatorProvider validatorProvider) {
        this.tagGroupRepository = tagGroupRepository;
        this.contentTagRepository = contentTagRepository;
        tagGroupValidator = validatorProvider.getValidator(SystemResourceKind.TAG_GROUP);
        tagValidator = validatorProvider.getValidator(SystemResourceKind.TAG);
    }

    @Override
    public boolean supports(SystemResourceKind systemResourceKind) {
        return systemResourceKind == SystemResourceKind.TAG ||
                systemResourceKind == SystemResourceKind.TAG_GROUP;
    }

    @Override
    public boolean isAssignableTo(Class<? extends SystemResourceOperator> clazz) {
        return false;
    }

    @Override
    public SystemResourceOperator createResourceOperator(
            SystemResource systemResource,
            boolean checkDelete) {
        return switch (systemResource.getSystemResourceKind()) {
            case TAG -> forTagOperator(systemResource, checkDelete);
            case TAG_GROUP -> forTagGroupOperator(systemResource, checkDelete);
            default -> throw new IllegalArgumentException("Unsupported system resource kind: " +
                    systemResource.getSystemResourceKind());
        };
    }

    private TagOperator forTagOperator(SystemResource systemResource,
                                       boolean check) {
        if (systemResource instanceof ContentTag contentTag) {
            return new TagOperatorImpl(contentTag, this, check);
        }
        ContentTag contentTag = contentTagRepository.getById(systemResource.getResourceId());
        if (contentTag == null) {
            throw new ContentTagException(ContentTagErrorCode.ERROR_TAG_NOT_EXIST);
        }
        return new TagOperatorImpl(contentTag, this, check);
    }

    private TagGroupOperator forTagGroupOperator(SystemResource systemResource,
                                                 boolean check) {
        if (systemResource instanceof TagGroup tagGroup) {
            return new TagGroupOperatorImpl(tagGroup, this, check);
        }
        TagGroup tagGroup = tagGroupRepository.getById(systemResource.getResourceId());
        if (tagGroup == null) {
            throw new ContentTagException(ContentTagErrorCode.ERROR_TAG_GROUP_NOT_EXIST);
        }
        return new TagGroupOperatorImpl(tagGroup, this, check);
    }

    @Override
    public void updateTagGroup(TagGroup tagGroup) {
        tagGroupRepository.update(tagGroup);
    }

    @Override
    public Validator getTagGroupValidator() {
        return tagGroupValidator;
    }

    @Override
    public void updateTag(ContentTag tag) {
        contentTagRepository.update(tag);
    }

    @Override
    public Validator getTagValidator() {
        return tagValidator;
    }
}
