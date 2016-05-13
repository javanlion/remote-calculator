package com.calculator.client;

public class ClientRunner {

    public static void main(String[] args) {
        int length = args.length;
        if (length > 2) throw new IllegalArgumentException("Argument count should be 2 or less. Received: " + length);

        Client client;
        if (length == 2) {
            client = new Client(args[0], Integer.parseInt(args[1]));
        } else if (length == 1) {
            client = new Client(Integer.parseInt(args[0]));
        } else {
            client = new Client();
        }
        new Thread(client).start();
    }
}
