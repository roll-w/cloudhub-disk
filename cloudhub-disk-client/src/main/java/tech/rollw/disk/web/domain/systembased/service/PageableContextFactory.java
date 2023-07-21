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

package tech.rollw.disk.web.domain.systembased.service;

import tech.rollw.disk.web.domain.systembased.ContextThread;
import tech.rollw.disk.web.domain.systembased.ContextThreadAware;
import tech.rollw.disk.web.domain.systembased.DefaultContextThread;
import tech.rollw.disk.web.domain.systembased.paged.PageableContext;
import org.springframework.stereotype.Service;

/**
 * @author RollW
 */
@Service
public class PageableContextFactory implements ContextThreadAware<PageableContext> {
    private final ThreadLocal<ContextThread<PageableContext>> pageableContextThreadLocal = new ThreadLocal<>();

    @Override
    public ContextThread<PageableContext> assambleContextThread(PageableContext context) {
        ContextThread<PageableContext> contextThread = pageableContextThreadLocal.get();
        if (contextThread == null) {
            contextThread = new DefaultContextThread<>(context);
            pageableContextThreadLocal.set(contextThread);
        }
        return contextThread;
    }

    @Override
    public ContextThread<PageableContext> getContextThread() {
        ContextThread<PageableContext> thread =
                pageableContextThreadLocal.get();
        if (thread != null) {
            return thread;
        }
        return assambleContextThread(null);
    }

    @Override
    public void clearContextThread() {
        pageableContextThreadLocal.remove();
    }

}
