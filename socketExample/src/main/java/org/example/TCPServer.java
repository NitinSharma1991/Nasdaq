package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class TCPServer {
    private static final AtomicInteger counter = new AtomicInteger(0);
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(10);

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Server is listening on port 12345");
            while (true) {
                Socket socket = serverSocket.accept();
                threadPool.execute(new ClientHandler(socket));
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static class ClientHandler extends Thread {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try (InputStream input = socket.getInputStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                 OutputStream output = socket.getOutputStream();
                 PrintWriter writer = new PrintWriter(output, true)) {

                String command;

                while ((command = reader.readLine()) != null) {
                    System.out.println("Current Thread name for which input has received " + Thread.currentThread());
                    String response = handleCommand(command);
                    writer.println(response);
                }

            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        private String handleCommand(String command) {
            return switch (command.toLowerCase()) {
                case "increment" -> {
                    counter.addAndGet(1);
                    yield "Counter incremented. Current value: " + counter.get();
                }
                case "decrement" -> {
                    counter.decrementAndGet();
                    yield "Counter decremented. Current value: " + counter.get();
                }
                case "get" -> "Current counter value: " + counter.get();
                default -> "Unknown command.";
            };
        }
    }
}
