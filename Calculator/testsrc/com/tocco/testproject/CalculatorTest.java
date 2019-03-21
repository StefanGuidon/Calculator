package com.tocco.testproject;

import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Vector;

import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tocco.testproject.Calculator;

class CalculatorTest {
	
	public static Calculator calculator;
	
	private static PrintStream originalSystemOut = System.out;
	private static InputStream originalSystemIn = System.in;
    private static ByteArrayOutputStream testSystemOut;
	 
    @BeforeClass
    public static void beforeClass() {
    	calculator = new Calculator();
    }
    
    @BeforeEach
    void setTestSystemOut() {

    	/* set special System.out for testing */
        testSystemOut = new ByteArrayOutputStream();
        System.setOut(new PrintStream(testSystemOut));
    }

    @AfterEach
    void restoreOriginalSystemStreams() {
        System.setOut(originalSystemOut);
        System.setIn(originalSystemIn);
    }


	@Test
	void testMain() {
        
		String expression = "3.5 + (4 - 2) - 5 + 10 / 3 * 15";
        ByteArrayInputStream testIn = new ByteArrayInputStream(expression.getBytes());
        System.setIn(testIn);
		
        String[] args = null;
        Calculator.main(args);
        
        /* read last line of output stream */
        String output = testSystemOut.toString();
        output = output.substring(output.lastIndexOf("\n", output.length() - 2) + 1, output.lastIndexOf("\r\n"));
        
        assertTrue(output.equals("50.5"));
	}
	
	@Test
	void testParseArguments() throws NoSuchMethodException, IllegalAccessException,
								IllegalArgumentException, InvocationTargetException {
		
		Method method = Calculator.class.getDeclaredMethod("parseArguments", String.class, Vector.class);
		method.setAccessible(true);
		
		/* test wrong character in the argument */
		String expression1 = "9,8 * 2";
		Vector<String> postFixExpression1 = new Vector<String>();
		
		int output = (int) method.invoke(calculator, expression1, postFixExpression1);
		
		assertTrue(output == 1);
		
		/* test Shunting-yard algorithm */
		String expression2 = "3.5 + (4 - 2) - 5 + 10 / 3 * 15";
		Vector<String> postFixExpression2 = new Vector<String>();
		
		output = (int) method.invoke(calculator, expression2, postFixExpression2);
		
		assertTrue(output == 0);
		assertTrue(postFixExpression2.size() == 13);
		assertTrue(postFixExpression2.toString().equals("[3.5, 4, 2, -, +, 5, -, 10, 3, /, 15, *, +]"));
		
		String expression3 = "134.863 * 200 + ( 10-3.24) /4.367612 * 18.9";
		Vector<String> postFixExpression3 = new Vector<String>();
		
		output = (int) method.invoke(calculator, expression3, postFixExpression3);
		
		assertTrue(output == 0);
		assertTrue(postFixExpression3.size() == 11);
		assertTrue(postFixExpression3.toString().equals("[134.863, 200, *, 10, 3.24, -, 4.367612, /, 18.9, *, +]"));
	}
	
	@Test
	void testCalculat() throws NoSuchMethodException, IllegalAccessException,
						IllegalArgumentException, InvocationTargetException {
		
		Method method = Calculator.class.getDeclaredMethod("calculat", Vector.class);
		method.setAccessible(true);
		
		Vector<String> postFixExpression1 = new Vector<String>();
		postFixExpression1.add("3.5");
		postFixExpression1.add("4");
		postFixExpression1.add("2");
		postFixExpression1.add("-");
		postFixExpression1.add("+");
		postFixExpression1.add("5");
		postFixExpression1.add("-");
		postFixExpression1.add("10");
		postFixExpression1.add("3");
		postFixExpression1.add("/");
		postFixExpression1.add("15");
		postFixExpression1.add("*");
		postFixExpression1.add("+");
		
		int output = (int) method.invoke(calculator, postFixExpression1);
		
		assertTrue(output == 0);
		assertTrue(postFixExpression1.size() == 1);
		assertTrue(postFixExpression1.elementAt(0).equals("50.5"));
		
		Vector<String> postFixExpression2 = new Vector<String>();
		postFixExpression2.add("134.863");
		postFixExpression2.add("200");
		postFixExpression2.add("*");
		postFixExpression2.add("10");
		postFixExpression2.add("3.24");
		postFixExpression2.add("-");
		postFixExpression2.add("4.367612");
		postFixExpression2.add("/");
		postFixExpression2.add("18.9");
		postFixExpression2.add("*");
		postFixExpression2.add("+");

		output = (int) method.invoke(calculator, postFixExpression2);
		
		assertTrue(output == 0);
		assertTrue(postFixExpression2.size() == 1);
		assertTrue(postFixExpression2.elementAt(0).equals("27001.852598"));
	}
	
	@Test
	void testAdd() throws NoSuchMethodException, IllegalAccessException,
						IllegalArgumentException, InvocationTargetException {
		
		Method method = Calculator.class.getDeclaredMethod("add", BigDecimal.class, BigDecimal.class);
		method.setAccessible(true);
		
		BigDecimal operand1 = new BigDecimal("1");
		BigDecimal operand2 = new BigDecimal("2");
		
		BigDecimal result = (BigDecimal) method.invoke(calculator, operand1, operand2);
		BigDecimal expectedResult = new BigDecimal("3");
		
		assertTrue(result.compareTo(expectedResult) == 0);
		
		BigDecimal operand1_2 = new BigDecimal("1.34");
		BigDecimal operand2_2 = new BigDecimal("2.58");
		
		BigDecimal result_2 = (BigDecimal) method.invoke(calculator, operand1_2, operand2_2);
		BigDecimal expectedResult_2 = new BigDecimal("3.92");
		
		assertTrue(result_2.compareTo(expectedResult_2) == 0);
	}
	
	@Test
	void testSubtract() throws NoSuchMethodException, IllegalAccessException,
						IllegalArgumentException, InvocationTargetException {
		
		Method method = Calculator.class.getDeclaredMethod("subtract", BigDecimal.class, BigDecimal.class);
		method.setAccessible(true);
		
		BigDecimal operand1 = new BigDecimal("3");
		BigDecimal operand2 = new BigDecimal("2");
		
		BigDecimal result = (BigDecimal) method.invoke(calculator, operand1, operand2);
		BigDecimal expectedResult = new BigDecimal("1");
		
		assertTrue(result.compareTo(expectedResult) == 0);
		
		BigDecimal operand1_2 = new BigDecimal("3.92");
		BigDecimal operand2_2 = new BigDecimal("2.58");
		
		BigDecimal result_2 = (BigDecimal) method.invoke(calculator, operand1_2, operand2_2);
		BigDecimal expectedResult_2 = new BigDecimal("1.34");
		
		assertTrue(result_2.compareTo(expectedResult_2) == 0);
	}
	
	@Test
	void testMultiply() throws NoSuchMethodException, IllegalAccessException,
						IllegalArgumentException, InvocationTargetException {
		
		Method method = Calculator.class.getDeclaredMethod("multiply", BigDecimal.class, BigDecimal.class);
		method.setAccessible(true);
		
		BigDecimal operand1 = new BigDecimal("2");
		BigDecimal operand2 = new BigDecimal("4");
		
		BigDecimal result = (BigDecimal) method.invoke(calculator, operand1, operand2);
		BigDecimal expectedResult = new BigDecimal("8");
		
		assertTrue(result.compareTo(expectedResult) == 0);
		
		BigDecimal operand1_2 = new BigDecimal("3.92");
		BigDecimal operand2_2 = new BigDecimal("2.58");
		
		BigDecimal result_2 = (BigDecimal) method.invoke(calculator, operand1_2, operand2_2);
		BigDecimal expectedResult_2 = new BigDecimal("10.1136");
		
		assertTrue(result_2.compareTo(expectedResult_2) == 0);
	}
	
	@Test
	void testDivide() throws NoSuchMethodException, IllegalAccessException,
						IllegalArgumentException, InvocationTargetException {
		
		Method method = Calculator.class.getDeclaredMethod("divide", BigDecimal.class, BigDecimal.class);
		method.setAccessible(true);
		
		BigDecimal operand1 = new BigDecimal("8");
		BigDecimal operand2 = new BigDecimal("2");
		
		BigDecimal result = (BigDecimal) method.invoke(calculator, operand1, operand2);
		BigDecimal expectedResult = new BigDecimal("4");
		
		assertTrue(result.compareTo(expectedResult) == 0);
		
		BigDecimal operand1_2 = new BigDecimal("10");
		BigDecimal operand2_2 = new BigDecimal("3");
		
		BigDecimal result_2 = (BigDecimal) method.invoke(calculator, operand1_2, operand2_2);
		BigDecimal expectedResult_2 =
				new BigDecimal("3.33333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333333");
		
		assertTrue(result_2.compareTo(expectedResult_2) == 0);		
	}

}
