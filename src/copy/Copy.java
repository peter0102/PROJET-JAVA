package copy;
import java.io.*;
public class Copy {
    public static void main(String args[]) throws FileNotFoundException { 
            File source = new File("C:\\Users\\LINPa\\Documents\\test\\test.txt");
            File destination = new File("C:\\Users\\LINPa\\Documents\\test\\test2.txt");
            try {
                FileInputStream in= new FileInputStream(source);
                FileOutputStream out= new FileOutputStream(destination);
                byte[] buffer= new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
                in.close();
                out.close();
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }

    }
}