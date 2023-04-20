package sync;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        String sourceFolderPath = "C:\\Users\\Peter\\Documents\\test";
        String destinationFolderPath = "C:\\Users\\LinPa\\Documents";
        File sourceFile = new File("C:\\Users\\Peter\\Documents\\test\\test1.txt");
        try {
            Socket socket = new Socket("192.168.1.100", 8000); // create client socket and connect to server
            System.out.println("Connection established!"); // print message to console
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF(sourceFolderPath);
            dataOutputStream.writeUTF(destinationFolderPath);
            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                dataOutputStream.write(buffer, 0, bytesRead);
            }
            dataOutputStream.writeUTF("done");
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            String response = dataInputStream.readUTF();
            System.out.println(response);
            fileInputStream.close();
            dataOutputStream.close();
            socket.close();
        }
        catch (IOException f) {
            System.out.println("Error: " + f);
        }
    }
}
