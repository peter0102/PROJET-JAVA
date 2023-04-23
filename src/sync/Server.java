package sync;
import java.io.*;
import java.net.*;

public class Server {

    private String dirPath;
    private ServerSocket serverSocket;

    public Server(String dirPath, int port) throws IOException {
        this.dirPath = dirPath;
        this.serverSocket = new ServerSocket(port);
    }
	public static void main(String[] args) throws IOException {
		String dirPath = "C:\\Users\\LINPa\\Documents\\test_copy";
		int serverPort = 12345;
	
		Server server = new Server(dirPath, serverPort);
		server.receiveFiles();
	}
	
    public void receiveFiles() throws IOException {
        Socket socket = serverSocket.accept();

        BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
        DataInputStream dis = new DataInputStream(bis);

        int filesCount = dis.readInt();
        File[] files = new File[filesCount];

        for (int i = 0; i < filesCount; i++) {
            long fileLength = dis.readLong();
            String fileName = dis.readUTF();

            files[i] = new File(dirPath + "/" + fileName);

            FileOutputStream fos = new FileOutputStream(files[i]);
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            for (int j = 0; j < fileLength; j++) {
                bos.write(bis.read());
            }

            bos.close();
        }

        dis.close();
    }

    public void close() throws IOException {
        serverSocket.close();
    }
}
