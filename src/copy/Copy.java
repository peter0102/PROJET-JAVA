package copy;
import java.io.*;
import java.nio.file.*;
import java.time.*;
import java.nio.file.attribute.BasicFileAttributes;
public class Copy implements Runnable {
    private String name;

    public Copy(String name) {
        this.name = name;
    }

    public void CC(File[] a, int i, int lvl, File dir) throws FileNotFoundException {
        if (i == a.length) {
            return;
        }
        if (a[i].isFile()) {
            try {
                Path source = Paths.get(a[i].getPath());
                Path destination = Paths.get(dir.getPath() + "\\" + a[i].getName());
                Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (a[i].isDirectory()) {
            File newDir = new File(dir.getPath() + "\\" + a[i].getName());
            if (!newDir.exists()) {
                newDir.mkdir();
            }
            CC(a[i].listFiles(), 0, lvl + 1, newDir);
        }
        CC(a, i + 1, lvl, dir);
    }
    public void delete(File[] a, File[] b) { //supprime dans b ce qu'il n'y a pas dans a
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

    public int check(File[] a, File[] b) {
        if (a == null || b == null) {
            return 0;
        }
        if (a.length == b.length) { // modification fichier
            for (int i=0; i<a.length; i++) {
                if (a[i].isFile() && b[i].isFile()) {
                    if (a[i].lastModified() != b[i].lastModified()) {
                        if (a[i].lastModified()>b[i].lastModified()) {
                            return 1; //a est plus récent, on copie a dans b
                        } else {
                            return -1; //b est plus récent, on copie b dans a
                        }
                    }
                else if (a[i].isDirectory() && b[i].isDirectory()) {
                    int result = check(a[i].listFiles(), b[i].listFiles());
                    return result;
                    }
                }
            }
        }
        if (a.length > b.length) { // on regarde dans a s'il y a du changement
            for (File filea : a) {
                Path filePath = filea.toPath();
                try {
                    BasicFileAttributes attrs = Files.readAttributes(filePath, BasicFileAttributes.class);
                    Instant lastAccessTime = attrs.lastAccessTime().toInstant();
                    LocalDateTime lastAccessDateTime = LocalDateTime.ofInstant(lastAccessTime, ZoneId.systemDefault());
                    if (lastAccessDateTime.isAfter(LocalDateTime.now().minusSeconds(1))) {
                        return 2; //addition de fichier dans a, on copie a dans b
                    }
                    else {
                        return -2; //supression de fichier dans b, on supprime dans a
                    }
                } 
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (b.length > a.length) { // on regarde dans b s'il y a du changement
            for (File fileb : b) {
                Path filePath = fileb.toPath();
                try {
                    BasicFileAttributes attrs = Files.readAttributes(filePath, BasicFileAttributes.class);
                    Instant lastAccessTime = attrs.lastAccessTime().toInstant();
                    LocalDateTime lastAccessDateTime = LocalDateTime.ofInstant(lastAccessTime, ZoneId.systemDefault());
                    if (lastAccessDateTime.isAfter(LocalDateTime.now().minusSeconds(1))) {
                        return 3; //addition de fichier dans b, on copie b dans a
                    }
                    else {
                        return -3; //supression de fichier dans a, on supprime dans b
                    }
                } 
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return 0;
    }
    

    public void run() {
        File dir = new File("C:\\Users\\LINPa\\Documents\\" + name);
        if (dir.exists() && dir.isDirectory()) {
            File[] a = dir.listFiles();
            Copy c = new Copy(name);
            File newDir = new File("C:\\Users\\LINPa\\Documents\\" + dir.getName() + "_copy");
            if (!newDir.exists()) {
                newDir.mkdir();
            }
            try { //1ère copie du dossier source vers le dossier destination
                c.CC(a, 0, 0, newDir);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            while (true) {
                File[] refreshedA = dir.listFiles();
                File[] refreshedB = newDir.listFiles();
                int result = check(refreshedA, refreshedB);
                switch(result) {
                    case 1: try {
                                c.CC(refreshedA, 0, 0, newDir);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            break;
                    case -1: try {
                                c.CC(refreshedB, 0, 0, dir);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            break;
                    case 2: try {
                                c.CC(refreshedA, 0, 0, newDir);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            break;
                    case -2: delete(refreshedB, refreshedA);
                            break;
                    case 3: try {
                                c.CC(refreshedB, 0, 0, dir);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                            break;
                    case -3: delete(refreshedA, refreshedB);
                            break;
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!dir.exists()) {
                    System.out.println("Source folder deleted. Exiting loop.");
                    break;
                }
                if (!newDir.exists()) {
                    System.out.println("Destination folder deleted. Exiting loop.");
                    break;
                }
            }
        }
    }

    public static void main(String[] args) {
        Copy c = new Copy("test");
        Thread t = new Thread(c);
        t.start();
    }
}