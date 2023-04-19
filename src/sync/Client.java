package sync;

import java.io.IOException;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("server_hostname", 1234); // create client socket and connect to server
        System.out.println("Connection established!"); // print message to console
        socket.close(); // close client socket
    }
}
