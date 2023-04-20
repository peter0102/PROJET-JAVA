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
        try {
            Socket socket = new Socket("192.168.1.100", 8000); // create client socket and connect to server
            System.out.println("Connection established!"); // print message to console
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataOutputStream.writeUTF(sourceFolderPath);
            dataOutputStream.writeUTF(destinationFolderPath);
            File sourceFolder = new File(sourceFolderPath);
            File[] files = sourceFolder.listFiles();
            for (File file : files) {
                if (file.isFile()) {
                    dataOutputStream.writeUTF(file.getName());
                    FileInputStream fileInputStream = new FileInputStream(file);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fileInputStream.read(buffer)) > 0) {
                        dataOutputStream.write(buffer, 0, length);
                    }
                    fileInputStream.close();
                    System.out.println("Sent file: " + file.getName());
                }
            }
            dataOutputStream.writeUTF("done");
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            String response = dataInputStream.readUTF();
            System.out.println(response);

        }
        catch (IOException f) {
            System.out.println("Error: " + f);
        }
    }
}
