package sync;

import java.net.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;

public class Client {
    Socket socket;
    PrintWriter out;
    BufferedReader in;
    String sourceFolder = "C:\\Users\\Peter\\Documents\\test";
    List<String> filesList = new ArrayList<>();

    public static void main(String[] args) throws IOException, InterruptedException {
        Client client = new Client();
        client.startConnection("localhost", 8000);
        client.stopConnection();
    }

    public void startConnection(String host, int port) throws IOException, InterruptedException {
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        File file = new File(sourceFolder);
        int initialLenght = check(file);
        send(file);
        
        // Start a new thread to read data from the server
        Thread readThread = new Thread(new Runnable() {
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
        
        while (true) {
            int newLenght = check(file);
            if (initialLenght!=newLenght) {
                send(file);
                initialLenght=newLenght;
                System.out.println("Files updated");
            }
            Thread.sleep(2000);
        }
    }
    

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        socket.close();
    }

    public void send(File file) {
        File[] files = file.listFiles();
        for (File sourceFile : files) {
            String buffer = "";
            if (sourceFile.isDirectory()) {
                buffer += "1||";
                buffer += sourceFile.getPath().substring(this.sourceFolder.length());
                out.println(buffer);
                out.flush();
                send(sourceFile);
            }
            if (sourceFile.isFile()) {
                buffer += "0||";
                buffer += sourceFile.getPath().substring(this.sourceFolder.length()) + "||";
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
        if ((file.getPath() + File.separator).equals(this.sourceFolder) || file.getPath().equals(this.sourceFolder)) {
            out.println("end");
            out.flush();
        }
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
    public void receiveFiles(String data) throws IOException {
        String[] separatedData = data.split("\\|\\|");
        if (data.equals("end")) {
            delete(new File(sourceFolder));
            filesList= new ArrayList<>();
            return;
        }
        filesList.add(separatedData[1]);
        if (separatedData[0].equals("1")) { // 1||path pour les dossiers
            File folder = new File(sourceFolder+File.separator+separatedData[1]);
            if (!folder.exists()) {
                folder.mkdir();
            }
        }
        else if (separatedData[0].equals("0")) { // 0||path||base64 pour les fichiers
            File file = new File(sourceFolder+File.separator+separatedData[1]);
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
    public void delete(File file) {
        File[] allFiles = file.listFiles();
        for (File f : allFiles) {
            if (!filesList.contains(f.getPath().substring(sourceFolder.length()))) { //on récupère le chemin relatif
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
}
