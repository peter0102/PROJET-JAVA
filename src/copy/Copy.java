package copy;
import java.io.*;
public class Copy {
    public void CC(File[] a, int i, int lvl, File dir) throws FileNotFoundException {
        if (i==a.length) {
            return;
        }
        if (a[i].isFile()) {
            try {
                FileInputStream in = new FileInputStream(a[i].getPath());
                FileOutputStream out = new FileOutputStream(dir.getPath() + "\\" + a[i].getName());
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (a[i].isDirectory()) {
            File newDir = new File(dir.getPath() + "\\" + a[i].getName());
            if (!newDir.exists()) {
                newDir.mkdir();
            }
            CC(a[i].listFiles(), 0, lvl+1, newDir);
        }
        CC(a, i+1, lvl, dir);
    }
    public static void main(String name) throws FileNotFoundException {
        File dir = new File("C:\\Users\\LINPa\\Documents\\" + name);
        if (dir.exists() && dir.isDirectory()) {
            File[] a = dir.listFiles();
            Copy c = new Copy();
            File newDir = new File("C:\\Users\\LINPa\\Documents\\" + dir.getName() + "_copy");
            if (!newDir.exists()) {
                newDir.mkdir();
            }
            c.CC(a, 0, 0, newDir);
        }
        else {
            System.out.println("Directory does not exist");
        }
    }
}