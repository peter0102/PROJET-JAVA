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
    
	private static void receiveFiles(Socket socket, String dirPath) throws IOException {
		try (BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
			 DataInputStream dis = new DataInputStream(bis)) {
	
			int filesCount = dis.readInt();
	
			for (int i = 0; i < filesCount; i++) {
				long fileLength = dis.readLong();
				String fileName = dis.readUTF();
	
				File file = new File(dirPath + "\\" + fileName);
				if (file.isDirectory()) {
					file.mkdirs(); // create the subdirectory
					receiveFiles(socket, dirPath + "\\" + fileName); // recursively receive files in the subdirectory
				} else {
					File parentDir = file.getParentFile();
					if (!parentDir.exists()) {
						parentDir.mkdirs(); // create parent directory if it does not exist
					}
	
					try (FileOutputStream fos = new FileOutputStream(file);
						 BufferedOutputStream bos = new BufferedOutputStream(fos)) {
	
						for (int j = 0; j < fileLength; j++) {
							bos.write(bis.read());
						}
					}
				}
			}
		}
	}
	
    public static void main(String[] args) throws IOException {
        String dirPath = "C:\\Users\\LINPa\\Documents\\";
        try (ServerSocket serverSocket = new ServerSocket(8000)) {
            while (true) {
                try (Socket socket = serverSocket.accept()) {
                    receiveFiles(socket, dirPath);
                }
            }
        }
    }
}
