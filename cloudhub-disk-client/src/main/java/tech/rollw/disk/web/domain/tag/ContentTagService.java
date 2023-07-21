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

package tech.rollw.disk.web.domain.tag;

import tech.rollw.disk.web.domain.tag.dto.ContentTagInfo;
import tech.rollw.disk.web.domain.tag.dto.TagGroupDto;
import tech.rollw.disk.common.data.page.Pageable;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author RollW
 */
public interface ContentTagService {
    // TODO: refactor this interface

    // TODO: not use Pageable parameter in this method
    List<ContentTagInfo> getTags(Pageable pageable);

    List<TagGroupDto> getTagGroups(Pageable pageable);

    void createContentTagGroup(String name,
                               String description,
                               KeywordSearchScope searchScope);

    void createContentTag(String name,
                          String description,
                          List<TagKeyword> keywords);

    void importFromKeywordsFile(InputStream stream, long tagGroupId);

    void exportToKeywordsFile(OutputStream stream, long tagGroupId);
}
