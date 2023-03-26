package copy;
import java.io.*;
public class Copy {
    public static void main(String[] args) throws FileNotFoundException {
        File folder = new File("C:\\Users\\LINPa\\Documents\\test");
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                File newFolder = new File("C:\\Users\\LINPa\\Documents\\test2");
                if (!newFolder.exists()) {
                    newFolder.mkdir();
                }
                for (File file : folder.listFiles()) {
                    try {
                        FileInputStream in = new FileInputStream(file.getPath());
                        FileOutputStream out = new FileOutputStream(newFolder.getPath() + "\\" + file.getName());
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
            }
        } else {
            throw new FileNotFoundException("Folder does not exist or is not a directory");
        }
    }
}