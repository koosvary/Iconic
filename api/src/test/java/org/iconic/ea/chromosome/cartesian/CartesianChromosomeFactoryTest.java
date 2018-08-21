package org.iconic.ea.chromosome.cartesian;

import org.iconic.ea.operator.primitive.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;


class CartesianChromosomeFactoryTest{

	@ParameterizedTest
	@MethodSource("initialisationTestProvider")
	@DisplayName("Test that a cartesian factory can be initialised with valid parameters")
	void initialisationTest(int numOutputs, int numInputs, int columns, int rows, int levelsBack){
		try{
			new CartesianChromosomeFactory<Double>(numOutputs, numInputs, columns, rows, levelsBack);
		}
		catch(Exception e){
			fail(e.getMessage());
		}
	}

	@ParameterizedTest
	@MethodSource("badInitialisationTestProvider")
	@DisplayName("Test that a cartesian factory can't be initialised with invalid parameters")
	void badInitialisationTest(int numOutputs, int numInputs, int columns, int rows, int levelsBack){
		assertThrows(
				AssertionError.class,
				() -> new CartesianChromosomeFactory<>(numOutputs, numInputs, columns, rows, levelsBack)
		);
	}

	@ParameterizedTest
	@MethodSource("encodeTailTestProvider")
	@DisplayName("Test that the encode tail method is working for a variety of valid inputs")
	void encodeTailTest(int numOutputs, int numInputs, int numColumns, int numRows){
		//dummy factory used to access the encode tail method
		CartesianChromosomeFactory<Double> factory = new CartesianChromosomeFactory<>(1,1,1,1,1);
		List<Integer> outputs = factory.encodeTail(numOutputs,numInputs,numColumns,numRows);
		for(Integer i : outputs){
			assertTrue(i <= numInputs + (numColumns * numRows) - 1 && i >= 0);
		}
	}

	@Test
	@DisplayName("Test that the add function method adds functions and correctly recalculates max arity")
	void addFunctionTest(){
		CartesianChromosomeFactory<Double> factory = new CartesianChromosomeFactory<>(1,1,1,1,1);

		assertEquals(factory.getMaxArity(), 0);

		factory.addFunction(Arrays.asList(
				new Sin()
		));

		assertEquals(factory.getFunctionalPrimitives().size(),1);
		assertEquals(factory.getFunctionalPrimitives().get(0).toString(),"SIN");
		assertEquals(factory.getMaxArity(), 1);

		factory.addFunction(Arrays.asList(
				new Sin()
		));
		
		assertEquals(factory.getFunctionalPrimitives().size(),1);

		factory.addFunction(Arrays.asList(
				new Addition(), new Subtraction(), new Multiplication(), new Division(),
				new Power(), new Root(), new Cos(), new Tan()
		));
		assertEquals(factory.getFunctionalPrimitives().size(),9);
		assertEquals(factory.getMaxArity(), 2);
	}

	@ParameterizedTest
	@MethodSource("encodeBodyTestProvider")
	@DisplayName("Test that the encode body method is working for a variety of valid inputs")
	void encodeBodyTest(){
		//dummy factory used to access the encode body method
		CartesianChromosomeFactory<Double> factory = new CartesianChromosomeFactory<>(1,1,1,1,1);

	}

	@Test
	void getChromosomeTest(){

	}

	private static Stream<Arguments> encodeTailTestProvider(){
		return Stream.of(
				Arguments.of(1,1,4,4),
				Arguments.of(5,5,10,10),
				Arguments.of(50,50,1000,1000),
				//there are only two options for outputs to connect to
				//this is the most likely test to reveal a problem
				//so I made heaps of outputs to be sure
				Arguments.of(1,500,1,1)
		);
	}

	private static Stream<Arguments> encodeBodyTestProvider(){
		return Stream.of(
				Arguments.of(1,1,4,4)
		);
	}

	private static Stream<Arguments> initialisationTestProvider(){
		return Stream.of(
				Arguments.of(1,1,4,4,2)
		);
	}

	private static Stream<Arguments> badInitialisationTestProvider(){
		return Stream.of(
				Arguments.of(0,1,1,1,1),
				Arguments.of(1,0,1,1,1),
				Arguments.of(1,1,0,1,1),
				Arguments.of(1,1,1,0,1),
				Arguments.of(1,1,1,1,0)
		);
	}



	@Test
	void getAddressUpperBoundTest(){
	}

	@Test
	void getRandomPrimitiveTest(){
	}

	@Test
	void getRandomConnectionTest(){
	}
}