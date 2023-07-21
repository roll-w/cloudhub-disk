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
public interface ContextThread<C extends SystemContext> {
    C getContext();

    void setContext(C systemThreadContext);

    boolean hasContext();

    void clearContext();

    default void callWithContext(C systemThreadContext, Runnable runnable) {
        C old = getContext();
        setContext(systemThreadContext);
        try {
            call(runnable);
        } finally {
            setContext(old);
        }
    }

    default <T> T callWithContext(C systemThreadContext,
                                  Callable<T> callable) throws Exception {
        C old = getContext();
        setContext(systemThreadContext);
        try {
            return call(callable);
        } finally {
            setContext(old);
        }
    }

    <T> T call(Callable<T> callable) throws Exception;

    void call(Runnable callable);
}
