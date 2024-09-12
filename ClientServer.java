import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientServer {

    public void startClient() {
        try {
            Socket clientServer = new Socket("localhost", 1337);
            System.out.println("Connected to server");

            BufferedReader in = new BufferedReader(new InputStreamReader(clientServer.getInputStream()));
            PrintWriter out = new PrintWriter(clientServer.getOutputStream(), true);
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

            // Thread for listening to server responses
            new Thread(new ServerListener(in)).start();

            String msg;
            // Sending user input to the server
            while ((msg = reader.readLine()) != null) {
                out.println(msg);  // Client sends message to the server
            }

            clientServer.close();

        } catch (Exception e) {
            System.err.println("Client error: " + e);
        }
    }

    // Inner class to handle server messages in a separate thread
    private static class ServerListener implements Runnable {
        private final BufferedReader serverInput;

        public ServerListener(BufferedReader in) {
            this.serverInput = in;
        }

        @Override
        public void run() {
            try {
                String response;
                while ((response = serverInput.readLine()) != null) {
                    System.out.println("Server: " + response);
                }
            } catch (Exception e) {
                System.err.println("Server listener error: " + e);
            }
        }
    }

    public static void main(String[] args) {
        ClientServer client = new ClientServer();
        client.startClient();
    }
}
