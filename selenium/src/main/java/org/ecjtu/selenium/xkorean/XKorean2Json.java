package org.ecjtu.selenium.xkorean;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.ecjtu.selenium.SeleniumEngine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.util.List;

public class XKorean2Json {

    public static void main(String[] args) {
        SeleniumEngine.initEngine(SeleniumEngine.DRIVE_PATH);
        List<XKoreanModel> xkoreanModels;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(".\\res\\xkorean"))) {
            xkoreanModels = (List<XKoreanModel>) in.readObject();
            JsonArray jsonArray = new JsonArray();
            for (XKoreanModel model : xkoreanModels) {
                JsonObject obj = new JsonObject();
                obj.addProperty("videoUrl", model.videoUrl);
                obj.addProperty("imageUrl", model.imageUrl);
                obj.addProperty("innerVideoUrl", model.innerVideoUrl);
                obj.addProperty("realUrl", model.realUrl);
                obj.addProperty("title", model.title);
                jsonArray.add(obj);
            }
            FileOutputStream output = new FileOutputStream(new File(".\\res\\xkorean.json"));
            output.write(jsonArray.toString().getBytes("utf-8"));
            output.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
