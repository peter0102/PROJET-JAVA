package sync;

import java.net.*;
import java.io.*;

public class Server {
    public static void main(String[] args) throws IOException {
        try {
            ServerSocket serverSocket = new ServerSocket(8000); // create server socket
            Socket clientSocket = serverSocket.accept(); // wait for client to connect
            System.out.println("Connection established!"); // print message to console
            DataInputStream dataInputStream = new DataInputStream(clientSocket.getInputStream());
            String sourceFolderPath = dataInputStream.readUTF();
            String destinationFolderPath = dataInputStream.readUTF();
            File destDir = new File(destinationFolderPath);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            File sourceDir = new File(sourceFolderPath);
            copy(sourceDir, destDir,0,0);

            DataOutputStream dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());
            dataOutputStream.writeUTF("Folder copied successfully!");

        }
        catch (IOException f) {
            System.out.println("Error: " + f);
        }
    }
    public static void copy(File source, File destination, int i, int lvl) throws IOException {
		File[] sourceDir = source.listFiles();
		if (i == sourceDir.length) {
			return;
		}
		if (sourceDir[i].isFile()) {
			FileInputStream inputStream = new FileInputStream(sourceDir[i]);
			FileOutputStream outputStream = new FileOutputStream(new File(destination.getPath() + "\\" + sourceDir[i].getName()));
			byte[] buffer = new byte[1024];
			int length;
			while ((length = inputStream.read(buffer)) > 0) {
				outputStream.write(buffer, 0, length);
			}
			inputStream.close();
			outputStream.close();
		}
		if (sourceDir[i].isDirectory()) {
			File newDir = new File(destination.getPath() + "\\" + sourceDir[i].getName());
			if (!newDir.exists()) {
				newDir.mkdir();
			}
			copy(sourceDir[i], newDir, 0, lvl+1);
		}
		copy(source,destination, i + 1, lvl);
	}
}