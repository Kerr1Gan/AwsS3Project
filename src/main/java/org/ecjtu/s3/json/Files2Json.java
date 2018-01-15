package org.ecjtu.s3.json;

import com.amazonaws.util.json.JSONArray;
import com.amazonaws.util.json.JSONException;
import com.amazonaws.util.json.JSONObject;
import org.ecjtu.s3.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;

public class Files2Json {

    private static final FilenameFilter sFilter = (dir, name) -> {
        File file = new File(dir, name);
        long length = file.length();
        if (file.isDirectory() || (name.endsWith(".mp4") && length > 1024 * 1024 * 50)) {
            return true;
        }
        return false;
    };

    public static void main(String[] args) throws JSONException {
        File file = new File(Constants.RES_PATH, "files.json");
        File uploadDir = new File("C:\\Users\\KerriGan\\Desktop");
        List<File> allFiles = getFiles(uploadDir, null);

        JSONArray array = new JSONArray();
        for (File item : allFiles) {
            JSONObject obj = new JSONObject();
            obj.put("name", item.getName());
            obj.put("url", item.getAbsolutePath());
            array.put(obj);
        }

        try (FileOutputStream os = new FileOutputStream(file)) {
            os.write(array.toString().getBytes());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        file = new File(Constants.RES_PATH, "tmp");
        file.delete();
    }

    public static List<File> getFiles(File f, List<File> ret) {
        if (ret == null) {
            ret = new ArrayList<>();
        }

        if (!f.isDirectory()) {
            ret.add(f);
        } else {
            File[] arr = f.listFiles(sFilter);
            if (arr != null) {
                for (int i = 0; i < arr.length; i++) {
                    if (!arr[i].isDirectory()) {
                        ret.add(arr[i]);
                    } else {
                        getFiles(arr[i], ret);
                    }
                }
            }
        }
        return ret;
    }

}
