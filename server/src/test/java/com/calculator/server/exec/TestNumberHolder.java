package com.calculator.server.exec;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestNumberHolder {

    @Test
    public void testNumberHolderToString() {
        assertEquals(ResponseConstants.RESULT + " " + 10, new NumberHolder(10, 1, 100).toString());
    }

    @Test(expected = NumberFormatException.class)
    public void testInstantiatingWithInvalidNumber() {
        new NumberHolder(-10, 0, 100);
    }

    @Test(expected = NumberFormatException.class)
    public void testSettingInvalidNumber() {
        NumberHolder numberHolder = new NumberHolder(10, 0, 49);
        numberHolder.setNumber(50);
    }

    @Test(expected = NumberFormatException.class)
    public void testInstantiatingWithInvalidString() {
        new NumberHolder("Invalid");
    }

}
