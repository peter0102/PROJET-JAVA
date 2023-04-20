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
        
        File file = new File("C:\\Users\\Peter\\Documents\\test\\dofus-2022-06-29_19-09-08-Photofiiltre.png");
        // Get the size of the file
        long length = file.length();
        byte[] bytes = new byte[1024];
        InputStream in = new FileInputStream(file);
        OutputStream out = socket.getOutputStream();
        DataOutputStream dataOut = new DataOutputStream(out);
        String fileName = file.getName();
        // Send the file name to the server
        dataOut.writeUTF(fileName);
        
        int count;
        while ((count = in.read(bytes)) > 0) {
            out.write(bytes, 0, count);
        }

        out.close();
        in.close();
        socket.close();
    }
}

