import java.io.File;
import java.io.IOException;

public class FileCreator {
    public static void main(String[] args) {
        File f = new File("C:\\Users\\ethan_xiang\\.aws");
        if (!f.isDirectory()) {
            f.mkdirs();
        }
        File tmp = new File("test");
        try {
            tmp.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
