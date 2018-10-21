/**
 * Copyright 2018 Iconic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.iconic.project;

import java.util.Optional;

/**
 * An interface for non-rendered objects that may be displayed through a user interface.
 */
public interface Displayable {
    /**
     * <p>Returns the label for this object</p>
     *
     * @return The label of the object
     */
    String getLabel();

    /**
     * <p>Returns the icon for for this object if present</p>
     *
     * @return The icon of the object if present
     */
    Optional<Enum<?>> getIcon();
}
