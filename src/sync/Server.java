package sync;

import java.net.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;

public class Server {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private String destinationFolder = "C:\\Users\\Peter\\Documents\\test_copy";
    private List<String> filesList = new ArrayList<>();

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = new Server();
        server.startServer(8000);
    }

    public void startServer(Integer port) throws IOException, InterruptedException {
        serverSocket = new ServerSocket(port);
        clientSocket = serverSocket.accept();
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        Thread readThread = new Thread(new Runnable() { // sans nouveau thread, le programme bloque à while ((line = in.readLine()) et attends des données du client
            public void run() {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        receiveFiles(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        readThread.start();
        File file = new File(destinationFolder);
        int initialLenght = check(file);
        send(file);
        while (true) {
            int newLenght = check(file);
            if (initialLenght != newLenght) {
                send(file);
                initialLenght = newLenght;
                System.out.println("Files updated");
            }
            Thread.sleep(2000);
        }
    }
    public void receiveFiles(String data) throws IOException {
        String[] separatedData = data.split("\\|\\|");
        if (data.equals("end")) {
            delete(new File(destinationFolder));
            filesList= new ArrayList<>();
            return;
        }
        filesList.add(separatedData[1]);
        if (separatedData[0].equals("1")) { // 1||path pour les dossiers
            File folder = new File(destinationFolder+File.separator+separatedData[1]);
            if (!folder.exists()) {
                folder.mkdir();
            }
        }
        else if (separatedData[0].equals("0")) { // 0||path||base64 pour les fichiers
            File file = new File(destinationFolder+File.separator+separatedData[1]);
            if (separatedData.length==2){ // 0||path si le fichier est vide, on le crée sans écrire dedans
                file.createNewFile();
            }
            else {
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(Base64.getDecoder().decode(separatedData[2]));
                    fileOutputStream.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void stopServer() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
    }
    public int check(File directory) {
        int lenght =0;
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                lenght+=check(file)+1;
            }
            else {
                lenght++;
            }
        }
        return lenght;
    }
    public void delete(File file) {
        File[] allFiles = file.listFiles();
        for (File f : allFiles) {
            if (!filesList.contains(f.getPath().substring(destinationFolder.length()))) {
                if (f.isDirectory()) {
                    deleteFolder(f);
                }
                if (f.isFile()) {
                    f.delete();
                }
            }
            if (f.isDirectory()) {
                delete(f);
            }
        }
    }
    public void deleteFolder(File folder) {
        File[] allFiles = folder.listFiles();
        for (File f : allFiles) {
            if (f.isDirectory()) {
                deleteFolder(f);
            }
            if (f.isFile()) {
                f.delete();
            }
        }
        folder.delete();
    }
    public void send(File file) {
        File[] files = file.listFiles();
        for (File sourceFile : files) {
            String buffer = "";
            if (sourceFile.isDirectory()) {
                buffer += "1||";
                buffer += sourceFile.getPath().substring(this.destinationFolder.length());
                out.println(buffer);
                out.flush();
                send(sourceFile);
            }
            if (sourceFile.isFile()) {
                buffer += "0||";
                buffer += sourceFile.getPath().substring(this.destinationFolder.length()) + "||";
                try {
                    byte[] bytes = Files.readAllBytes(sourceFile.toPath());
                    if (bytes == null) {
                        buffer += "";
                    } else {
                        buffer += Base64.getEncoder().encodeToString(bytes);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                out.println(buffer);
                out.flush();
            }
        }
        if ((file.getPath() + File.separator).equals(this.destinationFolder) || file.getPath().equals(this.destinationFolder)) {
            out.println("end");
            out.flush();
        }
    }
}
