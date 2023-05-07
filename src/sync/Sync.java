package sync;

import java.io.*;
import java.nio.file.*;

/**
 * Class for synchronizing two folders on a single computer, it implements
 * Runnable so it can be used in a thread
 * 
 * @param sourceFolder      the folder to be copied
 * @param destinationFolder the folder to be copied to
 * @param isActive          boolean to know if the synchronization is active
 *                          (helpful for the GUI)
 * 
 */
public class Sync implements Runnable {
    private String sourceFolder;
    private String destinationFolder;
    private static boolean isActive;

    public Sync(String sourceFolder, String destinationFolder) {
        this.sourceFolder = sourceFolder;
        this.destinationFolder = destinationFolder;
        this.isActive = true;
    }

    /**
     * This method is called to copy the files from the source folder to the
     * destination folder
     * It is recursive, lvl is used to know how deep we are in the folder structure
     * and i is used to know which file we are currently copying
     * 
     * @param sourceFolder      the folder to be copied
     * @param i                 the index of the file we are currently copying
     * @param lvl               the level of the folder structure we are currently
     *                          in
     * @param destinationFolder the folder to be copied to
     */
    public void copyFiles(File[] sourceFolder, int i, int lvl, File destinationFolder) throws FileNotFoundException {
        if (i == sourceFolder.length) {
            return;
        }
        if (sourceFolder[i].isFile()) {
            try {
                Path sourcePath = Paths.get(sourceFolder[i].getPath());
                Path destinationPath = Paths.get(destinationFolder.getPath() + File.separator + sourceFolder[i].getName());
                Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
                File destFile = destinationPath.toFile(); // on change la date de modification du dossier destination pour que ça fonctionne sur linux
                destFile.setLastModified(sourceFolder[i].lastModified());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (sourceFolder[i].isDirectory()) {
            File newDir = new File(destinationFolder.getPath() + File.separator + sourceFolder[i].getName());
            if (!newDir.exists()) {
                newDir.mkdir();
            }
            newDir.setLastModified(sourceFolder[i].lastModified()); // on change la date de modification du dossier destination pour que ça fonctionne sur linux
            copyFiles(sourceFolder[i].listFiles(), 0, lvl + 1, newDir);
        }
        copyFiles(sourceFolder, i + 1, lvl, destinationFolder);
    }
    

    /**
     * This method is called to delete the files from the destination folder that
     * are not in the source folder
     * 
     * @param a the source folder
     * @param b the destination folder
     */
    public void delete(File[] a, File[] b) { // supprime dans b ce qu'il n'y a pas dans a
        if (a == null || b == null) {
            return;
        }
        for (File fileb : b) {
            Boolean found = false;
            for (File filea : a) {
                if (filea.getName().equals(fileb.getName())) {
                    found = true;
                    if (filea.isDirectory() && fileb.isDirectory()) {
                        delete(filea.listFiles(), fileb.listFiles());
                    }
                }
            }
            if (!found) {
                if (fileb.isDirectory()) {
                    File[] filesToDelete = fileb.listFiles();
                    for (File file : filesToDelete) {
                        file.delete();
                    }
                    fileb.delete();
                } else {
                    fileb.delete();
                }
            }
        }
    }

    /**
     * This method is called to check if the source folder has been modified since
     * the last synchronization, and return an int so we know what to do
     * 
     * @param x the source folder
     * @param y the destination folder
     * @return an int that will be used to know what to do
     */
    public int check(File x, File y) {
        File[] a = x.listFiles();
        File[] b = y.listFiles();
        if (a == null && b == null || a.length == 0 && b.length == 0) {
            return 0;
        }
        if (a == null || a.length == 0) {
            if (b[0].getParentFile().lastModified() > x.lastModified()) {
                return 2; // addition de fichier dans b, on copie b dans a
            } else {
                return -2; // supression de fichier dans a, on supprime dans b
            }
        }
        if (b == null || b.length == 0) {
            if (a[0].getParentFile().lastModified() > y.lastModified()) {
                return 1; // addition de fichier dans a, on copie a dans b
            } else {
                return -1; // supression de fichier dans b, on supprime dans a
            }
        }
        if (a.length > b.length) { // addition dans a ou supression dans b, on compare date modification dossier a
                                   // et b
            if (a[0].getParentFile().lastModified() > b[0].getParentFile().lastModified()) {
                return 1; // addition de fichier dans a, on copie a dans b
            } else {
                return -1; // supression de fichier dans b, on supprime dans a
            }
        }
        if (b.length > a.length) { // addition dans b ou supression dans a, on compare date modification dossier a
                                   // et b
            if (b[0].getParentFile().lastModified() > a[0].getParentFile().lastModified()) {
                return 2; // addition de fichier dans b, on copie b dans a
            } else {
                return -2; // supression de fichier dans a, on supprime dans b
            }
        }
        if (a.length == b.length) { // modification fichier ou dans sous dossiers
            int result = 0;
            for (int i = 0; i < a.length; i++) {
                if (a[i].isFile() && b[i].isFile()) {
                    if (a[i].lastModified() != b[i].lastModified()) {
                        if (a[i].lastModified() > b[i].lastModified()) {
                            return 3; // a est plus récent, on copie a dans b
                        } else {
                            return -3; // b est plus récent, on copie b dans a
                        }
                    }
                } else if (a[i].isDirectory() && b[i].isDirectory()) {
                    int subResult = check(a[i], b[i]);
                    result += subResult;
                }
            }
            return result;
        }
        return 0;
    }

    /**
     * This method is used to stop the sync, it is called when the user clicks on
     * the stop button
     */
    public static void stopSync() { // méthode pour interrompre la boucle
        isActive = false;
    }

    /**
     * This method is used to start the sync, it is called when the user clicks on
     * the start button
     * It creates an infinite loop that will periodically check the source folder
     * and the destination folder, and synchronize them if needed
     */
    public void run() {
        File dir = new File(sourceFolder);
        if (dir.exists() && dir.isDirectory()) {
            File[] a = dir.listFiles();
            Sync c = new Sync(sourceFolder, destinationFolder);
            File newDir = new File(destinationFolder);
            try { // 1ère copie du dossier source vers le dossier destination
                c.copyFiles(a, 0, 0, newDir);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            while (isActive) {
                File[] refreshedA = dir.listFiles();
                File[] refreshedB = newDir.listFiles();
                int result = check(dir, newDir);
                switch (result) {
                    case 1:
                        try {
                            c.copyFiles(refreshedA, 0, 0, newDir);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                    case -1:
                        delete(refreshedB, refreshedA);
                        break;
                    case 2:
                        try {
                            c.copyFiles(refreshedB, 0, 0, dir);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                    case -2:
                        delete(refreshedA, refreshedB);
                        break;
                    case 3:
                        try {
                            c.copyFiles(refreshedA, 0, 0, newDir);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                    case -3:
                        try {
                            c.copyFiles(refreshedB, 0, 0, dir);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                }

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!dir.exists()) {
                    System.out.println("Source folder deleted. Exiting loop.");
                    isActive = false;
                    break;
                }
                if (!newDir.exists()) {
                    System.out.println("Destination folder deleted. Exiting loop.");
                    isActive = false;
                    break;
                }
                if (!isActive) {
                    break;
                }
            }
        }
    }
}