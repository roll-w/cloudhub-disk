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

package tech.rollw.disk.web.domain.operatelog.context;

/**
 * @author RollW
 */
public class OperationContextHolder {
    private static final ThreadLocal<OperationContext> THREAD_LOCAL = new ThreadLocal<>();

    public static void setContext(OperationContext operationContext) {
        THREAD_LOCAL.set(operationContext);
    }

    public static OperationContext getContext() {
        OperationContext context = THREAD_LOCAL.get();
        if (context == null) {
            THREAD_LOCAL.set(new OperationContext());
        }
        return THREAD_LOCAL.get();
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }

    public static boolean isPresent() {
        return THREAD_LOCAL.get() != null;
    }

    public static void clear() {
        THREAD_LOCAL.remove();
    }


    private OperationContextHolder() {
    }
}
