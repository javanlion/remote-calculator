package com.calculator.client;

import com.calculator.common.util.ConnectionConstants;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

import static com.calculator.common.util.LogUtils.log;


public class Client implements Runnable {

    private final String host;
    private final int port;
    private SocketChannel channel;
    private Selector selector;
    private Scanner scanner;

    public Client(String host, int port) {
        this.port = port;
        this.host = host;
    }

    public Client(int port) {
        this("localhost", port);
    }

    public Client() {
        this(50000);
    }

    private void init() {
        log("initializing client");
        if (selector != null) return;
        if (channel != null) return;

        try {
            selector = Selector.open();
            channel = SocketChannel.open();
            channel.configureBlocking(false);

            channel.register(selector, SelectionKey.OP_CONNECT);
            channel.connect(new InetSocketAddress(host, port));
            scanner = new Scanner(System.in);

        } catch (IOException e) {
            throw new RuntimeException("Cannot initialize client...", e);
        }
    }

    @Override
    public void run() {
        try {
            init();
            while (!Thread.interrupted()) {
                selector.select(1000);
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();

                    if (!key.isValid()) continue;

                    if (key.isConnectable()) {
                        log("Connecting to the server");
                        connect(key);
                        log("Connected");
                    }
                    if (key.isWritable()) {
                        log("writing...");
                        write(key);
                    }
                    if (key.isReadable()) {
                        read(key);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            close();
        }
    }

    private void close() {
        try {
            selector.close();
            scanner.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer readBuffer = ByteBuffer.allocate(1000);
        readBuffer.clear();
        int length;
        try {
            length = channel.read(readBuffer);
        } catch (IOException e) {
            log("Reading problem, closing connection");
            close();
            return;
        }
        if (length == -1) {
            log("Connection closed by server");
            close();
            System.exit(0);
        }
        readBuffer.flip();
        byte[] buff = new byte[1024];
        readBuffer.get(buff, 0, length);
        String answer = new String(buff).trim();
        log("Server said: " + answer);
        if (ConnectionConstants.CLOSED.equals(answer)) {
            key.interestOps(SelectionKey.OP_READ);
        } else {
            key.interestOps(SelectionKey.OP_WRITE);
        }
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        String line = scanner.nextLine();
        if ("".equals(line)) {
            key.interestOps(SelectionKey.OP_WRITE);
        } else {
            channel.write(ByteBuffer.wrap(line.getBytes()));
            log("send: " + line);
            key.interestOps(SelectionKey.OP_READ);
        }
    }

    private void connect(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        if (channel.isConnectionPending()) {
            channel.finishConnect();
        }
        channel.configureBlocking(false);
        channel.register(selector, SelectionKey.OP_WRITE);
    }
}
