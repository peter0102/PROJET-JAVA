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
            if (sourceFile.isDirectory()) {
                dos.writeLong(0L); // indicate that this is a directory
                dos.writeUTF(sourceFile.getName()); // send the name of the directory
    
                sendFiles(sourceFile, socket); // recursively send files in the directory
            } else {
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
        }
    
        dos.flush(); // flush the output stream to ensure all data has been sent
        socket.shutdownOutput(); // signal that we have finished sending data
    
        // wait for the server to acknowledge receipt of all data
        InputStream inputStream = socket.getInputStream();
        int acknowledgement = inputStream.read();
        if (acknowledgement == 1) {
            System.out.println("All files sent successfully");
        } else {
            System.out.println("Error sending files");
        }
    
        dos.close();
        socket.close();
    }
    
    
    
    
}
