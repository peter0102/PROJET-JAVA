package sync;

import java.io.IOException;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        try {
            Socket socket = new Socket("localhost", 1234); // create client socket and connect to server
            System.out.println("Connection established!"); // print message to console
            Gui gui = Gui.createAndShowGui();
        }
        catch (IOException e) {
            System.out.println("Error: " + e);
        }
    }
}
