package com.mycompany.pname;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class Pname {
    
    public static void main(String[] args) throws Exception {
        Socket clientSocket = null;
        OutputStream outputStream = null;
        ObjectOutputStream objectOutputStream = null;

        try {
            // Créer une nouvelle socket et connecter au serveur
            clientSocket = new Socket("adresse IP", "numéro de port");

            // Obtenir un flux de sortie vers le serveur
            outputStream = clientSocket.getOutputStream();

            // Créer un objet ObjectOutputStream pour écrire des objets vers le flux de sortie
            objectOutputStream = new ObjectOutputStream(outputStream);

            // Définir le chemin d'accès du dossier à envoyer
            String folderPath = "chemin d'accés";

            // Créer un objet File à partir du chemin d'accès spécifié
            File folder = new File(folderPath);

            // Appeler la méthode sendFolder pour envoyer le dossier au serveur
            sendFolder(folder, objectOutputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // Fermer tous les flux et sockets
            try {
                if (objectOutputStream != null) {
                    objectOutputStream.close();
                }   
                if (outputStream != null) {
                    outputStream.close();
                }
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void sendFolder(File folder, ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeObject(folder);
        File[] files = folder.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                sendFolder(file, objectOutputStream);
            } else {
                FileInputStream fileInputStream = new FileInputStream(file);
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) > 0) {
                    objectOutputStream.write(buffer, 0, bytesRead);
                }
                fileInputStream.close();
            }
        }
    }
}
