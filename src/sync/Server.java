package sync;

import java.io.*;
import java.net.*;

public class Server {
    ServerSocket serverSocket;
    Socket clientSocket;
    DataInputStream in;
    FileOutputStream out;
    String folder;
    public static void main(String[] args) throws Exception {
        Server server = new Server();
        server.folder = "C:\\Users\\Peter\\Documents\\test_copy";
        server.start(8000);
        server.stop();
    }

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        clientSocket = serverSocket.accept();
        in = new DataInputStream(clientSocket.getInputStream());

        while (true) {
            int type = in.readInt();
            String name = in.readUTF();
            if (type == 1) {
                File directory = new File(folder + "\\" + name);
                if (!directory.exists()) {
                    directory.mkdir();
                    System.out.println("Created directory: " + name);
                }
            } else if (type == 0) {
                File file = new File(folder + "\\" + name);
                out = new FileOutputStream(name);
                byte[] buffer = new byte[4096];
                int bytesRead = -1;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
                out.close();
                System.out.println("Created file: " + name);
            }
        }
    }

    public void stop() throws IOException {
        in.close();
        clientSocket.close();
        serverSocket.close();
    }
}
