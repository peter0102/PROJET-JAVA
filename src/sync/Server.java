package sync;

import java.net.*;
import java.io.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        while (true) {
            ServerSocket serverSocket = null;

            try {
                serverSocket = new ServerSocket(8000);
            } catch (IOException ex) {
                System.out.println("Can't setup server on this port number. ");
            }

            Socket socket = null;
            InputStream in = null;
            OutputStream out = null;

            try {
                socket = serverSocket.accept();
            } catch (IOException ex) {
                System.out.println("Can't accept client connection. ");
            }

            try {
                in = socket.getInputStream();
            } catch (IOException ex) {
                System.out.println("Can't get socket input stream. ");
            }

            try {
                // Read the file name from the input stream
                DataInputStream dataIn = new DataInputStream(in);
                String fileName = dataIn.readUTF();

                // Read the file size from the input stream
                long fileSize = dataIn.readLong();

                // Open a file output stream with the received file name
                out = new FileOutputStream("C:\\Users\\LINPa\\Documents\\" + fileName);

                byte[] bytes = new byte[1024];
                int count;
                long totalBytesRead = 0;

                // Read the file content until the total number of bytes read matches the file size
                while (totalBytesRead < fileSize && (count = in.read(bytes)) > 0) {
                    out.write(bytes, 0, count);
                    totalBytesRead += count;
                }

                out.close();
            } catch (FileNotFoundException ex) {
                System.out.println("File not found. ");
            }

            in.close();
            socket.close();
            serverSocket.close();
        }
    }
}
