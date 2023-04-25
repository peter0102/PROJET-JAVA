package sync;

import java.net.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;

public class Client {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    String sourceFolder = "C:\\Users\\Peter\\Documents\\test";
    private List<String> filesList = new ArrayList<>();
    private boolean firstSend = false;

    public void startConnection(String host, int port) throws IOException, InterruptedException {
        socket = new Socket(host, port);
        System.out.println("Connected");
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        File file = new File(sourceFolder);
        int initialLenght = check(file);
        send(file);
        while (!firstSend) {
            Thread.sleep(1000);
            System.out.println("First send not done");
        }
        System.out.println("First send done");
        Thread readThread = new Thread(new Runnable() { // thread qui reçoit les données du serveur
            public void run() {
                try {
                    String data;
                    while ((data = in.readLine()) != null) {
                        receiveFiles(data);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        readThread.start();
        while (true) { // partie qui vérifie si des fichiers ont été ajoutés ou supprimés et les envoie
                                 // au serveur
            int newLenght = check(file);
            if (initialLenght != newLenght) {
                System.out.println("Updating files");
                send(file);
                initialLenght = newLenght;
                System.out.println("Files all sent");
            }
            Thread.sleep(2000);
        }
    }

    public void stopConnection() throws IOException {
        try {
            if (socket != null) {
                socket.close();
            }
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(File file) { // serialisation des fichiers et envoi au serveur
        File[] files = file.listFiles();
        for (File sourceFile : files) {
            String buffer = "";
            if (sourceFile.isDirectory()) {
                buffer += "1||"; // 1 pour les dossiers; || pour séparer les données
                buffer += sourceFile.getPath().substring(this.sourceFolder.length());
                out.println(buffer); // envoi des données au serveur
                out.flush(); // pour bien TOUT envoyer au serveur
                send(sourceFile);
            }
            if (sourceFile.isFile()) {
                buffer += "0||"; // 0 pour les fichiers
                buffer += sourceFile.getPath().substring(this.sourceFolder.length()) + "||"; // on récupère le chemin
                                                                                             // relatif seulement pour
                                                                                             // les sous-dossiers
                try {
                    byte[] bytes = Files.readAllBytes(sourceFile.toPath());
                    if (bytes == null) {
                        buffer += ""; // si le fichier est vide, on envoie une chaîne vide
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
        firstSend = true;
    }

    public int check(File directory) {
        int lenght = 0;
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                lenght += check(file) + 1;
            } else {
                lenght++;
            }
        }
        return lenght;
    }

    public void receiveFiles(String data) throws IOException { // désérialisation des fichiers et création des
                                                               // dossiers/fichierss
        String[] separatedData = data.split("\\|\\|");
        if (data.equals("end")) {
            delete(new File(sourceFolder));
            filesList = new ArrayList<>();
            System.out.println("Files all written");
            return;
        }
        filesList.add(separatedData[1]);
        if (separatedData[0].equals("1")) { // 1||path pour les dossiers
            File folder = new File(sourceFolder + File.separator + separatedData[1]);
            if (!folder.exists()) {
                folder.mkdir();
            }
        } else if (separatedData[0].equals("0")) { // 0||path||base64 pour les fichiers
            File file = new File(sourceFolder + File.separator + separatedData[1]);
            if (separatedData.length == 2) { // 0||path|| si le fichier est vide, on le crée sans écrire dedans
                file.createNewFile();
            } else {
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    fileOutputStream.write(Base64.getDecoder().decode(separatedData[2]));
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void delete(File file) {
        File[] allFiles = file.listFiles();
        for (File f : allFiles) {
            if (!filesList.contains(f.getPath().substring(sourceFolder.length()))) { // on récupère le chemin relatif
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
