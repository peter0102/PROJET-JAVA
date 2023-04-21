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
        File files = new File(directory);
        Socket socket = new Socket("192.168.1.100", 8000);
        sendFiles(files, socket);
    }

    public static void sendFiles(File file, Socket socket) throws IOException {
        BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
        DataOutputStream dos = new DataOutputStream(bos);
        File[] files = file.listFiles();
        dos.writeInt(files.length);
    
        for (File sourceFile : files) {
            if (sourceFile.isFile()) {
                long length = sourceFile.length();
                dos.writeLong(length);
        
                String name = sourceFile.getName();
                dos.writeUTF(name);
        
                FileInputStream fis = new FileInputStream(sourceFile);
                BufferedInputStream bis = new BufferedInputStream(fis);
        
                int theByte = 0;
                while ((theByte = bis.read()) != -1)
                    bos.write(theByte);
        
                bis.close();
            }
            if (sourceFile.isDirectory()) {
                long length = sourceFile.length();
                dos.writeLong(length);
        
                String name = sourceFile.getName();
                dos.writeUTF(name);
                
                sendFiles(sourceFile, socket);
            }
        }
    
        dos.close();
    }
    
    
}
