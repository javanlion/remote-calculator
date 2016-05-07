package com.calculator.server;

import com.calculator.common.util.ConnectionConstants;
import com.calculator.server.exec.Calculator;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.calculator.common.util.LogUtils.log;

public class Server implements Runnable {

    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 50000;
    private static final long TIMEOUT = 10000;
    private final String host;
    private final int port;
    private final Calculator calculator;
    private final Map<SocketChannel, byte[]> dataTracking;
    private ServerSocketChannel serverChannel;
    private Selector selector;

    public Server(String host, int port) {
        this.port = port;
        this.host = host;
        calculator = new Calculator();
        dataTracking = new HashMap<>();
        init();
    }

    public Server(int port) {
        this(DEFAULT_HOST, port);
    }

    public Server() {
        this(DEFAULT_PORT);
    }

    @Override
    public void run() {
        log("Now accepting connections...");
        try {
            //run the server as long as the thread is not interrupted.
            while (!Thread.currentThread().isInterrupted()) {
                selector.select(TIMEOUT);

                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();

                    if (!key.isValid()) {
                        continue;
                    }

                    if (key.isAcceptable()) {
                        log("Accepting connection");
                        accept(key);
                    } else if (key.isWritable()) {
                        log("Writing...");
                        write(key);
                    } else if (key.isReadable()) {
                        log("Reading connection");
                        read(key);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error during processing", e);
        } finally {
            closeConnection();
        }

    }

    private void init() {
        log("initializing server");
        if (selector != null) return;
        if (serverChannel != null) return;

        try {
            selector = Selector.open();
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.socket().bind(new InetSocketAddress(host, port));
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        } catch (IOException e) {
            throw new RuntimeException("Cannot initialize server...", e);
        }
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = serverSocketChannel.accept();
        socketChannel.configureBlocking(false);

        socketChannel.register(selector, SelectionKey.OP_WRITE);
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        byte[] data = dataTracking.remove(channel);

        if (data == null) {
            key.interestOps(SelectionKey.OP_READ);
            return;
        } else if (ConnectionConstants.CLOSED.equals(new String(data))) {
            channel.write(ByteBuffer.wrap(ConnectionConstants.CLOSED.getBytes()));
            log("send: " + ConnectionConstants.CLOSED);
            channel.close();
        } else {
            channel.write(ByteBuffer.wrap(data));
            log("send: " + new String(data));
            key.interestOps(SelectionKey.OP_READ);
        }
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer readBuffer = ByteBuffer.allocate(1024);
        readBuffer.clear();
        int read;
        try {
            read = channel.read(readBuffer);
        } catch (IOException e) {
            log("Reading problem, closing connection");
            key.cancel();
            channel.close();
            return;
        }
        if (read == -1) {
            log("Nothing was there to be read, closing connection");
            channel.close();
            key.cancel();
            return;
        }

        readBuffer.flip();
        byte[] data = new byte[1000];
        readBuffer.get(data, 0, read);
        String receivedString = new String(data).trim();
        log("Received: " + receivedString);

        if (ConnectionConstants.OK_CLOSE.equals(receivedString)) {
            data = ConnectionConstants.CLOSED.getBytes();
        } else {
            data = calculator.calculate(receivedString).getBytes();
        }
        writeMessage(key, data);
    }

    private void writeMessage(SelectionKey key, byte[] data) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        dataTracking.put(socketChannel, data);
        key.interestOps(SelectionKey.OP_WRITE);
    }

    private void closeConnection() {
        log("Closing server down");
        if (selector != null) {
            try {
                selector.close();
                serverChannel.socket().close();
                serverChannel.close();
            } catch (IOException e) {
                throw new RuntimeException("Error during closing server", e);
            }
        }
    }
}
