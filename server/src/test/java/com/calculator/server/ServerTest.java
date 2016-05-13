package com.calculator.server;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class ServerTest {
    private Thread serverThread;

    @Before
    public void setUp() {
        serverThread = new Thread(new Server(55555));
        serverThread.start();
    }

    @After
    public void tearDown() {
        serverThread.interrupt();
    }

    @Test
    public void testRun() throws Exception {
        System.out.println("test");
    }
}