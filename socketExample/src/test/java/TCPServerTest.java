import org.example.TCPServer;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TCPServerTest {
    private static final int PORT = 12345;
    private static ServerSocket serverSocket;

    @BeforeAll
    public static void setUp() throws IOException {
        serverSocket = new ServerSocket(PORT);
        Thread serverThread = new Thread(() -> {
            try {
                Socket socket = serverSocket.accept();
                new TCPServer.ClientHandler(socket).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThread.start();
    }

    @Test
    public void testIncrement() throws IOException {
        try (Socket socket = new Socket("localhost", PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            writer.println("increment");
            String response = reader.readLine();
            assertTrue(response.contains("Counter incremented"));

            writer.println("get");
            response = reader.readLine();
            assertEquals("Current counter value: 1", response);
        }
    }

    @Test
    public void testDecrement() throws IOException {
        try (Socket socket = new Socket("localhost", PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            writer.println("decrement");
            String response = reader.readLine();
            assertTrue(response.contains("Counter decremented"));

            writer.println("get");
            response = reader.readLine();
            assertEquals("Current counter value: -1", response);
        }
    }

    @Test
    public void testUnknownCommand() throws IOException {
        try (Socket socket = new Socket("localhost", PORT);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            writer.println("unknown");
            String response = reader.readLine();
            assertEquals("Unknown command.", response);
        }
    }


}
