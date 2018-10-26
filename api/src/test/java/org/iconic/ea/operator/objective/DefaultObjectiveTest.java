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
package org.iconic.ea.operator.objective;

import org.iconic.ea.chromosome.Chromosome;
import org.iconic.ea.data.DataManager;
import org.iconic.ea.data.FeatureClass;
import org.iconic.ea.data.NumericFeatureClass;
import org.iconic.ea.operator.objective.error.ErrorFunction;
import org.iconic.ea.operator.objective.error.MeanSquaredError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
/**
 * <p>Test class for {@link org.iconic.ea.operator.objective.DefaultObjective}
 * <p><b>Mockito</b> is used, and shows how one should use mocks in their own tests.
 * @author Scott Walker
 */
@Disabled
@ExtendWith(MockitoExtension.class) // Use @ExtendWith to ensure each @Mock is instantiated.
class DefaultObjectiveTest {

    /** The instance we will use for every test (It is created during {@link #setUp()}) */
    private DefaultObjective defaultObjective;

    /** Mocked Chromosome */
    @Mock // Be sure to use a Mockito.when() for every call it will receive
    private Chromosome<Double> chromosome;

    /**
     * <p>The setup method to run before EVERY test is started. (Not once overall)
     */
    @BeforeEach
    void setUp() {
        // We can also mock objects in-line.
        @SuppressWarnings("unchecked")
        DataManager<Double> dataManager = mock(DataManager.class);

        // We have to define our lists before using them in a thenReturn() statement.
        List<Number> results = new LinkedList<>();
        results.addAll(Arrays.asList(1.0, 2.0, 3.0, 4.0));


        // Similarly, set all the data we need for it prior to setting it.
        NumericFeatureClass features = new NumericFeatureClass(true);
        features.addSampleValue(2.0);
        features.addSampleValue(4.0);
        features.addSampleValue(6.0);
        features.addSampleValue(8.0);
        HashMap<String, FeatureClass<Number>> dataset = new HashMap<>();
        dataset.put("A", features);

        // Set the mocks we require.
        // NOTE: Explicitly define anything you want to use in thenReturn() as its OWN variable. Mockito is strict.
        //when(chromosome.evaluate(dataManager)).thenReturn(results); // TODO uncomment
        when(dataManager.getDataset()).thenReturn(dataset); // When we call dataManager.getDataset(), return the one we defined.

        // Create the defaultObjective we will use for each test.
        // We would expect to ALSO mock out lambda, but for the sake of this I am showing not everything has to be mocked.
        ErrorFunction lambda = new MeanSquaredError();
        defaultObjective = new DefaultObjective(lambda, dataManager);
    }

    /**
     * <p>Test we get the expected results from apply()
     * <p>Because @BeforeEach was used, the test itself is quite small, yay!
     */
    @Test
    void testApply() {
        final double expected = 7.5;
        final double fitness = defaultObjective.apply(chromosome);
        assertEquals(expected, fitness);
    }
}
