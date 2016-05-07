package com.calculator.server;

public class ServerRunner {

    public static void main(String[] args) {
        new Thread(new Server()).start();
    }
}
