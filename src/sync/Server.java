package sync;

import java.net.*;
import java.util.Base64;
import java.io.*;

public class Server {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    String destinationFolder = "C:\\Users\\Peter\\Documents\\test_copy";

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.startServer(8000);
        server.stopServer();
    }

    public void startServer(Integer port) throws IOException {
        serverSocket = new ServerSocket(port);
        clientSocket = serverSocket.accept();
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        String line = in.readLine();
        while (line != null) {
            receiveFiles(line);
            if (line.equals("end")) {
                break;
            }
            line = in.readLine();
        }
    }
    public void receiveFiles(String data) {
        System.out.println(data);
        String[] separatedData = data.split("\\|\\|");
        if (data.equals("end")) {
            return;
        }
        if (separatedData[0].equals("1")) {
            File folder = new File(destinationFolder+File.separator+separatedData[1]);
            if (!folder.exists()) {
                folder.mkdir();
            }
        }
        else if (separatedData[0].equals("0")) {
            File file = new File(destinationFolder+File.separator+separatedData[1]); 
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                fileOutputStream.write(Base64.getDecoder().decode(separatedData[2]));
                fileOutputStream.close();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void stopServer() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
    }

}
