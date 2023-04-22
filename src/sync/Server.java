package sync;

import java.io.*;
import java.net.*;

public class Server {
    public static void main(String[] args) throws Exception {
        ServerSocket serverSocket = new ServerSocket(1234);
        System.out.println("Server listening on port 1234");
        while (true) {
            Socket socket = serverSocket.accept();
            InputStream inputStream = socket.getInputStream();
            ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
            try {
                File folder = (File) objectInputStream.readObject();
                createFolder(folder, "C:\\Users\\Peter\\Documents\\test_copy");
            } catch (EOFException e) {
                System.err.println("Connection closed by client");
            } finally {
                objectInputStream.close();
                inputStream.close();
                socket.close();
            }
        }
    }

    public static void createFolder(File folder, String outputDirectoryPath) {
        if (folder.isDirectory()) {
            String folderName = folder.getName();
            String folderPath = outputDirectoryPath + File.separator + folderName;
            File newFolder = new File(folderPath);
            if (!newFolder.exists()) {
                newFolder.mkdir();
            }
            File[] files = folder.listFiles();
            for (File file : files) {
                createFolder(file, folderPath);
            }
        } else {
            String fileName = folder.getName();
            String filePath = outputDirectoryPath + File.separator + fileName;
            File newFile = new File(filePath);
            if (!newFile.exists()) {
                try {
                    newFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try (InputStream fileInputStream = new FileInputStream(folder);
                     OutputStream fileOutputStream = new FileOutputStream(newFile)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fileInputStream.read(buffer)) > 0) {
                        fileOutputStream.write(buffer, 0, length);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
