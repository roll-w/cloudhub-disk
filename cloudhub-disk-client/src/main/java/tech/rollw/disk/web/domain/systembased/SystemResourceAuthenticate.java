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

import tech.rollw.disk.web.domain.operatelog.Action;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;

/**
 * Mark the system resource access as requiring authentication.
 *
 * @author RollW
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({METHOD})
public @interface SystemResourceAuthenticate {
    /**
     * System resource kind. The default value is the first
     * element of {@link SystemResourceKind}.
     */
    SystemResourceKind kind() default SystemResourceKind.FILE;

    String kindParam() default "";

    boolean inferredKind() default true;

    long id() default INVALID_ID;

    String idParam() default "";

    /**
     * The action of the system resource. The default value
     * is the first element of {@link Action}.
     */
    Action action() default Action.CREATE;

    boolean inferredAction() default true;

    long INVALID_ID = -1000L;
}
