package com.calculator.server.exec;

public class NumberHolder {

    private final int MIN;
    private final int MAX;

    private int number;


    public NumberHolder(String s) {
        this(Integer.parseInt(s));
    }

    public NumberHolder(String s, int min, int max) {
        this(Integer.parseInt(s), min, max);
    }

    public NumberHolder(int number) {
        this(number, Config.MIN_NUMBER, Config.MAX_NUMBER);
    }

    public NumberHolder(int i, int min, int max) {
        this.MIN = min;
        this.MAX = max;
        validate(i);
        this.number = i;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int i) {
        validate(i);
        this.number = i;
    }

    private void validate(int i) {
        if (!(i >= MIN && i <= MAX)) {
            throw new NumberFormatException();
        }
    }

    @Override
    public String toString() {
        return ResponseConstants.RESULT + " " + number;
    }

}
