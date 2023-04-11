package sync;
import java.io.*;
import java.nio.file.*;
public class Sync implements Runnable {
    private String sourceFolder;
    private String destinationFolder;
    private static boolean isActive;
    public Sync(String sourceFolder, String destinationFolder) {
        this.sourceFolder = sourceFolder;
        this.destinationFolder=destinationFolder;
        this.isActive=true;
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
    public int check(File x, File y) {
        File[] a=x.listFiles();
        File[] b=y.listFiles();
        if (a == null && b == null || a.length == 0 && b.length == 0) {
            return 0;
        }
        if (a==null || a.length==0) {
            if (b[0].getParentFile().lastModified() > x.lastModified()) {
                return 2; //addition de fichier dans b, on copie b dans a
            } else {
                return -2; //supression de fichier dans a, on supprime dans b
            }
        }
        if (b==null || b.length==0) {
            if (a[0].getParentFile().lastModified() > y.lastModified()) {
                return 1; //addition de fichier dans a, on copie a dans b
            } else {
                return -1; //supression de fichier dans b, on supprime dans a
            }
        }
        if (a.length > b.length) { //addition dans a ou supression dans b, on compare date modification dossier a et b
            if (a[0].getParentFile().lastModified() > b[0].getParentFile().lastModified()) {
                return 1; //addition de fichier dans a, on copie a dans b
            } else {
                return -1; //supression de fichier dans b, on supprime dans a
            }
        }
        if (b.length > a.length) { //addition dans b ou supression dans a, on compare date modification dossier a et b
            if (b[0].getParentFile().lastModified() > a[0].getParentFile().lastModified()) {
                return 2; //addition de fichier dans b, on copie b dans a
            } else {
                return -2; //supression de fichier dans a, on supprime dans b
            }
        }
        if (a.length == b.length) { //modification fichier ou dans sous dossiers
            int result = 0;
            for (int i = 0; i < a.length; i++) {
                if (a[i].isFile() && b[i].isFile()) {
                    if (a[i].lastModified() != b[i].lastModified()) {
                        if (a[i].lastModified() > b[i].lastModified()) {
                            return 3; //a est plus récent, on copie a dans b
                        } else {
                            return -3; //b est plus récent, on copie b dans a
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
    public static void stopSync() { //méthode pour interrompre la boucle
        isActive = false;
    }
    public void run() {
        File dir = new File(sourceFolder);
        if (dir.exists() && dir.isDirectory()) {
            File[] a = dir.listFiles();
            Sync c = new Sync(sourceFolder,destinationFolder);
            File newDir = new File(destinationFolder + "\\" + new File(sourceFolder).getName());
            if (newDir.exists()) {
                newDir = new File(destinationFolder + File.separator + new File(sourceFolder).getName() + "_copy");
            }
            if (!newDir.exists()) {
                newDir.mkdir();
            }
            try { //1ère copie du dossier source vers le dossier destination
                c.CC(a, 0, 0, newDir);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            while (isActive) {
                File[] refreshedA = dir.listFiles();
                File[] refreshedB = newDir.listFiles();
                int result = check(dir, newDir);
                switch (result) {
                    case 1: try {
                            c.CC(refreshedA, 0, 0, newDir);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                    case -1: delete(refreshedB,refreshedA);
                        break;
                    case 2: try {
                            c.CC(refreshedB, 0, 0, dir);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                    case -2: delete(refreshedA,refreshedB);
                        break;
                    case 3: try {
                            c.CC(refreshedA, 0, 0, newDir);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        break;
                    case -3: try {
                            c.CC(refreshedB, 0, 0, dir);
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
                    break;
                }
                if (!newDir.exists()) {
                    System.out.println("Destination folder deleted. Exiting loop.");
                    break;
                }
                if (!isActive) {
                	break;
                }
            }
        }
    }

    public static void main(String[] args) {
    	if (args.length!=2) {
    		System.out.println("Error, 2 arguments are needed, source folder and destination foler");
    		System.exit(1);
    	}
        Sync c = new Sync(args[0],args[1]);
        Thread thread = new Thread(c);
        thread.start();
    }
}