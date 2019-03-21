package com.tocco.testproject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Scanner;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

public class Calculator {
	
	public static void main(String[] args) {
		
		System.out.println("Enter math expression");
		Scanner in = new Scanner(System.in);
		String expression = in.nextLine();
		in.close();
		
		Vector<String> postFixExpression = new Vector<String>();
		if(parseArguments(expression, postFixExpression) != 0) {
			System.exit(1);
		}
		
		if(calculat(postFixExpression) != 0) {
			System.exit(1);
		}
		
		System.out.println(postFixExpression.elementAt(0));
	}
	
	private static int parseArguments(String expression, Vector<String> postFixExpression) {
		
		/* check allowed characters */
		if(!expression.matches("^[0-9.*/+() -]*$")) {
			System.out.println("There is a wrong character in the argument!");
			System.out.println("Only following characters will be accepted:");
			System.out.println("Digits:\t\t\t0-9");
			System.out.println("Floating-point sign:\t.");
			System.out.println("Mathsigns:\t\t* / + -");
			System.out.println("Parentheses:\t\t( )");
			
			return 1;			
		}
		
		/* add spaces */
		expression = expression.replace("(", " ( ");
		expression = expression.replace(")", " ) ");
		expression = expression.replace("+", " + ");
		expression = expression.replace("-", " - ");
		expression = expression.replace("*", " * ");
		expression = expression.replace("/", " / ");
		while(expression.indexOf("  ") != -1) {
			expression = expression.replace("  ", " ");
		}
		
		/* Shunting-yard algorithm change infix notation to postfix notation */
		Stack<String> operatorStack = new Stack<String>();
		StringTokenizer tokenizer = new StringTokenizer(expression, " ");
	    while (tokenizer.hasMoreElements()) {
	        String token = tokenizer.nextToken();
	        
	        if (token.isEmpty()) {
                continue;
	        }
	        
	        if(token.matches("[*/+-]")) {
	        	/* the token is an operator */
	        	while(!operatorStack.isEmpty() &&
	        		  (token.matches("[+-]") && operatorStack.lastElement().matches("[*/+-]") ||
	    	           token.matches("[*/]") && operatorStack.lastElement().matches("[*/]")) &&
	        		   operatorStack.lastElement().compareTo("(") != 0) {
	        		postFixExpression.add(operatorStack.pop());	        		
	        	}
	        	operatorStack.push(token);
	        } else if (token.compareTo("(") == 0) {
	        	/* the token is an open parentheses */
	        	operatorStack.push(token);
	        } else if (token.compareTo(")") == 0) {
	        	/* the token is an close parentheses */
	        	if(operatorStack.contains("(")) {
		        	while(operatorStack.lastElement().compareTo("(") != 0) {
		        		postFixExpression.add(operatorStack.pop());
		        	}
		        	if(operatorStack.lastElement().compareTo("(") == 0) {
		        		operatorStack.pop();
		        	}
	        	} else {
	        		System.out.println("There are mismatched parentheses!");
	        		
	        		return 1;
	        	}
	        } else {
	        	/* the token is a number */
	        	postFixExpression.add(token);
	        }
	    }
	    
	    while(!operatorStack.isEmpty()) {
	    	if(!operatorStack.lastElement().matches("[/(/)]")) {
	    		postFixExpression.add(operatorStack.pop());
	    	} else {
	    		System.out.println("There are mismatched parentheses!");
        		
        		return 1;
	    	}
	    }
		
		return 0;
	}
	
	private static int calculat(Vector<String> postFixExpression) {

		/* check max scale of inserted values */
		int maxScale = 0;		
		for(int i = 0; i < postFixExpression.size(); i++) {
			if(!postFixExpression.elementAt(i).matches("[*/+-]")) {
				BigDecimal number = new BigDecimal(postFixExpression.elementAt(i));
	        	if(maxScale < number.scale()) {
	        		maxScale = number.scale();
				}
			}
		}
		
		while(postFixExpression.size() > 1) {
			/* search for first operator */
			int operatorIndex = -1;
			for(int i = 0; i < postFixExpression.size(); i++) {
				if(postFixExpression.elementAt(i).matches("[*/+-]")) {
					operatorIndex = i;
					break;
				}
			}
			
			if(operatorIndex > -1) {
				/* read and remove the two operands before the operator and the operator itself from the vector
				   check the operator and execute suitable function
				   the result of the function will be written back to the vector at the position of the first operand */
				String operator = postFixExpression.remove(operatorIndex);
				BigDecimal operand2 = new BigDecimal(postFixExpression.remove(operatorIndex - 1));
				BigDecimal operand1 = new BigDecimal(postFixExpression.remove(operatorIndex - 2));
				
				switch(operator) {
					case "*":
						postFixExpression.insertElementAt(multiply(operand1, operand2).toString(), operatorIndex - 2);
						break;
					case "/":
						postFixExpression.insertElementAt(divide(operand1, operand2).toString(), operatorIndex - 2);
						break;
					case "+":
						postFixExpression.insertElementAt(add(operand1, operand2).toString(), operatorIndex - 2);
						break;
					case "-":
						postFixExpression.insertElementAt(subtract(operand1, operand2).toString(), operatorIndex - 2);
						break;
				}
			}
		}
		
		/* round to max scale of inserted values */
		BigDecimal result = new BigDecimal(postFixExpression.remove(0));
		result = result.setScale(maxScale, RoundingMode.HALF_EVEN);
		postFixExpression.add(result.toString());
		
		return 0;
	}
	
	private static BigDecimal add(BigDecimal operand1, BigDecimal operand2) {
		BigDecimal ret = null;
		
		ret = operand1.add(operand2);
		
		return ret;
	}
	
	private static BigDecimal subtract(BigDecimal operand1, BigDecimal operand2) {
		BigDecimal ret = null;
		
		ret = operand1.subtract(operand2);
		
		return ret;
	}
	
	private static BigDecimal multiply(BigDecimal operand1, BigDecimal operand2) {
		BigDecimal ret = null;
		
		ret = operand1.multiply(operand2);
		
		return ret;
	}
	
	private static BigDecimal divide(BigDecimal operand1, BigDecimal operand2) {
		BigDecimal ret = null;
		
		try {
			ret = operand1.divide(operand2, 128, RoundingMode.HALF_EVEN);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return ret;
	}

}
