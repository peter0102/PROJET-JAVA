package copy;
import java.io.*;
import java.nio.file.*;
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

    public boolean check(File[] a, File[] b) {
        if (a == null || b == null) {
            return false;
        }
        if (a.length != b.length) {
            return false;
        }
        for (int i=0; i<a.length;i++) {
            if (a[i].isDirectory() && b[i].isDirectory()) {
                if (!check(a[i].listFiles(), b[i].listFiles())) {
                    return false;
                }
            }
        }
        return true;
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
            try {
                c.CC(a, 0, 0, newDir);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            while (true) {
                File[] refreshedA = dir.listFiles();
                File[] refreshedB = newDir.listFiles();
                delete(refreshedA, refreshedB);
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