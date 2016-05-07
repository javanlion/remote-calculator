package com.calculator.server.exec;

public class Calculator {

    public String calculate(String line) {
        int i = 0;
        String[] split = line.split(Config.DELIMITER);
        try {

            // return error if argument quantity is less than allowed
            if (!(split.length >= Config.MIN_ARGUMENTS_QUANTITY)) {
                return ResponseConstants.ERROR;
            }

            // get operator, throws IllegalArgumentException if operator is not valid
            Operator operator = Operator.valueOf(split[i].toUpperCase());

            NumberHolder num1 = new NumberHolder(split[++i]);
            NumberHolder num2 = new NumberHolder(split[++i]);

            NumberHolder result = new NumberHolder(operator.eval(num1.getNumber(), num2.getNumber()));

            // calculate result for other values if any
            for (int j = ++i; j < split.length; j++) {
                result.setNumber(operator.eval(result.getNumber(), new NumberHolder(split[j]).getNumber()));
            }

            return result.toString();
        } catch (Exception e) {
            return ResponseConstants.ERROR;
        }
    }

}
