package sync;

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
        Socket socket = new Socket("192.168.1.100", 8000);

        File fileDir = new File("C:\\Users\\Peter\\Documents\\test");
        File[] files = fileDir.listFiles();
        for (File file : files) {
            if (file.isFile()) {
                byte[] bytes = new byte[1024];
                InputStream in = new FileInputStream(file);
                OutputStream out = socket.getOutputStream();
                DataOutputStream dataOut = new DataOutputStream(out);
                String fileName = file.getName();
                // Send the file name to the server
                dataOut.writeUTF(fileName);

                // Send the size of the file to the server
                dataOut.writeLong(file.length());

                int count;
                while ((count = in.read(bytes)) > 0) {
                    out.write(bytes, 0, count);
                }
                in.close();
                out.close();
            }
        }

        socket.close();
    }
}

