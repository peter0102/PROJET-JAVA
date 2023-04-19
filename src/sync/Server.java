package sync;

import java.net.*;
import java.io.*;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234); // create server socket
        Socket clientSocket = serverSocket.accept(); // wait for client to connect
        System.out.println("Connection established!"); // print message to console
        serverSocket.close(); // close server socket
    }
}