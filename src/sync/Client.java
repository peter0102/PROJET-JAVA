package sync;

import java.net.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;

/**
 * Class for client side of the synchronization, it contains the logic for
 * sending data to the server, and for receiving data from the server.
 * At first we want the client to send data to the server only, but because we
 * need the synchronization to work both ways, we also need to receive data from
 * the server.
 */
public class Client {
    /*
     * We use a PrintWriter to send data to the server, and a BufferedReader to
     * receive data from the server, we declare the sourceFolder so we can use it
     * and modify it in the methods, and we declare a list of files, to know which
     * files to delete if needed
     */
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    String sourceFolder = "C:\\Users\\Peter\\Documents\\test";
    private List<String> filesList = new ArrayList<>();
    private boolean firstSend = false;

    /**
     * This method starts the connection with the server, and calls the methods for
     * the synchronization. It contains a thread that receives data from server, and
     * a thread that checks if files have been added or deleted, and sends data if
     * needed
     * 
     * @param host the host of the server
     * @param port the port of the server
     */
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
            if (newLenght == -1) {
                System.out.println("Client directory has been deleted, please restart");
                return;
            }
            if (initialLenght != newLenght) {
                System.out.println("Updating files");
                send(file);
                initialLenght = newLenght;
                System.out.println("Files all sent");
            }
            Thread.sleep(2000);
        }
    }

    /**
     * This method stops the connection with the server, and closes the streams,
     * helpful for the GUI
     */
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

    /**
     * This method contains the logic to send data to the server. It serializes the
     * files and sends them to the server, we turn the bytes of data into a base64
     * string, and send it to the server
     * 
     * @param file the file to send to the server
     */
    public void send(File file) { // serialisation des fichiers et envoi au serveur
        File[] files = file.listFiles();
        for (File sourceFile : files) {
            String buffer = ""; // on crée un buffer qui va contenir un string avec toutes les données
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
                        buffer += Base64.getEncoder().encodeToString(bytes); // on code les bytes en string en passant
                                                                             // par la base64, pour copier toutes les
                                                                             // extensions
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                out.println(buffer);
                out.flush();
            }
        }
        if ((file.getPath() + File.separator).equals(this.sourceFolder) || file.getPath().equals(this.sourceFolder)) {
            out.println("end"); //fin de l'envoi, pour que le serveur sache quand s'arrêter de lire
            out.flush();
        }
        firstSend = true;
    }

    /**
     * This method checks the number of files in a directory, and in its
     * subdirectories, and returns the number of files, useful to know if
     * synchronization is needed
     * 
     * @param directory the directory to check
     * @return
     */
    public int check(File directory) {
        int lenght = 0;
        File[] files = directory.listFiles();
        if (files == null) {
            return -1;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                lenght += check(file) + 1;
            } else {
                lenght++;
            }
        }
        return lenght;
    }

    /**
     * This method contains the logic to receive data from the server. It
     * deserializes the data and creates the files and folders. We deserialize the
     * string using the separator "||", and we create the files and folders
     * 
     * @param data the data to deserialize
     */
    public void receiveFiles(String data) throws IOException { // désérialisation des fichiers et création des
                                                               // dossiers/fichierss
        String[] separatedData = data.split("\\|\\|");
        if (data.equals("end")) {
            delete(new File(sourceFolder));
            filesList = new ArrayList<>();
            System.out.println("Files all written");
            return;
        }
        filesList.add(separatedData[1]); //on ajoute le fichier à la liste de fichiers, pour la synchronisation
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
                    fileOutputStream.write(Base64.getDecoder().decode(separatedData[2])); //on décode le string
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * This method is used to delete files and folders that are not in the list of
     * files sent by the server
     * 
     * @param file the file to delete
     */
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
            if (f.isDirectory()) { //recursivité pour les sous-dossiers
                delete(f);
            }
        }
    }

    /**
     * This method is used to delete a folder and its content
     * 
     * @param folder the folder to delete
     */
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
