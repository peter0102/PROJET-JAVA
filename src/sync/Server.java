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
			String dirPath ="C:\\Users\\LINPa\\Documents\\";

			ServerSocket serverSocket = new ServerSocket(8000);
			Socket socket = serverSocket.accept();
			
			BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
			DataInputStream dis = new DataInputStream(bis);
			
			int filesCount = dis.readInt();
			File[] files = new File[filesCount];
			
			for(int i = 0; i < filesCount; i++)
			{
				long fileLength = dis.readLong();
				String fileName = dis.readUTF();
			
				files[i] = new File(dirPath + "\\" + fileName);
			
				FileOutputStream fos = new FileOutputStream(files[i]);
				BufferedOutputStream bos = new BufferedOutputStream(fos);
			
				for(int j = 0; j < fileLength; j++) bos.write(bis.read());
			
				bos.close();
			}
			
			dis.close();
			socket.close();
            serverSocket.close();
        }
    }
}
