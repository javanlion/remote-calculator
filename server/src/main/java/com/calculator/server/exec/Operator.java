package com.calculator.server.exec;

public enum Operator {
    PLUS {
        @Override
        public int eval(int x, int y) {
            return x + y;
        }
    },
    MINUS {
        @Override
        public int eval(int x, int y) {
            return x - y;
        }
    },
    TIMES {
        @Override
        public int eval(int x, int y) {
            return x * y;
        }
    },
    DIVIDE {
        @Override
        public int eval(int x, int y) {
            return x / y;
        }
    };

    public abstract int eval(int x, int y);
}