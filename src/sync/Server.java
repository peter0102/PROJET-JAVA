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
	
				// Open a file output stream with the received file name
				out = new FileOutputStream("C:\\Users\\LINPa\\Documents\\" + fileName);
			} catch (FileNotFoundException ex) {
				System.out.println("File not found. ");
			}
	
			byte[] bytes = new byte[1024];
	
			int count;
			while ((count = in.read(bytes)) > 0) {
				out.write(bytes, 0, count);
			}
	
			out.close();
			in.close();
			socket.close();
			serverSocket.close();
		}
    }
}
