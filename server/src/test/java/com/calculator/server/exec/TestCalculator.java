package com.calculator.server.exec;

import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestCalculator {

    private static Calculator calculator;

    @BeforeClass
    public static void setUp() {
        calculator = new Calculator();
    }

    @Test
    public void testInvalidInvalidArgumentsNumber() {
        assertEquals(ResponseConstants.ERROR, calculator.calculate("PLUS 1"));
    }

    @Test
    public void testTwoValidNumbers() {
        assertEquals(ResponseConstants.RESULT + " 3", calculator.calculate("PLUS 1 2"));
        assertEquals(ResponseConstants.RESULT + " 3", calculator.calculate("MINUS 10 7"));
        assertEquals(ResponseConstants.RESULT + " 2", calculator.calculate("DIVIDE 10 5"));
        assertEquals(ResponseConstants.RESULT + " 50", calculator.calculate("TIMES 10 5"));

    }

    @Test
    public void testThreeValidNumbers() {
        assertEquals(ResponseConstants.RESULT + " 6", calculator.calculate("PLUS 1 2 3"));
        assertEquals(ResponseConstants.RESULT + " 5", calculator.calculate("MINUS 10 2 3"));
        assertEquals(ResponseConstants.RESULT + " 5", calculator.calculate("DIVIDE 100 2 10"));
        assertEquals(ResponseConstants.RESULT + " 100", calculator.calculate("TIMES 10 5 2"));
    }

    @Test
    public void testInvalidOperator() {
        assertEquals(ResponseConstants.ERROR, calculator.calculate("A 1 2"));
    }

    @Test
    public void testInvalidNumber() {
        assertEquals(ResponseConstants.ERROR, calculator.calculate("PLUS 1 A"));
        assertEquals(ResponseConstants.ERROR, calculator.calculate("PLUS 1 -1"));
        assertEquals(ResponseConstants.ERROR, calculator.calculate("PLUS 1 5005500"));
    }

    @Test
    public void testInvalidResult() {
        assertEquals(ResponseConstants.ERROR, calculator.calculate("DIVIDE 1 0"));
        assertEquals(ResponseConstants.ERROR, calculator.calculate("TIMES 100 -1"));
        assertEquals(ResponseConstants.ERROR, calculator.calculate("TIMES 100 100 100 100"));
    }

    @Test
    public void testOperatorIgnoreCase() {
        assertEquals(ResponseConstants.RESULT + " 6", calculator.calculate("PLUS 1 2 3"));
        assertEquals(ResponseConstants.RESULT + " 6", calculator.calculate("plus 1 2 3"));
        assertEquals(ResponseConstants.RESULT + " 6", calculator.calculate("PlUs 1 2 3"));
    }

}
