package sync;

import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) throws Exception {
        Socket clientSocket = new Socket("localhost", 1234);
        OutputStream outputStream = clientSocket.getOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        String folderPath = "C:\\Users\\Peter\\Documents\\test"; //replace with the path of the folder to be sent
        File folder = new File(folderPath);
        sendFolder(folder, objectOutputStream);
        objectOutputStream.close();
        outputStream.close();
        clientSocket.close();
    }

    private static void sendFolder(File folder, ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeObject(folder);
        File[] files = folder.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                sendFolder(file, objectOutputStream);
            } else {
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) > 0) {
                    objectOutputStream.write(buffer, 0, bytesRead);
                }
                fileInputStream.close();
            }
        }
    }
}
