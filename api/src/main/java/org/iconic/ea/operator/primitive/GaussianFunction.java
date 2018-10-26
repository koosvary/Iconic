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


//A sigmoid squashing function
public class GaussianFunction extends ArithmeticPrimitive<Number> {
    public GaussianFunction() {
        super(
                args -> Math.exp(-Math.pow(args.get(0),2)),
                1, "GAUSS", "Returns exp(-x^2), providing a normal distribution.", 4
        );
    }
}
