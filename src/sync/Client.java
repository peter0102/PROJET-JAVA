package sync;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Base64;

public class Client {
    Socket socket;
    PrintWriter out;
    BufferedReader in;
    String sourceFolder = "C:\\Users\\Peter\\Documents\\test";

    public static void main(String[] args) throws IOException {
        Client client = new Client();
        client.startConnection("localhost", 8000);
        client.stopConnection();
    }

    public void startConnection(String host, int port) throws IOException {
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        File file = new File(sourceFolder);
        send(file);
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    public void send(File file) {
        File[] files = file.listFiles();
        for (File sourceFile : files) {
            String buffer = "";
            if (sourceFile.isDirectory()) {
                buffer += "1||";
                buffer += sourceFile.getPath().substring(this.sourceFolder.length());
                out.println(buffer);
                out.flush();
                send(sourceFile);
            }
            if (sourceFile.isFile()) {
                buffer += "0||";
                buffer += sourceFile.getPath().substring(this.sourceFolder.length()) + "||";
                try {
                    byte[] bytes = Files.readAllBytes(sourceFile.toPath());
                    if (bytes == null) {
                        buffer += "";
                    } else {
                        buffer += Base64.getEncoder().encodeToString(bytes);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                out.println(buffer);
                out.flush();
            }
        }
        if ((file.getPath() + File.separator).equals(this.sourceFolder) || file.getPath().equals(this.sourceFolder)) {
            System.out.println("End send");
            out.println("end");
            out.flush();
        }
    }
}
