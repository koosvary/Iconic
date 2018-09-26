package org.iconic.ea.chromosome.expression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main{
	public static void main(String[] args){
		String expression = "(((6*B)/(3*B)))+(((((2*B)-(3*B)))-(((1*B)*(1*B)))))+(-3*B)+(3*B)";
		System.out.println(expression);

		//TODO: see if you can fix the minus thing...
		String coXPlusCoXPattern = "\\(([-?0-9]+)?\\*?([A-Za-z0-9_]+)\\)\\+\\(([-?0-9]+)?\\*?\\2\\)";
		String coXMinusCoXPattern = "\\(([-?0-9]+)?\\*?([A-Za-z0-9_]+)\\)\\-\\(([-?0-9]+)?\\*?\\2\\)";
		String coXTimesCoXPattern = "\\(([-?0-9]+)?\\*?([A-Za-z0-9_]+)\\)\\*\\(([-?0-9]+)?\\*?\\2\\)";
		String coXDividesCoXPattern = "\\(([-?0-9]+)?\\*?([A-Za-z0-9_]+)\\)\\/\\(([-?0-9]+)?\\*?\\2\\)";
		//TODO:
		String indicesPattern = "\\(([-?0-9]+)?\\*?([A-Za-z0-9_]+)\\)\\/\\(([-?0-9]+)?\\*?\\2\\)";
		//check fractions

		Pattern pattern = Pattern.compile(coXPlusCoXPattern);
		Matcher matcher = pattern.matcher(expression);

		if(matcher.find()){
			int a = 1, b = 1;
			if(matcher.group(1) != null){
				a = Integer.parseInt(matcher.group(1));
			}
			if(matcher.group(3) != null){
				b = Integer.parseInt(matcher.group(3));
			}
			if(a+b == 0){
				expression = expression.replaceAll(coXPlusCoXPattern, "(0)");
			}
			else if(a+b == 1){
				expression = expression.replaceAll(coXPlusCoXPattern, "($2)");
			}
			else if(a+b == -1){
				expression = expression.replaceAll(coXPlusCoXPattern, "(-$2)");
			}
			else{
				expression = expression.replaceAll(coXPlusCoXPattern, "(" + (a+b) + "*$2)");
			}

		}
		System.out.println(expression);
		pattern = Pattern.compile(coXMinusCoXPattern);
		matcher = pattern.matcher(expression);

		if(matcher.find()){
			int a = 1, b = 1;
			if(matcher.group(1) != null){
				a = Integer.parseInt(matcher.group(1));
			}
			if(matcher.group(3) != null){
				b = Integer.parseInt(matcher.group(3));
			}
			if(a-b == 0){
				expression = expression.replaceAll(coXMinusCoXPattern, "(0)");
			}
			else if(a-b == 1){
				expression = expression.replaceAll(coXMinusCoXPattern, "($2)");
			}
			else if(a-b == -1){
				expression = expression.replaceAll(coXMinusCoXPattern, "(-$2)");
			}
			else{
				expression = expression.replaceAll(coXMinusCoXPattern, "(" + (a - b) + "*$2)");
			}
		}
		System.out.println(expression);
		pattern = Pattern.compile(coXTimesCoXPattern);
		matcher = pattern.matcher(expression);

		if(matcher.find()){
			int a = 1, b = 1;
			if(matcher.group(1) != null){
				a = Integer.parseInt(matcher.group(1));
			}
			if(matcher.group(3) != null){
				b = Integer.parseInt(matcher.group(3));
			}
			if(a*b == 1){
				expression = expression.replaceAll(coXTimesCoXPattern, "($2^2)");
			}
			else if(a*b == -1){
				expression = expression.replaceAll(coXTimesCoXPattern, "(-$2^2)");
			}
			else{
				expression = expression.replaceAll(coXTimesCoXPattern, "(" + (a * b) + "$2^2)");
			}
		}
		System.out.println(expression);
		pattern = Pattern.compile(coXDividesCoXPattern);
		matcher = pattern.matcher(expression);

		if(matcher.find()){
			int a = 1, b = 1;
			if(matcher.group(1) != null){
				a = Integer.parseInt(matcher.group(1));
			}
			if(matcher.group(3) != null){
				b = Integer.parseInt(matcher.group(3));
			}
			if(a/b == 1){
				expression = expression.replaceAll(coXDividesCoXPattern, "(1)");
			}
			else if(a/b == -1){
				expression = expression.replaceAll(coXDividesCoXPattern, "(-1)");
			}
			else if((float)a/(float)b < 0){
				expression = expression.replaceAll(coXDividesCoXPattern, "(-(" + Math.abs(a) + "/" + Math.abs(b) + "))");
			}
			else if(a < 0 && b < 0){
				expression = expression.replaceAll(coXDividesCoXPattern, "(" + Math.abs(a) + "/" + Math.abs(b) + ")");
			}
			else{
				expression = expression.replaceAll(coXDividesCoXPattern, "(" + a + "/" + b + ")");
			}
		}
//		expression = expression.replaceAll(xPlusXPattern, "(2*$1)");
//		expression = expression.replaceAll(coXPlusXPattern, "(2*$2)");
//		expression = expression.replaceAll(xPlusCoXPattern, "(2*$1)");
//		expression = expression.replaceAll(coXPlusCoXPattern, "(2*$2)");
		System.out.println(expression);
//		expression = expression.replaceAll(xMinusXPattern, "(0)");
//		System.out.println(expression);
//		expression = expression.replaceAll(xTimesXPattern, "($1^2)");
//		System.out.println(expression);
//		expression = expression.replaceAll(xDividesXPattern, "(1)");
//		System.out.println(expression);
//
//		expression = expression.replaceAll("\\(([A-Za-z0-9\\*\\-\\+\\/]+)\\)\\-\\(([A-Za-z0-9\\*\\-\\+\\/]+)\\)", "($1-$2)");
//		System.out.println(expression);
//		expression = expression.replaceAll("([A-Za-z0-9\\*\\-\\+\\/]+)[\\-\\+]\\(0\\)", "$1");
//		System.out.println(expression);
	}
}