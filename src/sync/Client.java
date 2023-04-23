package sync;

import java.io.*;
import java.net.*;

public class Client {

    private String directory;
    private String hostDomain;
    private int port;

    public Client(String directory, String hostDomain, int port) {
        this.directory = directory;
        this.hostDomain = hostDomain;
        this.port = port;
    }

    public static void main(String[] args) throws IOException {
        String directory = "C:\\Users\\LINPa\\Documents\\test";
        String hostDomain = "localhost";
        int port = 12345;

        Client client = new Client(directory, hostDomain, port);
        client.sendFiles();
    }

    public void sendFiles() throws IOException {
        File[] files = new File(directory).listFiles();

        Socket socket = new Socket("localhost", port);

        BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
        DataOutputStream dos = new DataOutputStream(bos);

        dos.writeInt(files.length);

        for (File file : files) {
            long length = file.length();
            dos.writeLong(length);

            String name = file.getName();
            dos.writeUTF(name);

            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);

            int theByte = 0;
            while ((theByte = bis.read()) != -1) {
                bos.write(theByte);
            }

            bis.close();
        }

        dos.close();
    }
}
