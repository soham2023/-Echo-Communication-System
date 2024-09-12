import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public void startServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(1337);
            System.out.println("Server started, waiting for clients...");

            // Keep the server running to handle multiple clients
            while (true) {
                Socket clientSocket = serverSocket.accept(); // Accept a client connection
                System.out.println("Client connected.");

                // Create a new thread for each client
                new Thread(new ClientHandler(clientSocket)).start();
            }
        } catch (Exception e) {
            System.err.println("Server error: " + e);
        }
    }

    // Inner class to handle client communication in a separate thread
    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader consoleInput = new BufferedReader(new InputStreamReader(System.in));

                // Separate thread for reading server inputs and sending messages to clients
                new Thread(() -> {
                    try {
                        String serverMsg;
                        while ((serverMsg = consoleInput.readLine()) != null) {
                            out.println(serverMsg);  // Server sends message to the client
                        }
                    } catch (Exception e) {
                        System.err.println("Error in server's input handler: " + e);
                    }
                }).start();

                String clientMessage;
                while ((clientMessage = in.readLine()) != null) {
                    System.out.println("Client: " + clientMessage);
                    out.println("Server received: " + clientMessage);
                }

                clientSocket.close();
            } catch (Exception e) {
                System.err.println("Client communication error: " + e);
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();
    }
}
