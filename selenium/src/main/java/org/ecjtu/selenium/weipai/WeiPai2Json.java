package org.ecjtu.selenium.weipai;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WeiPai2Json {
    public static void main(String[] args) {
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(new File("res\\weipai")))) {
            Map<String, List<WeiPaiModel>> saved = (LinkedHashMap<String, List<WeiPaiModel>>) is.readObject();
            if (saved != null) {
                JSONArray jArray = new JSONArray();
                for (Map.Entry<String, List<WeiPaiModel>> entry : saved.entrySet()) {
                    String key = entry.getKey();
                    List<WeiPaiModel> value = entry.getValue();
                    JSONObject jObj = new JSONObject();
                    jObj.put("key", key);

                    JSONArray itemList = new JSONArray();
                    for (WeiPaiModel model : value) {
                        JSONObject item = new JSONObject();
                        item.put("title", model.getTitle());
                        item.put("href", model.getHref());
                        item.put("imgUrl", model.getImgUrl());
                        item.put("videoUrl", model.getVideoUrl());
                        item.put("videoImageUrl", model.getVideoImageUrl());
                        itemList.put(item);
                    }
                    jObj.put("array", itemList);
                    jArray.put(jObj);
                }
                try (FileOutputStream os = new FileOutputStream(new File("res\\WeiPai.json"))) {
                    os.write(jArray.toString().getBytes("utf-8"));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
