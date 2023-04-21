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
			File[] files = new File[filesCount];
	
			for (int i = 0; i < filesCount; i++) {
				long fileLength = dis.readLong();
				String fileName = dis.readUTF();
	
				if (fileLength == 0) { // directory
					File dir = new File(dirPath + "\\" + fileName);
					dir.mkdir();
					receiveFiles(socket, dirPath + "\\" + fileName);
				} else { // file
					files[i] = new File(dirPath + "\\" + fileName);
	
					try (FileOutputStream fos = new FileOutputStream(files[i]);
						 BufferedOutputStream bos = new BufferedOutputStream(fos)) {
	
						byte[] buffer = new byte[(int) fileLength];
						int bytesRead = 0;
						while (bytesRead < fileLength) {
							int n = bis.read(buffer, bytesRead, (int) fileLength - bytesRead);
							if (n < 0) {
								throw new IOException("Unexpected end of stream");
							}
							bytesRead += n;
						}
						bos.write(buffer);
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
