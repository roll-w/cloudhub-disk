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

package tech.rollw.disk.web.domain.systembased;

import java.util.concurrent.Callable;

/**
 * @author RollW
 */
public class DefaultContextThread<C extends SystemContext> implements ContextThread<C> {
    private C systemThreadContext;

    public DefaultContextThread(C systemThreadContext) {
        this.systemThreadContext = systemThreadContext;
    }

    @Override
    public C getContext() {
        return systemThreadContext;
    }

    @Override
    public void setContext(C systemThreadContext) {
        this.systemThreadContext = systemThreadContext;
    }

    @Override
    public boolean hasContext() {
        return systemThreadContext != null;
    }

    @Override
    public void clearContext() {
        this.systemThreadContext = null;
    }

    @Override
    public <T> T call(Callable<T> callable) throws Exception {
        return callable.call();
    }

    @Override
    public void call(Runnable callable) {
        callable.run();
    }
}
