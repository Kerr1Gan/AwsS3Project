package org.ecjtu.htmlunit;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class V332Json {

    private static Map<String, List<V33Model>> sMap;

    public static void main(String[] args) {
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(new File(".\\res\\v33a")))) {
            sMap = (Map<String, List<V33Model>>) is.readObject();
        } catch (Exception e) {
        } finally {
            if (sMap == null) {
                sMap = new LinkedHashMap<>();
            }
        }

        try (FileWriter writer = new FileWriter(".\\res\\v33a.json")) {
            JSONArray jObj = new JSONArray();
            Set<Map.Entry<String, List<V33Model>>> entries = sMap.entrySet();
            for (Map.Entry<String, List<V33Model>> entry : entries) {
                JSONObject jTitle = new JSONObject();
                JSONArray jArr = new JSONArray();
                for (V33Model model : entry.getValue()) {
                    JSONObject jArrItem = new JSONObject();
                    jArrItem.put("baseUrl", model.getBaseUrl());
                    jArrItem.put("imageUrl", model.getImageUrl());
                    jArrItem.put("title", model.getTitle());
//                    jArrItem.put("others",model.getOthers());
                    jArrItem.put("videoUrl", model.getVideoUrl());
                    jArr.put(jArrItem);
                }
                jTitle.put("title",entry.getKey());
                jTitle.put("list",jArr);
                jObj.put(jTitle);
            }
            writer.write(jObj.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
