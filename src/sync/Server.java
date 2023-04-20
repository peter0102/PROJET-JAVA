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
        ServerSocket serverSocket = new ServerSocket(8000);
        System.out.println("Server started and listening on port 8000...");

        while (true) {
            Socket socket = serverSocket.accept();
            System.out.println("New client connected: " + socket.getInetAddress().getHostAddress());
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
			while (true) {
				String fileName = dataInputStream.readUTF();
	
				// Break the loop if the client signals that all files have been sent
				if (fileName.equals("done")) {
					System.out.println("All files received from client");
					break;
				}
	
				// Create output stream to write file to destination folder
				FileOutputStream fileOutputStream = new FileOutputStream("C:\\Users\\LINPa\\Documents\\" + fileName);
	
				// Read file data from client and write it to output stream
				byte[] buffer = new byte[1024];
				int length;
				while ((length = dataInputStream.read(buffer)) > 0) {
					fileOutputStream.write(buffer, 0, length);
				}
				fileOutputStream.close();
				System.out.println("Received file: " + fileName);
			}
	
			// Send response to client
			dataInputStream.close();
			socket.close();
			serverSocket.close();
			System.out.println("Transfer completed successfully");
		}
		
    }

    public static void copyFolder(File source, File destination) throws IOException {
        if (source.isDirectory()) {
            if (!destination.exists()) {
                destination.mkdir();
            }

            String[] files = source.list();
            for (String file : files) {
                File srcFile = new File(source, file);
                File destFile = new File(destination, file);
                copyFolder(srcFile, destFile);
            }
        } else {
            FileInputStream inputStream = new FileInputStream(source);
            FileOutputStream outputStream = new FileOutputStream(destination);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            inputStream.close();
            outputStream.close();
        }
    }
}
