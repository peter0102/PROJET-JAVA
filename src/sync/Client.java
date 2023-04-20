package sync;

import java.io.*;
import java.net.*;

public class Client {
    private static Socket socket;
    private static BufferedOutputStream bos;
    private static DataOutputStream dos;
    private static String rootDir;

    public static void main(String[] args) throws IOException {
        rootDir = "C:\\Users\\Peter\\Documents\\test";
        socket = new Socket("192.168.1.100", 8000);
        bos = new BufferedOutputStream(socket.getOutputStream());
        dos = new DataOutputStream(bos);

        sendFiles(new File(rootDir));

        dos.close();
    }

    public static void sendFiles(File file) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            dos.writeUTF(file.getAbsolutePath());
            dos.writeInt(files.length);
            for (File f : files) {
                sendFiles(f);
            }
        } else {
            dos.writeUTF(file.getAbsolutePath().replace(rootDir, ""));
            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);

            byte[] buffer = new byte[8192];
            int count;
            while ((count = bis.read(buffer)) > 0) {
                bos.write(buffer, 0, count);
            }

            bis.close();
        }
    }
}
