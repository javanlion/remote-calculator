package com.calculator.server;

public class ServerRunner {

    public static void main(String[] args) {
        int length = args.length;
        if (length > 2) throw new IllegalArgumentException("Argument count should be 2 or less. Received: " + length);

        Server server;
        if (length == 2) {
            server = new Server(args[0], Integer.parseInt(args[1]));
        } else if (length == 1) {
            server = new Server(Integer.parseInt(args[0]));
        } else {
            server = new Server();
        }
        new Thread(server).start();
    }
}
