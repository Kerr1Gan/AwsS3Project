package org.ecjtu.selenium.ofo91;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class OfO912Json {

    public static void main(String[] args) {
        Map<String, List<OfO91Model>> map = null;
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream("res\\ofo91"))) {
            map = (Map<String, List<OfO91Model>>) is.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONArray jsonArray = new JSONArray();
        if (map != null) {
            Set<Map.Entry<String, List<OfO91Model>>> entrySet = map.entrySet();
            loop:
            for (Map.Entry<String, List<OfO91Model>> entry : entrySet) {
                List<OfO91Model> ofoList = entry.getValue();
                JSONObject jItem = new JSONObject();
                JSONArray jArray = new JSONArray();
                for (OfO91Model model : ofoList) {
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("title", model.getTitle());
                        obj.put("imageUrl", model.getImageUrl());
                        obj.put("videoUrl", model.getVideoUrl());
                        obj.put("innerVideoUrl", model.getInnerVideoUrl());
                        jArray.put(obj);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    jItem.put("title", entry.getKey());
                    jItem.put("array", jArray);
                    jsonArray.put(jItem);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            try (FileWriter writer = new FileWriter("res\\ofo91.json")) {
                writer.write(jsonArray.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
