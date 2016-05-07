package com.calculator.common.util;

import java.util.Date;

public class LogUtils {
    public static void log(Object object) {
        System.out.println(new Date() + ": " + object);
    }
}
