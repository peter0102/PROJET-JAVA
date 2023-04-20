package sync;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        String directory = "C:\\Users\\Peter\\Documents\\test";

        File[] files = new File(directory).listFiles();

        Socket socket = new Socket("192.168.1.100", 8000);

        BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
        DataOutputStream dos = new DataOutputStream(bos);

        dos.writeInt(files.length);

        for (File file : files) {
            sendFile(file,socket);
        }

        dos.close();
    }

    public static void sendFile(File file,Socket socket) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
        DataOutputStream dos = new DataOutputStream(bos);
        if (file.isFile()) {

            long length = file.length();
            dos.writeLong(length);

            String name = file.getName();
            dos.writeUTF(name);

            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);

            int theByte = 0;
            while ((theByte = bis.read()) != -1)
                bos.write(theByte);

            bis.close();
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            dos.writeInt(files.length);
            for (File f : files) {
                sendFile(f, socket);
            }
        }
        dos.close();
    }
    
}
