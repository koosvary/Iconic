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

public class Division extends ArithmeticPrimitive<Number> {
    public Division() {
        super(
                    args -> {
                        final double delta = 0.001;
                        double identity = args.get(0);

                        for (int i = 1; i < args.size(); ++i) {
                            if (args.get(i) < delta + 0.d && args.get(i) > 0.d - delta) {
                                return 1.d;
                            }

                            identity /= args.get(i);
                        }

                        return identity;
                    },
                2, "DIV", "Returns the division of a / b."
        );
    }
}
