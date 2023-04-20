package sync;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    public static void main(String[] args) throws IOException {
        String dirPath = "C:\\Users\\LINPa\\Documents\\";

        ServerSocket serverSocket = new ServerSocket(8000);

        while (true) {
            Socket socket = serverSocket.accept();
            BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
            DataInputStream dis = new DataInputStream(bis);

            int filesCount = dis.readInt();
            File[] files = new File[filesCount];

            for (int i = 0; i < filesCount; i++) {
                long fileLength = dis.readLong();
                String fileName = dis.readUTF();

                File file = new File(dirPath + "\\" + fileName);

                // Create any required subdirectories
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }

                // Check if the file already exists
                if (file.exists()) {
                    // Handle the file already exists case as needed
                    // For example, you could skip the file or overwrite it
                    System.out.println("File already exists: " + file.getAbsolutePath());
                } else {
                    // Create the file and write the contents
                    FileOutputStream fos = new FileOutputStream(file);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);

                    for (int j = 0; j < fileLength; j++) {
                        bos.write(bis.read());
                    }

                    bos.close();
                    System.out.println("File created: " + file.getAbsolutePath());
                }
            }

            dis.close();
            socket.close();
        }
    }
}
