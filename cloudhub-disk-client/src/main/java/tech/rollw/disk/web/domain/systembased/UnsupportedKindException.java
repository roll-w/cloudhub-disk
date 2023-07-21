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

/**
 * Unsupported kind exception.
 *
 * @author RollW
 */
public class UnsupportedKindException extends IllegalArgumentException {
    public UnsupportedKindException() {
        super();
    }

    public UnsupportedKindException(String s) {
        super(s);
    }

    public UnsupportedKindException(SystemResourceKind systemResourceKind) {
        super("Unsupported system resource kind: " + systemResourceKind);
    }

    public UnsupportedKindException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedKindException(Throwable cause) {
        super(cause);
    }
}
