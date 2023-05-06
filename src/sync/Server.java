package sync;

import java.net.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;

/**
 * Class for server side of the synchronization, it contains for receiving data
 * from the server and for sending data to the server.
 * At first we want the server to receive data from the client only, but because
 * we need the synchronization to work both ways, we also need to send data to
 * the client.
 */
public class Server {
    /*
     * We use a PrintWriter to send data to the client, and a BufferedReader to
     * receive data from the client, we declare the destinationFolder so we can use
     * it and modify it in the methods, and we declare a list of files, to know
     * which files to delete if needed
     */
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out; // output pour envoyer des données au clients sous forme de string
    private BufferedReader in; // input pour lire les données envoyées par le client
    String destinationFolder = "C:\\Users\\Peter\\Documents\\test_copy";
    private List<String> filesList = new ArrayList<>(); // liste de fichiers qui s'actualise pour savoir quels fichiers
                                                        // supprimer si besoin
    private boolean firstWrite = false;
    private boolean isRunning = true;

    /**
     * This method starts the server so it can receive data from the client, and
     * calls the methods for the synchronization. It contains a thread that receives
     * data from client, and a thread that checks if files have been added or
     * deleted, and sends data if needed
     * 
     * @param port the port of the server
     */
    public void startServer(Integer port) throws IOException, InterruptedException {
        System.out.println("Waiting for connection");
        serverSocket = new ServerSocket(port);
        clientSocket = serverSocket.accept();
        System.out.println("Conneceted");
        out = new PrintWriter(clientSocket.getOutputStream(), true); // envoi des données au client
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())); // lecture des données envoyées
                                                                                       // par le client
        Thread readThread = new Thread(new Runnable() { // sans nouveau thread, le programme bloque à while ((line =
                                                        // in.readLine()) et attends des données du client, on crée un
                                                        // nouveau thread qui s'occupe de la réception et lecture des
                                                        // données
            public void run() {
                try {
                    String data;
                    while ((data = in.readLine()) != null) { // on lit les données envoyées par le client
                        receiveFiles(data);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        readThread.start();
        while (isRunning) {
            while (!firstWrite) {
                Thread.sleep(1000);
                System.out.println("First write not done");
            }
            System.out.println("First write done");
            File file = new File(destinationFolder);
            int initialLenght = check(file);
            while (true) {
                int newLenght = check(file);
                if (newLenght == -1) {
                    System.out.println("Destination directory has been deleted, please restart");
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
    }

    /**
     * This method contains the logic to receive data from the server. It
     * deserializes the data and creates the files and folders. We deserialize the
     * string using the separator "||", and we create the files and folders. We
     * receive in order the type of the file (file or folder), the path of the file,
     * and the content of the file if it's a file.
     * 
     * @param data the data to deserialize
     */
    public void receiveFiles(String data) throws IOException {
        String[] separatedData = data.split("\\|\\|");
        if (data.equals("end")) {
            delete(new File(destinationFolder));
            filesList = new ArrayList<>(); // on met à jour la liste des fichiers à la fin de la réception des données
                                           // et copie des fichiers
            if (!firstWrite) { // booléen qui permet d'attendre que le serveur ait fini la 1ère copie avant de
                               // synchroniser
                firstWrite = true;
                System.out.println("First write done in receiveFiles");
            }
            return;
        }
        filesList.add(separatedData[1]); // on le chemin du fichier (chemin + nom) à la liste des fichiers
        if (separatedData[0].equals("1")) { // 1||path pour les dossiers
            File folder = new File(destinationFolder + File.separator + separatedData[1]);
            if (!folder.exists()) {
                folder.mkdir();
            }
        } else if (separatedData[0].equals("0")) { // 0||path||base64 pour les fichiers
            File file = new File(destinationFolder + File.separator + separatedData[1]);
            if (separatedData.length == 2) { // 0||path si le fichier est vide, on le crée sans écrire dedans
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

    /**
     * This method is used to stop the server, useful for the GUI
     * 
     * @throws IOException
     */
    public void stopServer() throws IOException {
        isRunning = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
            if (clientSocket != null) {
                clientSocket.close();
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
        System.out.println("Server stopped");
    }

    /**
     * This method is used to check the number of files and folders in the
     * destination folder, it is used to know if files have been added or deleted
     * 
     * @param directory the directory to check
     * @return the number of files and folders in the directory
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
     * This method is used to delete files and folders that are not in the list of
     * files sent by the server
     * 
     * @param file the file to delete
     */
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

    /**
     * This method is used to delete a folder and all its content
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

    /**
     * This method contains the logic to send data to the server. It serializes the
     * files and sends them to the server, we turn the bytes of data into a base64
     * string, and send it to the server
     * 
     * @param file the file to send to the server
     */
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
        if ((file.getPath() + File.separator).equals(this.destinationFolder)
                || file.getPath().equals(this.destinationFolder)) {
            out.println("end");
            out.flush();
        }
    }
}