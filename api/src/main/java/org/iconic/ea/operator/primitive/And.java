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
package org.iconic.ea.operator.primitive;

public class And extends ArithmeticPrimitive<Number> {
    public And() {
        super(
                args -> (args.get(0) > 0 && args.get(1) > 0) ? 1.d : 0.d,
                2, "AND", "Returns 1 if both a and b are greater than 0, 0 otherwise."
        );
    }
}
