package sync;

import java.io.*;
import java.net.*;

public class Client {
    DataOutputStream out;
    Socket socket;
    public static void main(String[] args) throws Exception {
        Client client = new Client();
        client.startConnection("localhost", 8000);
        File file = new File("C:\\Users\\Peter\\Documents\\test");
        client.sendFiles(file);
        client.stopConnection();
    }
    public void sendFiles(File file) {
        File[] files = file.listFiles();
        for (File sourceFile : files) {
            if (sourceFile.isDirectory()) {
                try {
                    out.writeInt(1);
                    out.writeUTF(sourceFile.getName());
                    sendFiles(sourceFile);
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            if (sourceFile.isFile()) {
                try {
                    out.writeInt(0);
                    out.writeUTF(sourceFile.getName());
                    FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    byte[] buffer = new byte[4096];
                    int bytesRead = -1;
                    while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                    fileInputStream.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            }
        }
    }
    public void startConnection(String ip, int port) throws Exception {
        socket = new Socket(ip, port);
        System.out.println("Connected to server");
        out = new DataOutputStream(socket.getOutputStream());
    }

    public void stopConnection() throws Exception {
        out.close();
        socket.close();
    }

}
