package sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        String sourceFolderPath = "C:\\Users\\Peter\\Documents\\test";
        String destinationFolderPath = "C:\\Users\\LinPa\\Documents";
        try {
            FileInputStream fileInputStream = new FileInputStream(sourceFolderPath);
            Socket socket = new Socket("192.168.1.100", 8000); // create client socket and connect to server
            System.out.println("Connection established!"); // print message to console
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.write(sourceFolderPath.getBytes());
            dataOutputStream.write(destinationFolderPath.getBytes());
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            String response = dataInputStream.readUTF();

            System.out.println(response);

        }
        catch (IOException f) {
            System.out.println("Error: " + f);
        }
    }
}
