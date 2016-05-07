package com.calculator.server.exec;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestOperator {

    @Test
    public void testPlus() {
        assertEquals(3, Operator.PLUS.eval(1, 2));
    }

    @Test
    public void testMinus() {
        assertEquals(1, Operator.MINUS.eval(2, 1));
    }

    @Test
    public void testDivide() {
        assertEquals(5, Operator.DIVIDE.eval(10, 2));
    }

    @Test
    public void testTimes() {
        assertEquals(10, Operator.TIMES.eval(2, 5));
    }
}
